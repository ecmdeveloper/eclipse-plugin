package com.ecmdeveloper.plugin.wizard;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.editors.CodeModuleEditor;
import com.ecmdeveloper.plugin.editors.CodeModuleEditorInput;
import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.CodeModuleFile;
import com.ecmdeveloper.plugin.model.CodeModulesManager;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.util.PluginLog;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ImportCodeModuleWizard  extends Wizard implements IImportWizard {

	private SelectCodeModuleWizardPage selectCodeModuleWizardPage;
	private ObjectStore objectStore;
	private CodeModule codeModule;
	private	ObjectStoresManager objectStoresManager;
	private IWorkbenchPage activePage;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		objectStoresManager = ObjectStoresManager.getManager();
		activePage = workbench.getActiveWorkbenchWindow().getActivePage();
	}

	@Override
	public void addPages() {
		
		selectCodeModuleWizardPage = new SelectCodeModuleWizardPage();
		addPage( selectCodeModuleWizardPage );
	}

	@Override
	public boolean canFinish() {
		return objectStore != null && codeModule != null;
	}

	@Override
	public boolean performFinish() {
		openEditor();
		return true;
	}

	public ObjectStore getObjectStore() {
		return objectStore;
	}

	public void setObjectStore(ObjectStore objectStore) {
		this.objectStore = objectStore;
	}

	public CodeModule getCodeModule() {
		return codeModule;
	}

	public void setCodeModule(CodeModule codeModule) {
		this.codeModule = codeModule;
		getContainer().updateButtons();
	}

	private void openEditor()
	{
		CodeModulesManager manager = CodeModulesManager.getManager();
		CodeModuleFile codeModuleFile = manager.createCodeModuleFile( codeModule, objectStore);
		IEditorInput input = new CodeModuleEditorInput( codeModuleFile );
		String editorId = CodeModuleEditor.CODE_MODULE_EDITOR_ID;
		try {
			IDE.openEditor( activePage, input, editorId);
		} catch (PartInitException e) {
			PluginLog.error("Open editor failed" , e);
		}
	}

	public Object getObjectStores() {
		return objectStoresManager.getObjectStores().getChildren().toArray();		
	}

	public void connectObjectStore() {
		if ( objectStore != null ) {
			objectStoresManager.connectObjectStore( objectStore );
		}
	}
}
