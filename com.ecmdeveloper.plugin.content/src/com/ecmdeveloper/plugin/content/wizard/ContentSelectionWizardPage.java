/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.content.wizard;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.content.util.IconFiles;

/**
 * @author Ricardo.Belfor
 *
 */
public class ContentSelectionWizardPage extends WizardPage {

	private static final String SELECT_WORKSPACE_FILE_MESSAGE = "Select a file:";
	private static final String REMOVE_LABEL = "Remove";
	private static final String MIME_TYPE_LABEL_TEXT = "Document Mime Type:";
	private static final String PAGE_DESCRIPTION_MESSAGE = "Select the files for the content of document \"{0}\"";
	private static final String PAGE_TITLE = "Select Document Content";
	private static final String ADD_WORKSPACE_FILE_LABEL = "Add Workspace File...";
	private static final String ADD_EXTERNAL_FILE_LABEL = "Add External File...";

	private TableViewer contentTable;
	private Button addWorkspaceFileButton;
	private Button addExternalFileButton;
	private Button removeButton;
	private Text mimeTypeText;
	
	private ArrayList<Object> content;
	
	protected ContentSelectionWizardPage(String documentName ) {
		super("contentSelectionWizardPage");
		
		setTitle( PAGE_TITLE );
		setDescription( MessageFormat.format( PAGE_DESCRIPTION_MESSAGE, documentName ) );
		
		content = new ArrayList<Object>();
	}

	public ArrayList<Object> getContent() {
		return content;
	}

	public void setContent(ArrayList<Object> content) {
		this.content = content;
	}

	public String getMimeType() {
		return mimeTypeText.getText();
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);

		createContentTable(container);
		createButtons(container);
		createMimeTypeInput(container);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		contentTable.setInput( content );
		updateMimeType();
	}
	
	private void createMimeTypeInput(Composite container) {
		
		Composite composite = new Composite(container, SWT.BEGINNING );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL) );
		
		addLabel(composite, MIME_TYPE_LABEL_TEXT ); 
		mimeTypeText = new Text(composite, SWT.BORDER);
		mimeTypeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void addLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
	}
	
	private void createButtons(Composite container) {
		Composite composite = new Composite(container, SWT.NULL );
		composite.setLayoutData( new GridData( GridData.BEGINNING ) );
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;
		composite.setLayout(gridLayout2);

		createAddWorkspaceFileButton(composite);
		createAddExternalFileButton(composite);
		createRemoveButton(composite);
	}

	private void createAddExternalFileButton(Composite container) {
		
		addExternalFileButton = new Button(container, SWT.PUSH);
		addExternalFileButton.setText(ADD_EXTERNAL_FILE_LABEL);
		addExternalFileButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectExternalFile();
			}
		});
	}

	protected void selectExternalFile() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
		fileDialog.setText( "Select a file" );
		String filename = fileDialog.open();
		if ( filename != null ) {
			content.add(  new File( filename) );
			updateMimeType();
			contentTable.refresh();
		}
	}

	private void createAddWorkspaceFileButton(Composite container) {

		addWorkspaceFileButton = new Button(container, SWT.PUSH);
		addWorkspaceFileButton.setText(ADD_WORKSPACE_FILE_LABEL);
		addWorkspaceFileButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectWorkspaceFile();
			}
		});
	}

	private void createRemoveButton(Composite container) {

		removeButton = new Button(container, SWT.PUSH);
		removeButton.setText(REMOVE_LABEL);
		removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelection();
			}
		});
	}
	
	private void createContentTable(Composite container) {
		contentTable = new TableViewer(container, SWT.BORDER);
		contentTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		contentTable.setLabelProvider( new TableLabelProvider() );
		contentTable.setContentProvider( new TableContentProvider() );
	}
	
	private void selectWorkspaceFile() {
		
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog( getShell(),
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle( PAGE_TITLE );
		dialog.setMessage(SELECT_WORKSPACE_FILE_MESSAGE);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		if ( dialog.open() == Window.OK ) {
			Object result = dialog.getFirstResult();
			if ( result instanceof IFile ) {
				
				content.add((IFile) result);
				updateMimeType();
				contentTable.refresh();
			}
		}
	}

	private void updateMimeType() {
		
		String contentType = getFirstContentElementType();
		if ( contentType != null ) {
			mimeTypeText.setText( contentType );
		} else {
			mimeTypeText.setText("");
		}
	}

	private String getFirstContentElementType() {
		String contentType = null;
		
		if ( content.size() > 0 ) {
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String filename = getContentFilename( content.get(0) );
			contentType = fileNameMap.getContentTypeFor( filename );
		}
		return contentType;
	}

	private String getContentFilename(Object file) {
		String filename = null;
		if ( file instanceof IFile ) {
			filename = ((IFile)file).getName();
		} else if (file instanceof File ) {
			filename = ((File)file).getName();
			
		}
		return filename;
	}
	private void removeSelection() {
		IStructuredSelection selection = (IStructuredSelection) contentTable.getSelection();
		Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext() ) {
			Object element = iterator.next();
			if ( element instanceof ExternalFileAdapter ) {
				content.remove( ((ExternalFileAdapter)element).file );
			} else {
				content.remove( element );
			}
		}
		updateMimeType();
		contentTable.refresh();
	}
	
	class TableLabelProvider extends WorkbenchLabelProvider {

		@Override
		protected String decorateText(String input, Object element) {
			if ( element instanceof IFileStore ) {
				return ((IFileStore)element).toString();
			} else if ( element instanceof IFile ) {
				return ((IFile)element).getFullPath().toString();
			}
			return super.decorateText(input, element);
		}
	}

	class TableContentProvider extends ArrayContentProvider 
	{
		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object inputElement) {
			
			ArrayList<Object> wrappedContent = new ArrayList<Object>();
			for ( Object element : (Collection) inputElement ) {
				if ( element instanceof File ) {
					wrappedContent.add( new ExternalFileAdapter( (File) element ) );
				} else {
					wrappedContent.add( element );
				}
			}
			return wrappedContent.toArray();
		}
	}
	
	class ExternalFileAdapter implements IWorkbenchAdapter {

		private File file;
		
		public ExternalFileAdapter(File file) {
			this.file = file;
		}

		@Override
		public Object[] getChildren(Object o) {
			return null;
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object) {
			return Activator.getImageDescriptor( IconFiles.ICON_EXTERNAL_FILE );
		}

		@Override
		public String getLabel(Object o) {
			return file.getAbsolutePath();
		}

		@Override
		public Object getParent(Object o) {
			return null;
		}
	}
}
