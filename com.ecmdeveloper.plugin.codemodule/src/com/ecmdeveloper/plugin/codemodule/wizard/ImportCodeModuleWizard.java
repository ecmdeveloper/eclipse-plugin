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
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditor;
import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditorInput;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;
import com.ecmdeveloper.plugin.codemodule.util.PluginMessage;
import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ImportCodeModuleWizard  extends Wizard implements IImportWizard {

	private static final String WIZARD_NAME = "Import Code Module";
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
		setWindowTitle( WIZARD_NAME );
		setNeedsProgressMonitor(true);
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

	public Collection<CodeModule> getCodeModules() {
		
		if ( objectStore != null ) {
			
			NewCodeModulesRunnable runnable = new NewCodeModulesRunnable(objectStore);
			try {
				getContainer().run(true, false, runnable);
			} catch (Exception e ) {
				PluginMessage.openError(getShell(), WIZARD_NAME, e.getLocalizedMessage(), e );
			}
			return runnable.getCodeModules();
		} else {
			return new ArrayList<CodeModule>();
		}
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

		if (objectStore == null) {
			return;
		}
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						objectStoresManager.connectObjectStore(objectStore, monitor);
					} catch (ExecutionException e) {
						PluginMessage.openErrorFromThread(getShell(),
								WIZARD_NAME, e.getLocalizedMessage(), e);
					}
				}
			});
		} catch (InvocationTargetException e) {
			PluginMessage.openError(getShell(), WIZARD_NAME, e .getLocalizedMessage(), e);
		} catch (InterruptedException e) {
			// Should not happen
			PluginLog.error(e);
		}
	}
	
	class NewCodeModulesRunnable implements IRunnableWithProgress {

		private ObjectStore objectStore;
		private Collection<CodeModule> codeModules;

		public Collection<CodeModule> getCodeModules() {
			return codeModules;
		}

		public NewCodeModulesRunnable(ObjectStore objectStore) {
			super();
			this.objectStore = objectStore;
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Getting code modules", IProgressMonitor.UNKNOWN );
			Thread.sleep(2000);
			try {
				codeModules = CodeModulesManager.getManager().getNewCodeModules(objectStore);
			} catch (ExecutionException e) {
				PluginMessage.openErrorFromThread(getShell(), WIZARD_NAME, "Getting new Code Modules failed.", e );
			}
			monitor.done();
		}
	}
}
