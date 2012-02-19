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

package com.ecmdeveloper.plugin.content.wizard;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.content.jobs.CheckinJob;
import com.ecmdeveloper.plugin.content.jobs.UpdateDocumentContentJob;
import com.ecmdeveloper.plugin.core.model.IDocument;

/**
 * @author ricardo.belfor
 *
 */
public class UpdateDocumentContentWizard extends Wizard {

	private static final String WINDOW_TITLE_MESSAGE = "Update Content Document {0}";

	private ContentSelectionWizardPage contentSelectionPage;
	private final IDocument document;

	public UpdateDocumentContentWizard(IDocument document) {
		this.document = document;
		String title = MessageFormat.format( WINDOW_TITLE_MESSAGE, document.getName() );
		setWindowTitle(title);
	}

	public void addPages() {
		contentSelectionPage = new ContentSelectionWizardPage( document.getName() );
		addPage(contentSelectionPage);
	}
	
	
	@Override
	public boolean canFinish() {
		return contentSelectionPage.getContent().size() == 1;
	}

	@Override
	public boolean performFinish() {

		Job job = createUpdateDocumentContentJob();
		job.setUser(true);
		job.schedule();
		
		return true;
	}

	private Job createUpdateDocumentContentJob() {
		Collection<Object> content = contentSelectionPage.getContent();;
		String mimeType = contentSelectionPage.getMimeType();
		
		Job job = new UpdateDocumentContentJob( document, getShell(), content, mimeType  );
		return job;
	}
}
