package com.ecmdeveloper.plugin.wizard;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.ide.IDE;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ImportCodeModuleWizard  extends Wizard implements IImportWizard {

	private SelectCodeModuleWizardPage selectCodeModuleWizardPage;

	@Override
	public boolean performFinish() {
		openEditor();
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPages() {
		
		selectCodeModuleWizardPage = new SelectCodeModuleWizardPage();
		addPage( selectCodeModuleWizardPage );
	}
	
	private void openEditor()
	{
		File fileToOpen = new File("c:/temp/mynot.codemodule");
		 
		if (fileToOpen.exists() && fileToOpen.isFile()) {
		    IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
		    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		 
		    try {
		        IDE.openEditorOnFileStore( page, fileStore );
		    } catch ( PartInitException e ) {
		        // TODO Put your exception handler here if you wish to
		    }
		}		
	}
}
