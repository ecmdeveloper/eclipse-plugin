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
import org.eclipse.jface.wizard.IWizardPage;
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
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
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
		parentSelectionWizardPage.setFolder( getInitialSelection() );
		addPage( parentSelectionWizardPage );
		classSelectionPage = new ClassSelectionWizardPage(getClassType());
		addPage( classSelectionPage );
	}

	protected abstract ClassType getClassType();

	public String getObjectStoreId() {
		Folder folder = getParentFolder();
		if ( folder != null ) {
			return folder.getObjectStore().getId();
		}
		return null;
	}
	
	private Folder getInitialSelection() {
		if ( selection.size() == 1 ) {
			Object object = selection.iterator().next();
			if ( object instanceof Folder ) {
				return (Folder) object;
			}
		}
		return null;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if ( page instanceof ParentSelectionWizardPage ) {
			classSelectionPage.setObjectStoreId( getObjectStoreId() );
			return classSelectionPage;
		}
		return super.getNextPage(page);
	}
	@Override
	public boolean performFinish() {
		
		final Folder parentFolder = getParentFolder();
		if ( parentFolder == null ) {
			return false;
		}
		
		if ( getClassDescription() == null ) {
	         fetchDefaultClassDescription( parentFolder.getObjectStore() );
		}
		
		if ( getParentFolder() != null && getClassDescription() != null ) {
			IEditorInput input = getEditorInput();
			if ( input != null ) {
				return openEditor(input);
			} 
		}
		return false;
	}

	private void fetchDefaultClassDescription(final ObjectStore objectStore) {

		try {
			getContainer().run(true, false, new GetDefaultClassDescriptionRunner(objectStore, "Document"));
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(getShell(), getWindowTitle(),
					GETTING_DEFAULT_CLASS_DESCRIPTION_FAILED_MESSAGE, e);
		}
	}

	protected abstract IEditorInput getEditorInput();
	
	protected ClassDescription getClassDescription() {
		ClassDescription classDescription = classSelectionPage.getClassDescription();
		if ( classDescription == null ) {
			return defaultClassDescription;
		}
		return classDescription;
	}

	protected Folder getParentFolder() {
		Folder parent = parentSelectionWizardPage.getFolder();
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
			getDefaultClassDescription(objectStore);
		}

		private void getDefaultClassDescription(final ObjectStore objectStore) {
			try {
				GetClassDescriptionTask task = new GetClassDescriptionTask( className, objectStore );
				ObjectStoresManager.getManager().executeTaskSync(task);
				defaultClassDescription = task.getClassDescription();
			} catch (ExecutionException e) {
				PluginMessage.openErrorFromThread(getShell(), getWindowTitle(),
						GETTING_DEFAULT_CLASS_DESCRIPTION_FAILED_MESSAGE, e);
			}
		}
	}
}
