/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.search.ui;

import java.security.acl.LastOwnerException;
import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.GraphicalQueryEditor;
import com.ecmdeveloper.plugin.search.editor.QueryEditorInput;
import com.ecmdeveloper.plugin.search.editor.QueryFileInfo;
import com.ecmdeveloper.plugin.search.editor.QueryFileStore;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class SearchPage extends DialogPage implements ISearchPage {

	private static final String DATE_MODIFIED_FMT = "Date Modified: {0}";
	private static final String SQL_FMT = "SQL: {0}";
	private Text expressionText;
	private QueryFileStore queryFileStore = new QueryFileStore();
	private Button newQueryButton;
	private Button executeQueryButton;
	private Button modifyQueryButton;
	private Label dateModifiedLabel;
	private ComboViewer queriesCombo;
	private Label sqlLabel;

	public SearchPage() {
	}

	public SearchPage(String title) {
		super(title);
	}

	public SearchPage(String title, ImageDescriptor image) {
		super(title, image);
	}

/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchPage#performAction()
	 */
	@Override
	public boolean performAction() {
		if ( newQueryButton.getSelection() ) {
			return designNewQueryAction();
		}
		return false;
	}

	private boolean designNewQueryAction() {
		try {
			IEditorInput input = new QueryEditorInput();
			String editorId = GraphicalQueryEditor.ID;
			IWorkbenchPage activePage = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
			activePage.openEditor( input, editorId );
			return true;
		} catch (PartInitException e) {
			PluginMessage.openError( getShell(), getTitle(), e.getLocalizedMessage(), e);
		}
		return false;
	}

	@Override
	public void setContainer(ISearchPageContainer container) {
	}

	@Override
	public void createControl(Composite parent) {
		Composite root = createRoot(parent);
		
//        Label expressionLabel = new Label(root, SWT.NONE);
//        expressionLabel.setText("Expression");
//        expressionText = new Text(root, SWT.BORDER);
//        expressionText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
//                        true, true));
		createActionsGroup(root);
//		group.setLayoutData(getFullRowGridData() );
////	addLabel(root,"Select the desired search action:" ); 
		Group group = new Group(root, SWT.SHADOW_IN );
		createStoreQueriesGroup(group);
        setControl(root);
	}

	private void createStoreQueriesGroup(Group group) {
		group.setText( "Stored queries" );
		GridData fullRowGridData = getFullRowGridData();
		fullRowGridData.grabExcessVerticalSpace = true;
		group.setLayoutData(fullRowGridData );
		group.setLayout( new RowLayout( SWT.VERTICAL) );
		createQueriesCombo(group);
		dateModifiedLabel = addLabel(group, MessageFormat.format( DATE_MODIFIED_FMT, "-" ) );
		sqlLabel = addLabel(group, MessageFormat.format( SQL_FMT, "-" ) );
		expressionText = new Text(group, SWT.BORDER | SWT.MULTI );
//		expressionText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));
	}

	private void createActionsGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN  );
		group.setText("Select the desired search action");
		group.setLayout( new RowLayout( SWT.VERTICAL) );
		group.setLayoutData(getFullRowGridData() );
		newQueryButton = createContentRadioButton(group, "&Design New Query");
		modifyQueryButton = createContentRadioButton(group, "&Modify Existing Query");
		executeQueryButton = createContentRadioButton(group, "&Execute Existing Query");
	}

	private Composite createRoot(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
        root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        root.setLayout(layout);
		return root;
	}

	private Button createContentRadioButton(Composite container, String text) {
		Button contentRadioButton = new Button(container, SWT.RADIO);
		contentRadioButton.setText(text);
//		contentRadioButton.setLayoutData(getFullRowGridData());
		contentRadioButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//updateButtons();
			}
		});
		
		return contentRadioButton;
	}

	private void createQueriesCombo(Composite container) {

		queriesCombo = new ComboViewer(container, SWT.VERTICAL
				| SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		queriesCombo.setContentProvider(new ArrayContentProvider());
		queriesCombo.setLabelProvider(new LabelProvider());
		queriesCombo.setInput( queryFileStore.getQueryNames() );
//		connectionsCombo.getCombo().setLayoutData(
//				new GridData(GridData.FILL_HORIZONTAL));
//		connectionsCombo.getCombo().setEnabled(!connections.isEmpty());
		
		queriesCombo
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) queriesCombo.getSelection();
						if ( !selection.isEmpty() ) {
							QueryFileInfo queryInfo = (QueryFileInfo) selection.iterator().next();
							dateModifiedLabel.setText(MessageFormat.format(DATE_MODIFIED_FMT, queryInfo
							.getLastModified().toString()));
							dateModifiedLabel.pack();
							sqlLabel.setText( MessageFormat.format( SQL_FMT, queryInfo.getSql() ) );
							sqlLabel.pack(true);
						} else {
							dateModifiedLabel.setText( MessageFormat.format( DATE_MODIFIED_FMT, "-" ) );
							sqlLabel.setText( MessageFormat.format( SQL_FMT, "-" ) );
						}
//						updateConnectButton();
//						updatePageComplete();					
					}
				});
	}
	
	private GridData getFullRowGridData() {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		return gd;
	}

	private Label addLabel(Composite container, String text) {
		Label label = new Label(container, SWT.FILL);
//		GridData gridData = new GridData(GridData.BEGINNING);
//		label.setLayoutData(gridData);
		label.setText(text);
		return label;
	}
}
