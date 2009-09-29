/**
 * 
 */
package com.ecmdeveloper.plugin.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.wizards.NewClassCreationWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Ricardo.Belfor
 *
 */
public class NewEventActionClassWizard extends Wizard implements INewWizard {

	protected NewEventActionClassTypePage newEventActionTypePage;
	protected IStructuredSelection selection;
	
	@Override
	public void addPages() {
	
		newEventActionTypePage = new NewEventActionClassTypePage(); 
		addPage( newEventActionTypePage );
		newEventActionTypePage.init( selection );
	}

	
	@Override
	public boolean performFinish() {
		
		try {
			newEventActionTypePage.createType(null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
	
		this.selection = selection;
		
	}
	
	

}
