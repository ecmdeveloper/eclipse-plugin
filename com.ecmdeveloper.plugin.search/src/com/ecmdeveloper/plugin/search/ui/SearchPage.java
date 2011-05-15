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

import java.text.MessageFormat;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.GraphicalQueryEditor;
import com.ecmdeveloper.plugin.search.editor.QueryEditorInput;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.store.QueryFileInfo;
import com.ecmdeveloper.plugin.search.store.QueryFileStore;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class SearchPage extends DialogPage implements ISearchPage {

	private static final String DATE_MODIFIED_FMT = "Date Modified: {0}";
	private static final String SQL_FMT = "SQL: {0}";
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

	@Override
	public boolean performAction() {
		if ( newQueryButton.getSelection() ) {
			return openQueryEditor( new Query() );
		} else if ( modifyQueryButton.getSelection() ) {
			QueryFileInfo queryFileInfo = getSelectedQuery();
			try {
				Query query = queryFileStore.load( queryFileInfo.getName() );
				return openQueryEditor( query );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ( executeQueryButton.getSelection() ) {
			
		}
		return false;
	}

	private boolean openQueryEditor(Query query) {
		try {
			IEditorInput input = new QueryEditorInput(query);
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
		createActionsGroup(root);
		createStoreQueriesGroup(root);
        setControl(parent);
	}

	private Composite createRoot(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
	    root.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    GridLayout layout = new GridLayout();
	    root.setLayout(layout);
		return root;
	}

	private void createActionsGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN  );
		group.setText("Select the desired search action");
		RowLayout layout = new RowLayout( SWT.VERTICAL);
		layout.spacing = 7;
		group.setLayout( layout );
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false );
		group.setLayoutData( gd );
		newQueryButton = createActionRadioButton(group, "&Design New Query");
		newQueryButton.setSelection(true);
		modifyQueryButton = createActionRadioButton(group, "&Modify Existing Query");
		executeQueryButton = createActionRadioButton(group, "&Execute Existing Query");
	}

	private Button createActionRadioButton(Composite container, String text) {
		Button button = new Button(container, SWT.RADIO);
		button.setText(text);
		return button;
	}

	private void createStoreQueriesGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_IN );
		group.setText( "Stored queries" );
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true );
		group.setLayoutData(gd);
		group.setLayout( new RowLayout( SWT.VERTICAL) );
		createQueriesCombo(group);
		dateModifiedLabel = addLabel(group, MessageFormat.format( DATE_MODIFIED_FMT, "-" ) );
		sqlLabel = addLabel(group, MessageFormat.format( SQL_FMT, "-" ) );
	}

	private Label addLabel(Composite container, String text) {
		Label label = new Label(container, SWT.FILL);
		label.setText(text);
		return label;
	}

	private void createQueriesCombo(Composite container) {

		queriesCombo = new ComboViewer(container, SWT.VERTICAL
				| SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		queriesCombo.setContentProvider(new ArrayContentProvider());
		queriesCombo.setLabelProvider(new LabelProvider());
		queriesCombo.setInput( queryFileStore.getQueryNames() );
		
		queriesCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateQueryInfo();
			}
		});
	}
	

	private void updateQueryInfo() {
		QueryFileInfo queryInfo = getSelectedQuery();
	
		if ( queryInfo != null ) {
			dateModifiedLabel.setText(MessageFormat.format(DATE_MODIFIED_FMT, queryInfo
					.getLastModified().toString()));
			dateModifiedLabel.pack();
			sqlLabel.setText( MessageFormat.format( SQL_FMT, queryInfo.getSql() ) );
			sqlLabel.pack(true);
		} else {
			dateModifiedLabel.setText( MessageFormat.format( DATE_MODIFIED_FMT, "-" ) );
			sqlLabel.setText( MessageFormat.format( SQL_FMT, "-" ) );
		}
	}

	private QueryFileInfo getSelectedQuery() {
		QueryFileInfo queryInfo = null;
		IStructuredSelection selection = (IStructuredSelection) queriesCombo.getSelection();
		if ( !selection.isEmpty() ) {
			queryInfo = (QueryFileInfo) selection.iterator().next();
		}
		return queryInfo;
	}
}
