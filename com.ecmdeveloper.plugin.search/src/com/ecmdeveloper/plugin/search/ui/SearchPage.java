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

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.WorkbenchException;

import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.GraphicalQueryEditor;
import com.ecmdeveloper.plugin.search.editor.QueryEditorInput;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.store.QueryFileInfo;
import com.ecmdeveloper.plugin.search.store.QueryFileStore;

/**
 * @author ricardo.belfor
 *
 */
public class SearchPage extends DialogPage implements ISearchPage {

	public static final String ID = "com.ecmdeveloper.plugin.search.searchPage";
	
	private static final String PAGE_NAME = "Content Engine Search";
	private static final String DATE_MODIFIED_FMT = "Date Modified: {0}";
	private QueryFileStore queryFileStore = new QueryFileStore();
	private Button newQueryButton;
	private Button executeQueryButton;
	private Button modifyQueryButton;
	private Label dateModifiedLabel;
	private ComboViewer queriesCombo;
//	private Label sqlLabel;

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
			return performModifyQuery();
		} else if ( executeQueryButton.getSelection() ) {
			return performExecuteQuery();
		}
		return false;
	}

	private boolean performModifyQuery() {

		try {
			Query query = getSelectedQuery();
			if ( query == null ) {
				return false;
			}
			return openQueryEditor( query );
		} catch (Exception e) {
			PluginMessage.openError(getShell(), PAGE_NAME, e.getLocalizedMessage(), e);
		}
		return false;
	}

	private Query getSelectedQuery() throws IOException, WorkbenchException {
		QueryFileInfo queryFileInfo = getSelectedQueryFileInfo();
		if ( queryFileInfo == null ) {
			MessageDialog.openError(getShell(), PAGE_NAME, "There is no stored query selected.");
			return null;
		}
		Query query = queryFileStore.load( queryFileInfo.getName() );
		return query;
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

	private boolean performExecuteQuery() {

		try {
			Query query = getSelectedQuery();
			if ( query == null ) {
				return false;
			}
			ISearchQuery searchQuery = new SearchQuery(query);
			NewSearchUI.runQueryInBackground(searchQuery );
			return true;
		} catch (Exception e) {
			PluginMessage.openError(getShell(), PAGE_NAME, e.getLocalizedMessage(), e);
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
		Composite buttonRow = new Composite(parent, SWT.NONE);
		buttonRow.setLayout( new RowLayout( SWT.HORIZONTAL) );
		createDeleteButton(buttonRow);
		createShowSQLButton(buttonRow);
		dateModifiedLabel = addLabel(group, MessageFormat.format( DATE_MODIFIED_FMT, "-" ) );
	}

	private Label addLabel(Composite container, String text) {
		Label label = new Label(container, SWT.FILL);
		label.setText(text);
		return label;
	}

	private void createQueriesCombo(Composite container) {

		queriesCombo = new ComboViewer(container, SWT.VERTICAL
				| SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY | SWT.FILL);
		queriesCombo.setContentProvider(new ArrayContentProvider());
		queriesCombo.setLabelProvider(new LabelProvider());
		queriesCombo.setInput( queryFileStore.getQueryFileInfo() );
		
		queriesCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateQueryInfo();
			}
		});
	}
	
	private void createDeleteButton(Composite container) {
		Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteSelectedQuery();
			}
		});
		button.setText("Delete");
	}

	private void createShowSQLButton(Composite container) {
		Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				QueryFileInfo queryInfo = getSelectedQueryFileInfo();
				if ( queryInfo != null ) {
					showQuery( queryInfo.getSql() );
				}
			}
		});
		button.setText("Show SQL");
	}
	
	protected void deleteSelectedQuery() {
		QueryFileInfo queryInfo = getSelectedQueryFileInfo();
		
		if ( queryInfo != null ) {
			String message = MessageFormat.format("Do you want to delete the query \"{0}\"?",
					queryInfo.getName());
			if ( MessageDialog.openConfirm(getShell(), PAGE_NAME, message ) ) { 
				queryFileStore.delete(queryInfo);
				queriesCombo.setInput( queryFileStore.getQueryFileInfo() );
				updateQueryInfo();
			}
		}
	}

	private void updateQueryInfo() {
		
		QueryFileInfo queryInfo = getSelectedQueryFileInfo();
	
		if ( queryInfo != null ) {
			dateModifiedLabel.setText(MessageFormat.format(DATE_MODIFIED_FMT, queryInfo
					.getLastModified().toString()));
			dateModifiedLabel.pack();
		} else {
			dateModifiedLabel.setText( MessageFormat.format( DATE_MODIFIED_FMT, "-" ) );
		}
		
		modifyQueryButton.setSelection( queryInfo != null && ! executeQueryButton.getSelection() );
		newQueryButton.setSelection( queryInfo == null );
	}

	private QueryFileInfo getSelectedQueryFileInfo() {
		QueryFileInfo queryInfo = null;
		IStructuredSelection selection = (IStructuredSelection) queriesCombo.getSelection();
		if ( !selection.isEmpty() ) {
			queryInfo = (QueryFileInfo) selection.iterator().next();
		}
		return queryInfo;
	}
	
	private void showQuery(String sql) {
		ShowQueryDialog dialog = new ShowQueryDialog(getShell(), sql );
		dialog.create();
		dialog.open();
	}
}
