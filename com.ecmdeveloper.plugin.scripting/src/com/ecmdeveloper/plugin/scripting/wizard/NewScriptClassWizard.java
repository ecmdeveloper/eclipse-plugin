/**
 * Copyright 2012, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.scripting.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.ecmdeveloper.plugin.scripting.util.PluginLog;

/**
 * @author ricardo.belfor
 *
 */
public abstract class NewScriptClassWizard extends BasicNewResourceWizard {
	
	protected NewScriptClassTypePage newClassTypePage;
	private ScriptMethodWizardPage scriptMethodWizardPage;

	@Override
	public void addPages() {
	
		newClassTypePage = new NewScriptClassTypePage();
		addPage( newClassTypePage );
		
		scriptMethodWizardPage = getScriptMethodWizardPage();
		addPage(scriptMethodWizardPage);
	
		newClassTypePage.init( getSelection() );
	}

	protected abstract ScriptMethodWizardPage getScriptMethodWizardPage();

	@Override
	public boolean performFinish() {
		
		try {
			newClassTypePage.setMethodCode( scriptMethodWizardPage.getMethodCode( newClassTypePage.isAddComments() ) );
			newClassTypePage.setImports( scriptMethodWizardPage.getImports() );
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
		final Display display = getShell().getDisplay();

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
