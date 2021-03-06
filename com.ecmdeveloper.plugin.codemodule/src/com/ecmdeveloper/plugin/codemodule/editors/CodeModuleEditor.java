package com.ecmdeveloper.plugin.codemodule.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFileListener;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManagerEvent;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManagerListener;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;

public class CodeModuleEditor extends FormEditor implements CodeModulesManagerListener {

	private static final String NO_FILES_MESSAGE = "This Code Module does not contain any files. Are you sure you want to save?";

	private static final String TITLE = "Code Module Editor";

	public static final String CODE_MODULE_EDITOR_ID = "com.ecmdeveloper.plugin.editors.codeModuleEditor";

	private CodeModuleFile codeModuleFile;
	private CodeModuleEditorForm codeModuleEditorForm;
	private boolean isPageModified;

	private final CodeModuleFileListener propertyFileListener = new CodeModuleFileListener()
	{
		@Override
		public void nameChanged() {
			setCodeModuleFileModified();
		}

		@Override
		public void filesChanged() {
			setCodeModuleFileModified();
		}
	};

	@Override
	protected void addPages() {

		try {
			codeModuleEditorForm = new CodeModuleEditorForm( this );
			addPage( codeModuleEditorForm );
			
			codeModuleFile = (CodeModuleFile) getEditorInput().getAdapter( CodeModuleFile.class );
			codeModuleFile.addPropertyFileListener( propertyFileListener );
			
			updateTitle();
			isPageModified = false;
			
			CodeModulesManager.getManager().addCodeModuleManagerListener(this);

		} catch (PartInitException e) {
			// 
		}
	}

	@Override
	protected void setActivePage(int pageIndex) {
		super.setActivePage(pageIndex);
		codeModuleEditorForm.refreshFormContent(codeModuleFile);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

		if ( ! (input instanceof CodeModuleEditorInput ) )
	         throw new PartInitException( "Invalid Input: Must be CodeModuleEditorInput");
		
		site.setSelectionProvider( new ISelectionProvider() {

			@Override
			public void addSelectionChangedListener( ISelectionChangedListener listener) {
				// No changing selection
			}

			@Override
			public ISelection getSelection() {
				return new StructuredSelection( getEditorInput().getAdapter( CodeModuleFile.class ) );
			}

			@Override
			public void removeSelectionChangedListener(ISelectionChangedListener listener) {
				// No changing selection
			}

			@Override
			public void setSelection(ISelection selection) {
				// No changing selection
			}
		});
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

		try {

			if ( codeModuleFile.getContentElementFiles().isEmpty() ) {
				if ( !MessageDialog.openConfirm( getSite().getShell(), TITLE, NO_FILES_MESSAGE) ) {
					return;
				}
			}
			
			if ( codeModuleFile.getId() != null ) {
				CodeModulesManager.getManager().saveCodeModuleFile(codeModuleFile);
			} else {
				CodeModulesManager.getManager().saveNewCodeModuleFile(codeModuleFile);
			}
			isPageModified = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
			codeModuleEditorForm.refreshFormContent(codeModuleFile);
		}
		catch (Exception e ) {
			PluginMessage.openError( getSite().getShell(), TITLE, "Save failed", e );
		}
	}

	@Override
	public void doSaveAs() {
		// Not supported
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return isPageModified || codeModuleFile.getId() == null || super.isDirty();
	}

	private void updateTitle() {
		setPartName( codeModuleFile.getName() );
		setTitleToolTip( "Code Module: " + codeModuleFile.getName() );
	}

	public void setCodeModuleFileModified() {
		boolean wasDirty = isDirty();
		isPageModified = true;
		if (!wasDirty) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public void codeModuleFilesItemsChanged(CodeModulesManagerEvent event) {
		
		if ( event.getItemsRemoved() != null ) {
			for ( CodeModuleFile itemRemoved : event.getItemsRemoved() ) {
				if ( itemRemoved.getId().equalsIgnoreCase( codeModuleFile.getId() ) ) {
					close( false );
					return;
				}
			}
		}
	}
}
