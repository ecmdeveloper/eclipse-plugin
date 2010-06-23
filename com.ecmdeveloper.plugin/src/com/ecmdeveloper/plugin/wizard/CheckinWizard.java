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

package com.ecmdeveloper.plugin.wizard;

import java.util.Collection;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.jobs.CheckinJob;
import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinWizard extends Wizard {

	private ConfigureCheckinWizardPage configureCheckinPage;
	private ContentSelectionWizardPage contentSelectionPage;
	private Document document;
	
	public CheckinWizard(Document document) {
		this.document = document;
	}

	public void addPages() {
		configureCheckinPage = new ConfigureCheckinWizardPage();
		addPage(configureCheckinPage);
		contentSelectionPage = new ContentSelectionWizardPage( document.getName() );
		addPage(contentSelectionPage);
	}
	
	@Override
	public boolean canFinish() {
		
		if ( getContainer().getCurrentPage().equals( configureCheckinPage ) ) {
			return ! configureCheckinPage.isSelectContent();
		}
		return true;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {

		if ( page instanceof ConfigureCheckinWizardPage ) {
			if ( ((ConfigureCheckinWizardPage)page).isSelectContent() ) {
				return contentSelectionPage;
			}
		}
		return null;
	}


	@Override
	public boolean performFinish() {

		Job checkinJob = createCheckinJob();
		checkinJob.setUser(true);
		checkinJob.schedule();
		
		return true;
	}

	private Job createCheckinJob() {
		Collection<Object> content = null;
		String mimeType = null;

		if ( configureCheckinPage.isSelectContent() ) {
			content = contentSelectionPage.getContent();
			mimeType = contentSelectionPage.getMimeType();
		}
		
		boolean majorVersion = configureCheckinPage.isCheckinMajor();
		
		Job checkinJob = new CheckinJob( document, getShell(), content, mimeType, majorVersion   );
		return checkinJob;
	}
}
