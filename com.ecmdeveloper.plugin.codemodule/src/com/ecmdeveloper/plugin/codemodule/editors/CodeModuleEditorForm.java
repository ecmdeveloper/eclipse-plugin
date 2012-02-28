/**
 * Copyright 2009,2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.codemodule.editors;

import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
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
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.util.IconFiles;
import com.ecmdeveloper.plugin.codemodule.util.Messages;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;
import com.ecmdeveloper.plugin.codemodule.views.JavaElementContentProvider;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class CodeModuleEditorForm extends FormPage {

	private static final String JAVA_ELEMENT_CANNOT_BE_REMOVED_MESSAGE = "The Java element \"{0}\" cannot be removed, try removing the top level parent element.";
	private static final String JAVA_ELEMENT_SELECTION_MESSAGE = "Select a Java Element. If a project or package is selected\n then all the child classes will be \r\ndynamically added to the code module.";
	private static final String JAVA_ELEMENTS_SELECTION_TITLE = "Add Java Elements";
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
	private static final String CREATE_EVENT_ACTION_COMMAND_ID = "com.ecmdeveloper.plugin.createEventAction";
	
	private Text nameText;
	private Label objectStoreLabel;
	private CodeModuleFile codeModuleFile;
	private TreeViewer filesTreeViewer;
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
		createFilesBlock(form, toolkit );
		
		messageManager = managedForm.getMessageManager();
		form.reflow(true);
	}

	private void validateForm()
	{
		validateFormFiles();
		validateFormName();
	}

	private void validateFormName() {
		if ( codeModuleFile.getName().isEmpty() ) {
			messageManager.addMessage( NAME_MESSAGE_KEY, EMPTY_NAME_MESSAGE, null, IMessage.ERROR );
		} else {
			messageManager.removeMessage( NAME_MESSAGE_KEY );
		}
	}

	private void validateFormFiles() {
		try {
			if ( codeModuleFile.isEmpty()   ) {
				messageManager.addMessage(FILES_MESSAGE_KEY, NO_FILES_MESSAGE, null, IMessageProvider.ERROR );
			} else {
				messageManager.removeMessage(FILES_MESSAGE_KEY);
			}
		} catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	public void refreshFormContent(CodeModuleFile codeModuleFile)
	{
		this.codeModuleFile = codeModuleFile;
		getManagedForm().getForm().setText( CODE_MODULE_NAME_PREFIX + codeModuleFile.getName() );
		filesTreeViewer.setInput( codeModuleFile );

		nameText.setText( codeModuleFile.getName() );
		objectStoreLabel.setText(codeModuleFile.getConnectionDisplayName() + ":" //$NON-NLS-1$
				+ codeModuleFile.getObjectStoreDisplayName() );
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
		createAddEventActionLink(toolkit, client );
		//createAddActionLink(toolkit, client);
		
		section.setClient(client);
	}

	private void createAddActionLink(FormToolkit toolkit, Composite client) {
		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText("Add Action");
		link.setImage( Activator.getImage(IconFiles.ICON_ADD_EVENT_ACTION) );
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

	private void createAddEventActionLink(FormToolkit toolkit, Composite client) {

		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText("Create Event Action");
		link.setImage( Activator.getImage(IconFiles.ICON_ADD_EVENT_ACTION) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				try {
					handlerService.executeCommand(CREATE_EVENT_ACTION_COMMAND_ID, null );
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
	
	private void createFilesBlock(final ScrolledForm form, FormToolkit toolkit ) {
		Section section = createFilesSection(form, toolkit);
		Composite client = createFilesSectionClient(toolkit, section);
		createButtons(toolkit, client);
		createFileTree(client);
		toolkit.paintBordersFor(client);
		section.setClient(client);
	}

	private Section createFilesSection(ScrolledForm form, FormToolkit toolkit) {
	
		Section section = toolkit.createSection(form.getBody(),
				Section.DESCRIPTION | Section.TITLE_BAR );
	
		section.setText(CODE_MODULE_FILES_TEXT);
		section.setDescription(CODE_MODULE_FILES_DESCRIPTION);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		section.setLayoutData(gd);
		
		return section;
	}

	private Composite createFilesSectionClient(FormToolkit toolkit, Section section) {
		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		client.setLayout(layout);
		return client;
	}

	private void createButtons(FormToolkit toolkit, Composite client) {
		Composite buttons = new Composite(client, SWT.NONE );
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		fillLayout.spacing = 10;
		buttons.setLayout(fillLayout);

		createAddJavaElementLink(toolkit, buttons);
		createAddFileLink(toolkit, buttons);
		createRemoveContentLink(toolkit, buttons);
	}

	private void createAddJavaElementLink(FormToolkit toolkit, Composite client) {

		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText("Add Java Element");
		link.setImage( Activator.getImage(IconFiles.ICON_ADD_JAVA_ELEMENT) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				performAddJavaElement();
			}
	    });
	}

	private void createAddFileLink(FormToolkit toolkit, Composite client) {
		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText("Add External File");
		link.setImage( Activator.getImage(IconFiles.ICON_ADD_FILE) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				performAddFile();
			}
	    });
	}

	private void createRemoveContentLink(FormToolkit toolkit, Composite client) {
		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText("Remove");
		link.setImage( Activator.getImage(IconFiles.ICON_REMOVE_CONTENT) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				performRemoveFiles();
			}
	    });
	}
	
	protected void performAddJavaElement() {
		ElementTreeSelectionDialog dialog = getJavaElementSelectionDialog();
		int result = dialog.open();
		if ( result == Dialog.OK ) {
			for ( Object object : dialog.getResult() ) {
				codeModuleFile.addJavaElement( (IJavaElement)object );
			}
			validateForm();
		}
	}

	private ElementTreeSelectionDialog getJavaElementSelectionDialog() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getSite().getShell(),
				new JavaElementLabelProvider(), new JavaElementContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot() );
		dialog.setTitle(JAVA_ELEMENTS_SELECTION_TITLE);
		dialog.setMessage(JAVA_ELEMENT_SELECTION_MESSAGE);
		return dialog;
	}

	protected void performRemoveFiles() 
	{
		IStructuredSelection selection = (IStructuredSelection) filesTreeViewer.getSelection();
		if ( selection.isEmpty() ) {
			return;
		}
		
		Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext() ) {
			Object object = iterator.next();
			if ( object instanceof IJavaElement ) {
				IJavaElement javaElement = (IJavaElement) object;
				if ( !codeModuleFile.removeJavaElement(javaElement) ) {
					String message = MessageFormat.format(JAVA_ELEMENT_CANNOT_BE_REMOVED_MESSAGE,
							javaElement.getElementName());
					MessageDialog.openWarning(getSite().getShell(), "Remove", message);
				}
			} else if ( object instanceof File) {
				codeModuleFile.removeFile( (File) object );
			}
		}
		validateForm();
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

	protected void performRefreshFilesTreeViewer() {
		filesTreeViewer.setInput( codeModuleFile );
		validateFormFiles();
	}

	private void handleNameModified( String newName )
	{
		if ( codeModuleFile != null ) {
			codeModuleFile.setName( nameText.getText() );
			validateForm();
		}
	}

	private void createFileTree(Composite client) {

		filesTreeViewer = new TreeViewer(client, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		
		filesTreeViewer.getTree().setLayoutData(gd);
		filesTreeViewer.setContentProvider(new JavaElementContentProvider());
		filesTreeViewer.setLabelProvider(new FilesLabelProvider());
		filesTreeViewer.getTree().setHeaderVisible(true);
		filesTreeViewer.getTree().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				handleFilesTreeDoubleClick();
			}
		});

		createFilesTreeViewerContextMenu(client);
		
		TreeColumn treeColumn1 = new TreeColumn(filesTreeViewer.getTree(), SWT.LEFT );
		treeColumn1.setWidth(300);
		treeColumn1.setText(NAME_COLUMN_LABEL);
		treeColumn1.setResizable(true);

		TreeColumn treeColumn2 = new TreeColumn(filesTreeViewer.getTree(), SWT.LEFT );
		treeColumn2.setWidth(500);
		treeColumn2.setText(PATH_COLUMN_LABEL);
		treeColumn2.setResizable(true);
	}

	private void createFilesTreeViewerContextMenu(Composite client) {
		Menu menu = createFilesTreeViewerMenu(client);
		createFilesTreeViewerOpenMenuItem(menu);
		createFilesTreeViewerAddMenuItem(menu);
		createFilesTreeViewerRemoveMenuItem(menu);
		createFilesTreeViewerRefreshMenuItem(menu);
		filesTreeViewer.getTree().setMenu( menu );
	}

	private Menu createFilesTreeViewerMenu(Composite client) {

		final Menu menu = new Menu(client);
		menu.addMenuListener( new MenuAdapter() {

			@Override
			public void menuShown(MenuEvent e) {
				
		        IStructuredSelection selection = (IStructuredSelection) filesTreeViewer.getSelection();
		        boolean openableSelected = isSelectionOpenable(selection);
				MenuItem item = menu.getItem(0);
				item.setEnabled(openableSelected);
				
				item = menu.getItem(2);
				item.setEnabled( !selection.isEmpty() );
			}

			private boolean isSelectionOpenable(IStructuredSelection selection) {
				Iterator<?> iterator = selection.iterator();
		        boolean openableSelected = !selection.isEmpty();
		        while ( iterator.hasNext() && openableSelected ) {
		        	Object element = iterator.next();
		        	openableSelected = element instanceof ICompilationUnit || element instanceof File;
		        }
				return openableSelected;
			}
		} );
		return menu;
	}
	
	private void createFilesTreeViewerOpenMenuItem(Menu menu) {
		MenuItem menuItem = new MenuItem(menu, SWT.PUSH );
		menuItem.setText("&Open");
		menuItem.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) filesTreeViewer.getSelection();
				openTreeViewerFileEditor(selection);
			}
		} );
	}

	private void createFilesTreeViewerAddMenuItem(Menu menu) {
		MenuItem addSubmenuItem = new MenuItem(menu, SWT.CASCADE);
		addSubmenuItem.setText("Add");
		
		Menu addSubmenu = new Menu(addSubmenuItem);
		addSubmenuItem.setMenu(addSubmenu);

		MenuItem addJavaElementMenuItem = new MenuItem(addSubmenu, SWT.PUSH );
		addJavaElementMenuItem.setText("&Java Element");
		addJavaElementMenuItem.setImage( Activator.getImage( IconFiles.ICON_ADD_JAVA_ELEMENT ) );
		addJavaElementMenuItem.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddJavaElement();
			}
		} );

		MenuItem addFileMenuItem = new MenuItem(addSubmenu, SWT.PUSH );
		addFileMenuItem.setText("&File");
		addFileMenuItem.setImage( Activator.getImage( IconFiles.ICON_ADD_FILE ) );
		addFileMenuItem.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddFile();
			}
		} );
	}

	private void createFilesTreeViewerRemoveMenuItem(Menu menu) {
		MenuItem menuItem = new MenuItem(menu, SWT.PUSH );
		menuItem.setText("&Remove");
		menuItem.setImage( Activator.getImage( IconFiles.ICON_REMOVE_CONTENT ) );
		menuItem.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performRemoveFiles();
			}
		} );
	}

	private void createFilesTreeViewerRefreshMenuItem(Menu menu) {
		MenuItem menuItem = new MenuItem(menu, SWT.PUSH );
		menuItem.setText("R&efresh");
		menuItem.setImage(Activator.getImage( IconFiles.ICON_REFRESH_FILES ) );
		menuItem.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performRefreshFilesTreeViewer();
			}
		} );
	}

	private void addLabel(Composite container, String text) {
		Label label = new Label( container, SWT.NONE );
		GridData gridData_1 = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData_1);
		label.setText(text);
	}

	private void handleFilesTreeDoubleClick() {
		
        IStructuredSelection selection = (IStructuredSelection) filesTreeViewer.getSelection();
        Object element = selection.getFirstElement();

        if (filesTreeViewer.isExpandable(element)) {
        	filesTreeViewer.setExpandedState(element, !filesTreeViewer.getExpandedState(element));
        } else {
    		openTreeViewerFileEditor(selection);
        }
	}

	private void openTreeViewerFileEditor(IStructuredSelection selection) {
		
		Iterator<?> iterator = selection.iterator();
		while ( iterator.hasNext() ) {
			try {
				Object element = iterator.next();
				if ( element instanceof ICompilationUnit) {
					IResource resource = ((IJavaElement)element).getResource();
					if ( resource != null && resource instanceof IFile ) {
						IDE.openEditor(getSite().getPage(), (IFile) resource );
					}
				} else if ( element instanceof File) {
					File file = (File)  element;
					IFileStore store = EFS.getLocalFileSystem().getStore( new Path( file.getPath() ) );
					IDE.openEditorOnFileStore( getSite().getPage(), store);
				}
			} catch (PartInitException e) {
				PluginMessage.openError(getSite().getShell(), "Error", e.getLocalizedMessage(), e);
			}
		}
	}
	
	class FilesLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int PATH_COLUMN_INDEX = 1;
		private static final int NAME_COLUMN_INDEX = 0;
		private static final String EMPTY_STRING = ""; //$NON-NLS-1$
		private JavaElementLabelProvider javaElementLabelProvider;
		
		public FilesLabelProvider() {
			super();
			javaElementLabelProvider = new JavaElementLabelProvider();
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if ( element instanceof File ) {
				return getFileImage(element, columnIndex);
			} else if ( element instanceof IJavaElement ) {
				return getJavaElementImage((IJavaElement) element, columnIndex);
			}
			return null;
		}

		private Image getJavaElementImage(IJavaElement javaElement, int columnIndex) {
			if ( columnIndex == NAME_COLUMN_INDEX ) {
				return javaElementLabelProvider.getImage(javaElement);
			}
			return null;
		}

		private Image getFileImage(Object element, int columnIndex) {
			if ( columnIndex == NAME_COLUMN_INDEX ) {
				return Activator.getImage(IconFiles.ICON_EXTERNAL_FILE);
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if ( element instanceof File) {
				return getFileLabels((File) element, columnIndex);
			} else if ( element instanceof IJavaElement){
				return getJavaElementLabels((IJavaElement) element, columnIndex);
			} else {
				return columnIndex == NAME_COLUMN_INDEX ? element.toString(): EMPTY_STRING;
			}
		}

		private String getJavaElementLabels(IJavaElement javaElement, int columnIndex) {
			if ( columnIndex == NAME_COLUMN_INDEX ) {
				return javaElementLabelProvider.getText(javaElement);
			} if ( columnIndex == PATH_COLUMN_INDEX ) {
				return javaElement.getParent().getPath().toString();
			} else {
				return EMPTY_STRING;
			}
		}

		private String getFileLabels(File file, int columnIndex) {
			if ( columnIndex == NAME_COLUMN_INDEX ) {
				return file.getName();
			} else if ( columnIndex == PATH_COLUMN_INDEX) {
				return file.getParent();
			}
			return EMPTY_STRING;
		}
	}
}
