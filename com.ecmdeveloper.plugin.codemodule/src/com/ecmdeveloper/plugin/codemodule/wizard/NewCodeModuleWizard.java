/**
 * 
 */
package com.ecmdeveloper.plugin.codemodule.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.model.ObjectStoresManager;

/**
 * @author Ricardo.Belfor
 *
 */
public class NewCodeModuleWizard extends Wizard implements INewWizard {

	private NewCodeModuleWizardPage newCodeModuleWizardPage;

	/**
	 * 
	 */
	public NewCodeModuleWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addPages() {
		
		newCodeModuleWizardPage = new NewCodeModuleWizardPage();
		addPage( newCodeModuleWizardPage );
		setWindowTitle( "New Code Module" );
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}
