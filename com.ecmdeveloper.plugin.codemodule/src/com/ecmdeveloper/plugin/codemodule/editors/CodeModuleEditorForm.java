package com.ecmdeveloper.plugin.codemodule.editors;

import java.io.File;
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
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.util.IconFiles;
import com.ecmdeveloper.plugin.codemodule.util.Messages;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class CodeModuleEditorForm extends FormPage {

	private static final String REMOVE_LABEL = Messages.CodeModuleEditorForm_RemoveLabel;
	private static final String ADD_LABEL = Messages.CodeModuleEditorForm_AddLabel;
	private static final String CODE_MODULE_FILES_DESCRIPTION = Messages.CodeModuleEditorForm_CodeModuleFilesDescription;
	private static final String CODE_MODULE_FILES_TEXT = Messages.CodeModuleEditorForm_CodeModuleFilesText;
	private static final String FORM_ID = "first"; //$NON-NLS-1$
	private static final String FORM_TITLE = Messages.CodeModuleEditorForm_FormTitle;
	private static final String PATH_COLUMN_LABEL = Messages.CodeModuleEditorForm_PathColumnLabel;
	private static final String LAST_MODIFIED_COLUMN_LABEL = Messages.CodeModuleEditorForm_LastModifiedColumnLabel;
	private static final String NAME_COLUMN_LABEL = Messages.CodeModuleEditorForm_NameColumnLabel;
	private static final String CODE_MODULE_NAME_PREFIX = Messages.CodeModuleEditorForm_CodeModuleNamePrefix;
	private static final String EMPTY_NAME_MESSAGE = Messages.CodeModuleEditorForm_EmptyNameMessage;
	private static final String NO_FILES_MESSAGE = Messages.CodeModuleEditorForm_NoFilesMessage;
	private static final String OBJECT_STORE_LABEL = Messages.CodeModuleEditorForm_ObjectStoreLabel;
	private static final String NAME_LABEL = Messages.CodeModuleEditorForm_NameLabel;
	private static final String CODE_MODULE_PROPERTIES_DESCRIPTION = Messages.CodeModuleEditorForm_CodeModulePropertiesDescription;
	private static final String CODE_MODULE_PROPERTIES_TITLE = Messages.CodeModuleEditorForm_CodeModulePropertiesTitle;
	private static final String CODE_MODULE_ACTIONS_DESCRIPTION = Messages.CodeModuleEditorForm_CodeModuleActionsDescription;
	private static final String CODE_MODULE_ACTIONS_TITLE = Messages.CodeModuleEditorForm_CodeModuleActionsTitle;
	private static final String UPDATE_CODE_MODULE_LINK_TEXT = Messages.CodeModuleEditorForm_UpdateCodeModuleLinkText;
	private static final String SHOW_CODE_MODULE_ACTIONS_LINK_TEXT = "Show Code Module Actions";
	private static final String NAME_MESSAGE_KEY = "nameMessageKey"; //$NON-NLS-1$
	private static final String FILES_MESSAGE_KEY = "filesMessageKey"; //$NON-NLS-1$
	private static final String ALLOWED_FILES_EXTENSIONS[] = {"*.class", "*.jar", "*.zip"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String UPDATE_CODE_MODULE_COMMAND_ID = "com.ecmdeveloper.plugin.updateCodeModule"; //$NON-NLS-1$
	private static final String SHOW_CODE_MODULES_ACTIONS_COMMAND_ID = "com.ecmdeveloper.plugin.showCodeModuleActions";
	
	private Text nameText;
	private Label objectStoreLabel;
	private CodeModuleFile codeModuleFile;
	private TableViewer filesTableViewer;
	private IMessageManager messageManager;
	
	public CodeModuleEditorForm(FormEditor editor) {
		super(editor, FORM_ID, FORM_TITLE);
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
                HyperlinkSettings.UNDERLINE_ALWAYS);
		
		toolkit.decorateFormHeading(form.getForm());
		form.getForm().addMessageHyperlinkListener(new HyperlinkAdapter());
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		form.getBody().setLayout(layout);
		
		createInfoSection( form, toolkit );
		createActionsSection( form, toolkit );
		createFilesSection(form, toolkit );
		
		messageManager = managedForm.getMessageManager();
		form.reflow(true);
	}

	private void validateForm()
	{
		if ( codeModuleFile.getFiles().isEmpty() ) {
			messageManager.addMessage(FILES_MESSAGE_KEY, NO_FILES_MESSAGE, null, IMessageProvider.ERROR );
		} else {
			messageManager.removeMessage(FILES_MESSAGE_KEY);
		}
		
		if ( codeModuleFile.getName().isEmpty() ) {
			messageManager.addMessage( NAME_MESSAGE_KEY, EMPTY_NAME_MESSAGE, null, IMessage.ERROR );
		} else {
			messageManager.removeMessage( NAME_MESSAGE_KEY );
		}
	}

	public void refreshFormContent(CodeModuleFile codeModuleFile)
	{
		this.codeModuleFile = codeModuleFile;
		getManagedForm().getForm().setText( CODE_MODULE_NAME_PREFIX + codeModuleFile.getName() );
		filesTableViewer.setInput( codeModuleFile.getFiles() );
		nameText.setText( codeModuleFile.getName() );
		objectStoreLabel.setText(codeModuleFile.getConnectionName() + ":" //$NON-NLS-1$
				+ codeModuleFile.getObjectStoreName());
	}

	public void refreshFilesTableContent()
	{
		filesTableViewer.setInput( codeModuleFile.getFiles() );
	}

	private void createInfoSection( ScrolledForm form, FormToolkit toolkit ) {
		
		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );
		
		section.setText(CODE_MODULE_PROPERTIES_TITLE);
		section.setDescription(CODE_MODULE_PROPERTIES_DESCRIPTION);

		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);

		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);

		addLabel(client, NAME_LABEL);

		nameText = new Text(client, SWT.BORDER);
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				handleNameModified( nameText.getText() );
			}
		});
		nameText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );

		addLabel(client, OBJECT_STORE_LABEL);

		objectStoreLabel = new Label( client, SWT.NONE );
		objectStoreLabel.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		//objectStoreLabel.setText("");

		section.setClient(client);
	}

	private void createActionsSection(ScrolledForm form, FormToolkit toolkit )
	{
		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );
		
		section.setText(CODE_MODULE_ACTIONS_TITLE);
		section.setDescription(CODE_MODULE_ACTIONS_DESCRIPTION);

		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);

		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		client.setLayout(layout);

		createUpdateCodeModuleLink(toolkit, client);
		createShowCodeModuleActionsLink(toolkit, client);
		
		section.setClient(client);
	}

	private void createUpdateCodeModuleLink(FormToolkit toolkit,
			Composite client) {

		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText(UPDATE_CODE_MODULE_LINK_TEXT);
		link.setImage( Activator.getImage(IconFiles.ICON_CODEMODULE_UPDATE) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(UPDATE_CODE_MODULE_COMMAND_ID, null );
				} catch (Exception exception) {
					PluginLog.error( exception );
				}
			}
	    });
	}

	private void createShowCodeModuleActionsLink(FormToolkit toolkit,
			Composite client) {

		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText(SHOW_CODE_MODULE_ACTIONS_LINK_TEXT);
		link.setImage( Activator.getImage(IconFiles.ICON_ACTION) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(SHOW_CODE_MODULES_ACTIONS_COMMAND_ID, null );
				} catch (Exception exception) {
					PluginLog.error( exception );
				}
			}
	    });
	}
	
	private void createFilesSection(final ScrolledForm form, FormToolkit toolkit ) {

		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );

		section.setText(CODE_MODULE_FILES_TEXT);
		section.setDescription(CODE_MODULE_FILES_DESCRIPTION);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		section.setLayoutData(gd);
		
		//		ToolBar tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		ToolItem titem = new ToolItem(tbar, SWT.NULL);
