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

package com.ecmdeveloper.plugin.ui.wizard;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.jobs.DeleteJob;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public class DeleteWizard extends Wizard {

	private static final String WINDOW_TITLE = "Delete";
	private static final String DELETE_MESSAGE = "Do you want to perform the delete operation?";
	private static final String CUSTOM_OBJECTS_TYPE_NAME = "custom objects";
	private static final String FOLDERS_TYPE_NAME = "folders";
	private static final String DOCUMENTS_TYPE_NAME = "documents";
	
	private PreviewDeleteWizardPage confirmDeleteWizardPage;
	private ConfigureDeleteWizardPage configureDeleteWizardPage;
	
	private Collection<IObjectStoreItem> itemsDeleted;
	
	public DeleteWizard(Collection<IObjectStoreItem> itemsDeleted) {
		setWindowTitle(WINDOW_TITLE);
		this.itemsDeleted = itemsDeleted;
	}

	@Override
	public void addPages() {
		
		if ( isFolderDeleted() ) {
			configureDeleteWizardPage = new ConfigureDeleteWizardPage();
			addPage( configureDeleteWizardPage );
		}
		confirmDeleteWizardPage = new PreviewDeleteWizardPage();
		confirmDeleteWizardPage.setItemsDeleted(itemsDeleted);
		addPage(confirmDeleteWizardPage);
	}

	public boolean isFolderDeleted() {
		for ( IObjectStoreItem objectStoreItem : itemsDeleted ) {
			if ( objectStoreItem instanceof Folder) {
				return true;
			}
		}
		return false;
	}

	public boolean isDeleteContainedDocuments() {
		return configureDeleteWizardPage != null
				&& configureDeleteWizardPage.isDeleteContainedDocuments();
	}

	public boolean isDeleteContainedCustomObjects() {
		return configureDeleteWizardPage != null
				&& configureDeleteWizardPage.isDeleteContainedCustomObjects();
	}

	public boolean isDeleteContainedFolders() {
		return configureDeleteWizardPage != null
				&& configureDeleteWizardPage.isDeleteContainedFolders();
	}

	@Override
	public boolean performFinish() {

		boolean answerTrue = MessageDialog.openQuestion(getShell(), getWindowTitle(), DELETE_MESSAGE );
		if (answerTrue) {
			DeleteJob deleteJob = createDeleteJob();
			deleteJob.setUser(true);
			deleteJob.schedule();
			return true;
		}
		return false;
	}

	private DeleteJob createDeleteJob() {
		DeleteJob deleteJob = new DeleteJob(itemsDeleted );
		deleteJob.setDeleteContainedDocuments(isDeleteContainedDocuments() );
		deleteJob.setDeleteContainedCustomObjects(isDeleteContainedCustomObjects() );
		deleteJob.setDeleteContainedFolders(isDeleteContainedFolders() );
		return deleteJob;
	}

	public ArrayList<String> getDeletedItemTypes() {
		ArrayList<String> deletedItemTypes = new ArrayList<String>();
		if ( isDeleteContainedDocuments() ) {
			deletedItemTypes.add(DOCUMENTS_TYPE_NAME);
		}
		if ( isDeleteContainedCustomObjects() ) {
			deletedItemTypes.add(CUSTOM_OBJECTS_TYPE_NAME);
		}
		if ( isDeleteContainedFolders() ) {
			deletedItemTypes.add(FOLDERS_TYPE_NAME);
		}
		return deletedItemTypes;
	}
}
