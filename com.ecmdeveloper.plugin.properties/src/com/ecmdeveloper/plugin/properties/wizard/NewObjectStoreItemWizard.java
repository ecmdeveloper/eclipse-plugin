/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.constants.ClassType;
import com.ecmdeveloper.plugin.classes.model.task.GetClassDescriptionTask;
import com.ecmdeveloper.plugin.classes.wizard.ClassSelectionWizardPage;
import com.ecmdeveloper.plugin.core.model.tasks.TaskManager;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class NewObjectStoreItemWizard extends Wizard implements INewWizard {

	private static final String GETTING_DEFAULT_CLASS_DESCRIPTION_FAILED_MESSAGE = "Getting default class description failed";
	private static final String FAILED_MESSAGE = "Opening editor failed.";
	private IStructuredSelection selection;
	private IWorkbench workbench;
	private ClassSelectionWizardPage classSelectionPage;
	private ParentSelectionWizardPage parentSelectionWizardPage;
	private ClassDescription defaultClassDescription;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		parentSelectionWizardPage = new ParentSelectionWizardPage();
		parentSelectionWizardPage.setSelection( getInitialSelection() );
		addPage( parentSelectionWizardPage );
		classSelectionPage = new NewClassSelectionWizardPage(getClassType());
		addPage( classSelectionPage );
	}

	protected abstract ClassType getClassType();

	public String getParentObjectStoreId() {
		ObjectStoreItem parent = getParent();
		if ( parent != null ) {
			return parent.getObjectStore().getId();
		}
		return null;
	}

	public ObjectStore getParentObjectStore() {
		ObjectStoreItem parent = getParent();
		if ( parent != null ) {
			return parent.getObjectStore();
		}
		return null;
	}
	
	private ObjectStoreItem getInitialSelection() {
		if ( selection != null && selection.size() == 1 ) {
			Object object = selection.iterator().next();
			if ( object instanceof Folder || object instanceof ObjectStore ) {
				return (ObjectStoreItem) object;
			} 
		}
		return null;
	}

	@Override
	public boolean performFinish() {
		
		final ObjectStoreItem parent = getParent();
		if ( parent == null ) {
			return false;
		}
		
		if ( getClassDescription() == null ) {
	         fetchDefaultClassDescription( parent.getObjectStore() );
		}
		
		if ( getParent() != null && getClassDescription() != null ) {
			IEditorInput input = getEditorInput();
			if ( input != null ) {
				return openEditor(input);
			} 
		}
		return false;
	}

	protected abstract IEditorInput getEditorInput();

	public void fetchDefaultClassDescription(final ObjectStore objectStore) {

		if ( defaultClassDescription == null && objectStore != null) {
			try {
				getContainer().run(true, false, new GetDefaultClassDescriptionRunner(objectStore, getDefaultClassName() ) );
			} catch (Exception e) {
				PluginMessage.openErrorFromThread(getShell(), getWindowTitle(),
						GETTING_DEFAULT_CLASS_DESCRIPTION_FAILED_MESSAGE, e);
			}
		}
	}

	protected abstract String getDefaultClassName();
	
	protected ClassDescription getClassDescription() {
		ClassDescription classDescription = classSelectionPage.getClassDescription();
		if ( classDescription == null ) {
			return defaultClassDescription;
		}
		return classDescription;
	}

	public ClassDescription getDefaultClassDescription() {
		fetchDefaultClassDescription( getParentObjectStore() );
		return defaultClassDescription;
	}

	protected ObjectStoreItem getParent() {
		ObjectStoreItem parent = parentSelectionWizardPage.getSelection();
		return parent;
	}

	private boolean openEditor(IEditorInput input) {
		try {
			String editorId = getEditorId();
			IDE.openEditor( workbench.getActiveWorkbenchWindow().getActivePage(), input, editorId );
			return true;
		} catch (PartInitException e) {
			PluginMessage.openError( getShell(), getWindowTitle(), FAILED_MESSAGE, e);
		}
		return false;
	}

	protected abstract String getEditorId();
	
	class GetDefaultClassDescriptionRunner implements IRunnableWithProgress {

		private ObjectStore objectStore;
		private String className;
		
		public GetDefaultClassDescriptionRunner(ObjectStore objectStore, String className) {
			this.objectStore = objectStore;
			this.className = className;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException,
				InterruptedException {
			monitor.beginTask("Getting default class description", IProgressMonitor.UNKNOWN);
			getDefaultClassDescription(objectStore);
			monitor.done();
		}

		private void getDefaultClassDescription(final ObjectStore objectStore) {
			try {
				GetClassDescriptionTask task = new GetClassDescriptionTask( className, objectStore );
				TaskManager.getInstance().executeTaskSync(task);
				defaultClassDescription = task.getClassDescription();
			} catch (ExecutionException e) {
				PluginMessage.openErrorFromThread(getShell(), getWindowTitle(),
						GETTING_DEFAULT_CLASS_DESCRIPTION_FAILED_MESSAGE, e);
			}
		}
	}
}
