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
import com.ecmdeveloper.plugin.classes.wizard.ClassSelectionWizardPage;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.properties.editors.ObjectStoreItemEditorInput;
import com.ecmdeveloper.plugin.properties.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class NewObjectStoreItemWizard extends Wizard implements INewWizard {

	private static final String FAILED_MESSAGE = "Opening editor failed.";
	private IStructuredSelection selection;
	@SuppressWarnings("unused")
	private IWorkbench workbench;
	private ClassSelectionWizardPage classSelectionPage;
	private ParentSelectionWizardPage parentSelectionWizardPage;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
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
		Folder folder = parentSelectionWizardPage.getFolder();
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
		IEditorInput input = getEditorInput();
		if ( input != null ) {
			return openEditor(input);
		} 
		return false;
	}

	private IEditorInput getEditorInput() {
		
		Folder parent = parentSelectionWizardPage.getFolder();
		if ( parent == null ) {
			return null;
		}
		
		ClassDescription classDescription = classSelectionPage.getClassDescription();
		if ( classDescription == null ) {
			return null;
		}
		
		IEditorInput input = new ObjectStoreItemEditorInput( null, classDescription );
		return input;
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
}
