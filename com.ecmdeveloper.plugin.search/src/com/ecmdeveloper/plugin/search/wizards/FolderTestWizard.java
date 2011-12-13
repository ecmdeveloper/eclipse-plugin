/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.wizards;

import org.eclipse.jface.wizard.IWizardPage;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class FolderTestWizard extends QueryComponentWizard {

	private String folder;
	private ObjectValueWizardPage objectValueWizardPage;
	
	public FolderTestWizard(Query query) {
		super(query);
	}

	public void addPages() {

		super.addPages();
		objectValueWizardPage = new ObjectValueWizardPage();
		objectValueWizardPage.setShowOnlyFolders(true);
		objectValueWizardPage.setAllowPaths(true);
		objectValueWizardPage.setValue(folder);
		
		addPage(objectValueWizardPage);
	}

	@Override
	public IWizardPage getStartingPage() {
		if ( isSkipFieldSelection() ) {
			return objectValueWizardPage;
		}
		return super.getStartingPage();
	}

	
	@Override
	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {

			@Override
			protected boolean select(IQueryField queryField) {
				return InFolderTest.DESCRIPTION.isValidFor(queryField);
			}
		};
	}

	@Override
	public boolean canFinish() {
		return getField() != null && getFolder() != null;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFolder() {
		if ( objectValueWizardPage == null ) {
			return null;
		}
		return (String) objectValueWizardPage.getValue();
	}
}
