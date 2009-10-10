package com.ecmdeveloper.plugin.java.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.ecmdeveloper.plugin.java.util.PluginLog;

public abstract class NewClassWizard extends BasicNewResourceWizard {

	protected NewClassTypePage newClassTypePage;

	@Override
	public boolean performFinish() {
		
		try {
			newClassTypePage.createType(null);
			
			IResource resource= newClassTypePage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				openResource((IFile) resource);
			}	
			
		} catch (CoreException e) {
			PluginLog.error( e );
		} catch (InterruptedException e) {
			PluginLog.error(e);		
		}
		return true;
	}
	
	protected void openResource(final IFile resource) {
		
		final IWorkbenchPage activePage = getWorkbench().getActiveWorkbenchWindow().getActivePage();

		if (activePage == null) {
			return;
		}
		final Display display= getShell().getDisplay();

		if (display == null) {
			return;
		}

		display.asyncExec(new Runnable() {
			public void run() {
				try {
					IDE.openEditor(activePage, resource, true);
				} catch (PartInitException e) {
					PluginLog.error(e);
				}
			}
		});
	}
}