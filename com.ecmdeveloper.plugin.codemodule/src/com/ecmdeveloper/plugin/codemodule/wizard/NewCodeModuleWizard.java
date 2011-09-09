/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.codemodule.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditor;
import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditorInput;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.util.Messages;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;
import com.ecmdeveloper.plugin.core.model.IObjectStoresManager;
import com.ecmdeveloper.plugin.model.ObjectStore;

/**
 * @author Ricardo.Belfor
 *
 */
public class NewCodeModuleWizard extends Wizard implements INewWizard {

	private static final String OPEN_EDITOR_FAILED_MESSAGE = Messages.NewCodeModuleWizard_OpenEditorFailedMessage;
	private static final String WIZARD_NAME = Messages.NewCodeModuleWizard_WizardName;

	private NewCodeModuleWizardPage newCodeModuleWizardPage;
	private ObjectStore objectStore;
	private	IObjectStoresManager objectStoresManager;
	private IWorkbenchPage activePage;
	private String name;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		objectStoresManager = Activator.getDefault().getObjectStoresManager();
		activePage = workbench.getActiveWorkbenchWindow().getActivePage();
	}

	@Override
	public void addPages() {
		
		newCodeModuleWizardPage = new NewCodeModuleWizardPage();
		addPage( newCodeModuleWizardPage );
		setWindowTitle( WIZARD_NAME );
	}

	@Override
	public boolean canFinish() {
		return objectStore != null && name != null && ! name.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		openEditor();
		return true;
	}

	private void openEditor()
	{
		CodeModulesManager manager = CodeModulesManager.getManager();
		CodeModuleFile codeModuleFile = manager.createNewCodeModuleFile( objectStore, name );
		IEditorInput input = new CodeModuleEditorInput( codeModuleFile );
		String editorId = CodeModuleEditor.CODE_MODULE_EDITOR_ID;
		try {
			IDE.openEditor( activePage, input, editorId);
		} catch (PartInitException e) {
			PluginMessage.openError(activePage.getWorkbenchWindow().getShell(),
					WIZARD_NAME, OPEN_EDITOR_FAILED_MESSAGE, e);
			PluginLog.error(OPEN_EDITOR_FAILED_MESSAGE , e);
		}
	}
	
	public ObjectStore getObjectStore() {
		return objectStore;
	}

	public void setObjectStore(ObjectStore objectStore) {
		this.objectStore = objectStore;
		getContainer().updateButtons();
	}

	public void connectObjectStore() {
		if ( objectStore != null ) {
			try {
	         getContainer().run(true, false, new IRunnableWithProgress() {
		            public void run(IProgressMonitor monitor) throws InvocationTargetException,
		                  InterruptedException 
					{
		               try {
							objectStoresManager.connectObjectStore(objectStore,
									monitor);
						} catch (ExecutionException e) {
				    	  PluginMessage.openErrorFromThread( getShell(), WIZARD_NAME, e.getLocalizedMessage(), e );
						}
					}
		         });
		      }
		      catch (InvocationTargetException e) {
		    	  PluginMessage.openError(getShell(), WIZARD_NAME, e.getLocalizedMessage(), e );
		      }
		      catch (InterruptedException e) {
		    	  // Should not happen
		      }
		}
	}

	public Object getObjectStores() {
		return objectStoresManager.getObjectStores().getChildren().toArray();		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		getContainer().updateButtons();
	}
}
