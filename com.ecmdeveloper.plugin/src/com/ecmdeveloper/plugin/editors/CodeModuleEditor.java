package com.ecmdeveloper.plugin.editors;

import java.io.File;
import java.io.ObjectInputStream.GetField;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.ecmdeveloper.plugin.model.CodeModuleFile;
import com.ecmdeveloper.plugin.model.CodeModuleFileListener;
import com.ecmdeveloper.plugin.model.CodeModulesManager;
import org.eclipse.core.resources.IFile;

public class CodeModuleEditor extends FormEditor {

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
			codeModuleEditorForm.refreshFilesTableContent();
		}
	};

	@Override
	protected void addPages() {

		try {
			codeModuleEditorForm = new CodeModuleEditorForm( this );
			addPage( codeModuleEditorForm );
			
//			FileStoreEditorInput myFile = (FileStoreEditorInput) getEditorInput();
//	      	String filename = myFile.getURI().getPath();
//			codeModuleFile = CodeModulesManager.getManager().loadCodeModuleFile(filename );

			codeModuleFile = (CodeModuleFile) getEditorInput().getAdapter( CodeModuleFile.class );
			
			codeModuleFile.addPropertyFileListener( propertyFileListener );
			
			updateTitle();
			isPageModified = false;

		} catch (PartInitException e) {
			// 
		}
	}

	@Override
	protected void setActivePage(int pageIndex) {
		// TODO Auto-generated method stub
		super.setActivePage(pageIndex);
		
		codeModuleEditorForm.refreshFormContent(codeModuleFile);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

//		if ( ! (input instanceof FileStoreEditorInput ) )
//	         throw new PartInitException( "Invalid Input: Must be FileStoreEditorInput");
			
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		
		CodeModulesManager.getManager().saveCodeModuleFile(codeModuleFile);
		isPageModified = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
		codeModuleEditorForm.refreshFormContent(codeModuleFile);
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return isPageModified || super.isDirty();
	}

	private void updateTitle()
	{
		IEditorInput editorInput = getEditorInput();

//		setPartName( editorInput.getName() );
//		setTitleToolTip( editorInput.getToolTipText() );
		setPartName( codeModuleFile.getName() );
		setTitleToolTip( "Code Module: " + codeModuleFile.getName() );
//		setPartName( "Code Module: <name>" );
//		setContentDescription( "Edit for code module <name>" );
	}

	public void setCodeModuleFileModified() {
		boolean wasDirty = isDirty();
		isPageModified = true;
		if (!wasDirty) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}
	
}
