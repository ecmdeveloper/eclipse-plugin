package com.ecmdeveloper.plugin.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.filechooser.FileView;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ecmdeveloper.plugin.model.CodeModuleFile;

public class CodeModuleEditorForm extends FormPage {

	private Text nameText;
	private CodeModuleFile codeModuleFile;
	private TableViewer filesTableViewer;
	
	public CodeModuleEditorForm(FormEditor editor) {
		super(editor, "first", "Main");
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {

		ScrolledForm form = managedForm.getForm();
		form.setMessage( "Bla", 2);
		FormToolkit toolkit = managedForm.getToolkit();

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);
		
		createInfoSection( form, toolkit );
		createTableSection(form, toolkit );
	}

	public void refreshFormContent(CodeModuleFile codeModuleFile)
	{
		this.codeModuleFile = codeModuleFile;
		getManagedForm().getForm().setText( "Code Module: " + codeModuleFile.getName() );
		filesTableViewer.setInput( codeModuleFile.getFiles() );
		nameText.setText( codeModuleFile.getName() );
	}

	public void refreshFilesTableContent()
	{
		filesTableViewer.setInput( codeModuleFile.getFiles() );
	}

	private void createInfoSection( ScrolledForm form, FormToolkit toolkit ) {
		
		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );
		
		section.setText("Code Module properties");
		section.setDescription("Specify the properties of this code module");

		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);

		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);

		addLabel(client, "Name:");

		nameText = new Text(client, SWT.BORDER);
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				handleNameModified( nameText.getText() );
			}
		});
		nameText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		
		section.setClient(client);
	}

	private void createTableSection(final ScrolledForm form, FormToolkit toolkit ) {

		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );

		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		client.setLayout(layout);

		GridData gd;

		createFileTable(client);
		createAddButton(toolkit, client);
		createRemoveButton(toolkit, client);
		
		toolkit.paintBordersFor(client);

		
		section.setText("Code Module contents");
		section.setDescription("Specify the Java resources contained in this code module");
		section.setClient(client);

		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
	}

	private void createRemoveButton(FormToolkit toolkit, Composite client) {
		
		GridData gd;
		Button removeButton = toolkit.createButton( client, "Remove", SWT.PUSH);
		
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performRemoveFiles();
			}
		});
		
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		removeButton.setLayoutData(gd);
	}

	private void createAddButton(FormToolkit toolkit, Composite client) {
		
		Button addButton = toolkit.createButton( client, "Add...", SWT.PUSH);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddFile();
			}
		});

		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		addButton.setLayoutData(gd);
	}

	protected void performRemoveFiles() 
	{
		IStructuredSelection selection = (IStructuredSelection) filesTableViewer.getSelection();
		if ( selection.isEmpty() ) {
			return;
		}
		
		Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext() ) {
			codeModuleFile.removeFile( (File) iterator.next() );
		}
	}

	protected void performAddFile() 
	{
		FileDialog dialog = new FileDialog( this.getSite().getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String [] {"*.class", "*.jar", "*.zip", "*.*"} );
//		   dialog.setFilterPath("c:\\temp");
	   String result = dialog.open();
		
		codeModuleFile.addFile( new File( result ) );
	}

	private void handleNameModified( String newName )
	{
		if ( codeModuleFile != null ) {
			codeModuleFile.setName( nameText.getText() );
		}
	}

	private void createFileTable(Composite client) {

		filesTableViewer = new TableViewer(client, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createFilesTableColumns(filesTableViewer);

		GridData gd = new GridData(GridData.FILL_BOTH);
//		gd.heightHint = 100;
//		gd.widthHint = 100;
		gd.verticalSpan = 2;
		
//		viewer.getTable().addListener(SWT.Show, new Listener() {
//			public void handleEvent(Event e) {
//				TableColumn[] cols = viewer.getTable().getColumns();
//				for (int i = 0; i < cols.length; i++)
//					cols[i].pack();
//			}
//		});

		filesTableViewer.getTable().setLayoutData(gd);
		filesTableViewer.setContentProvider(new FilesContentProvider());
		filesTableViewer.setLabelProvider(new FilesLabelProvider());
	}

	private void addLabel(Composite container, String text) {
		final Label label_1 = new Label( container, SWT.NONE );
		final GridData gridData_1 = new GridData(GridData.BEGINNING);
		label_1.setLayoutData(gridData_1);
		label_1.setText(text);
	}

	private void createFilesTableColumns(TableViewer viewer) {

		String[] titles = { "Name", "Last modified", "Path" };
		int[] bounds = { 100, 100, 300 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

//	private Text addTextField(Composite container) {
//		Text textField = new Text(container, SWT.BORDER);
//		textField.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
////				
////				updatePageComplete();
//			}
//		});
//		textField.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
//		
//		return textField;
//	}
	
	class FilesContentProvider implements IStructuredContentProvider
	{
		@SuppressWarnings("unchecked")
		@Override
		public Object[] getElements(Object inputElement) {
			return ((ArrayList<File>)inputElement).toArray();
		}
	
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public class FilesLabelProvider extends LabelProvider implements ITableLabelProvider {

//		// We use icons
//		private static final Image CHECKED = AbstractUIPlugin
//				.imageDescriptorFromPlugin("de.vogella.jface.tableviewer",
//						"icons/checked.gif").createImage();
//		private static final Image UNCHECKED = AbstractUIPlugin
//				.imageDescriptorFromPlugin("de.vogella.jface.tableviewer",
//						"icons/unchecked.gif").createImage();


		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if ( columnIndex == 0 ) {
				String imageKey = ISharedImages.IMG_OBJ_FILE;
				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			File file = (File) element;
			switch ( columnIndex )
			{
			case 0:
				return file.getName();
			case 1: 
				if ( file.exists() ) {
					return new Date( file.lastModified() ).toString();
				}
				else {
					return "";
				}
			case 2:
				return file.getParent();
			}

			return "";
		}
	}
}

//Table table = viewer.getTable();
//table.setHeaderVisible(true);
//table.setLinesVisible(true);
//
//TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
//nameColumn.getColumn().setText( "Name" );
//
//TableViewerColumn lastModifiedColumn = new TableViewerColumn(viewer, SWT.NONE);
//lastModifiedColumn.getColumn().setText( "Last modified" );
//
//TableViewerColumn pathColumn = new TableViewerColumn(viewer, SWT.NONE);
//pathColumn.getColumn().setText( "Path" );
//
//TableColumnLayout layout = new TableColumnLayout();
//client.setLayout( layout );
//
//layout.setColumnData( nameColumn.getColumn(), new ColumnWeightData( 20 ) );
//layout.setColumnData( lastModifiedColumn.getColumn(), new ColumnWeightData( 20 ) ); 		
//layout.setColumnData( pathColumn.getColumn(), new ColumnWeightData( 60 ) ); 		