//		titem.setImage( Activator.getImage("icons/page_add.png") );
//		titem.setToolTipText("Add File");
//		titem = new ToolItem(tbar, SWT.PUSH);
//		titem.setToolTipText("Remove File");
//		titem.setImage( Activator.getImage("icons/page_delete.png") );
//		section.setTextClient(tbar);
		
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

		section.setClient(client);
	}

	private void createRemoveButton(FormToolkit toolkit, Composite client) {
		
		Button removeButton = toolkit.createButton( client, REMOVE_LABEL, SWT.PUSH);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performRemoveFiles();
			}
		});
	}

	private void createAddButton(FormToolkit toolkit, Composite client) {
		
		Button addButton = toolkit.createButton( client, ADD_LABEL, SWT.PUSH);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddFile();
			}
		});
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
		dialog.setFilterExtensions( ALLOWED_FILES_EXTENSIONS );
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
		Label label = new Label( container, SWT.NONE );
		GridData gridData_1 = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData_1);
		label.setText(text);
	}

	private void createFilesTableColumns(TableViewer viewer) {

		String[] titles = { NAME_COLUMN_LABEL, LAST_MODIFIED_COLUMN_LABEL, PATH_COLUMN_LABEL };
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
		}
	
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	public class FilesLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final String EMPTY_STRING = ""; //$NON-NLS-1$

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
					return EMPTY_STRING;
				}
			case 2:
				return file.getParent();
			}

			return EMPTY_STRING;
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

