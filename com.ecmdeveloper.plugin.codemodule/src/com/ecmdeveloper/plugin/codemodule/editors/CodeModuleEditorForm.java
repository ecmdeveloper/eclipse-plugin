package com.ecmdeveloper.plugin.codemodule.editors;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class CodeModuleEditorForm extends FormPage {

	private static final String NAME_MESSAGE_KEY = "nameMessageKey";
	private static final String FILES_MESSAGE_KEY = "filesMessageKey";
	private Text nameText;
	private Label objectStoreLabel;
	private CodeModuleFile codeModuleFile;
	private TableViewer filesTableViewer;
	private IMessageManager messageManager;
	
	public CodeModuleEditorForm(FormEditor editor) {
		super(editor, "first", "Main");
	}

	@Override
	public void setFocus() {
		nameText.setFocus();
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {

		final ScrolledForm form = managedForm.getForm();
		final FormToolkit toolkit = managedForm.getToolkit();
		toolkit.getHyperlinkGroup().setHyperlinkUnderlineMode(
                HyperlinkSettings.UNDERLINE_HOVER);
		
		toolkit.decorateFormHeading(form.getForm());
		form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter());
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		form.getBody().setLayout(layout);
		
		createInfoSection( form, toolkit );
		createTableSection(form, toolkit );
		
		messageManager = managedForm.getMessageManager();
	}

	private void validateForm()
	{
		if ( codeModuleFile.getFiles().isEmpty() ) {
			messageManager.addMessage(FILES_MESSAGE_KEY, "There are no files selected", null, IMessageProvider.ERROR );
		} else {
			messageManager.removeMessage(FILES_MESSAGE_KEY);
		}
		
		if ( codeModuleFile.getName().isEmpty() ) {
			messageManager.addMessage( NAME_MESSAGE_KEY, "The name is empty", null, IMessage.ERROR );
		} else {
			messageManager.removeMessage( NAME_MESSAGE_KEY );
		}
	}

	public void refreshFormContent(CodeModuleFile codeModuleFile)
	{
		this.codeModuleFile = codeModuleFile;
		getManagedForm().getForm().setText( "Code Module: " + codeModuleFile.getName() );
		filesTableViewer.setInput( codeModuleFile.getFiles() );
		nameText.setText( codeModuleFile.getName() );
		objectStoreLabel.setText(codeModuleFile.getConnectionName() + ":"
				+ codeModuleFile.getObjectStoreName());
	}

	public void refreshFilesTableContent()
	{
		filesTableViewer.setInput( codeModuleFile.getFiles() );
	}

	private void createInfoSection( ScrolledForm form, FormToolkit toolkit ) {
		
		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );
		
		form.setImage( Activator.getImage("icons/script.png") );
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

		addLabel(client, "Object Store:");

		objectStoreLabel = new Label( client, SWT.NONE );
		objectStoreLabel.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		objectStoreLabel.setText("");
		
		section.setClient(client);
	}

	private void createTableSection(final ScrolledForm form, FormToolkit toolkit ) {

		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );

		ToolBar tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		ToolItem titem = new ToolItem(tbar, SWT.NULL);
		titem.setImage( Activator.getImage("icons/page_add.png") );
		titem.setToolTipText("Add File");
		titem = new ToolItem(tbar, SWT.PUSH);
		titem.setToolTipText("Remove File");
		titem.setImage( Activator.getImage("icons/page_delete.png") );

		section.setTextClient(tbar);
		
		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		client.setLayout(layout);

		createFileTable(client);
		
		Composite buttons = new Composite(client, SWT.NONE );
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		buttons.setLayout(fillLayout);
		createAddButton(toolkit, buttons);
		createRemoveButton(toolkit, buttons);
		
		toolkit.paintBordersFor(client);

		section.setText("Code Module contents");
		section.setDescription("Specify the Java resources contained in this code module");
		section.setClient(client);

		section.setLayoutData( new GridData(GridData.FILL_BOTH) );
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
		
//		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
//		removeButton.setLayoutData(gd);
//		removeButton.setImage( Activator.getImage("icons/page_delete.png") );
	}

	private void createAddButton(FormToolkit toolkit, Composite client) {
		
		Button addButton = toolkit.createButton( client, "Add...", SWT.PUSH);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddFile();
			}
		});

//		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING );
//		//gd.grabExcessHorizontalSpace = true;
//		addButton.setLayoutData(gd);
//		addButton.setImage( Activator.getImage("icons/page_add.png") );
		
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
			validateForm();
		}
	}

	protected void performAddFile() 
	{
		FileDialog dialog = new FileDialog( this.getSite().getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String [] {"*.class", "*.jar", "*.zip", "*.*"} );
		String result = dialog.open();
		
		if ( result != null ) {
			codeModuleFile.addFile( new File( result ) );
			validateForm();
		}
	}

	private void handleNameModified( String newName )
	{
		if ( codeModuleFile != null ) {
			codeModuleFile.setName( nameText.getText() );
			validateForm();
		}
	}

	private void createFileTable(Composite client) {

		filesTableViewer = new TableViewer(client, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createFilesTableColumns(filesTableViewer);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.verticalSpan = 1;
		
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

