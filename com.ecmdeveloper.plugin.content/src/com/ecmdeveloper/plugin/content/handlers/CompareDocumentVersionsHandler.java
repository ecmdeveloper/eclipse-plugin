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

package com.ecmdeveloper.plugin.content.handlers;

import java.util.Collection;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.ecmdeveloper.plugin.content.jobs.CompareDocumentsJob;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.ui.handlers.AbstractDocumentVersionHandler;
import com.ecmdeveloper.plugin.ui.jobs.GetDocumentVersionJob;
import com.ecmdeveloper.plugin.core.model.IDocument;

/**
 * @author Ricardo.Belfor
 *
 */
public class CompareDocumentVersionsHandler extends AbstractDocumentVersionHandler {

	@Override
	protected IJobChangeListener getJobChangeListener(IDocument document) {
		return new ComparedDocumentVersionsJobListener();
	}
	
	class ComparedDocumentVersionsJobListener extends JobChangeAdapter {

		private static final String HANDLER_NAME = "Compare Document Versions";
		private static final String TWO_VERSIONS_MESSAGE = "Selected two versions for comparison";
		
		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			GetDocumentVersionJob job = (GetDocumentVersionJob) event.getJob();
			
			if ( job.getSelectedVersions().size() != 2 ) {
				PluginMessage.openErrorFromThread(window.getShell(), HANDLER_NAME, TWO_VERSIONS_MESSAGE, null);
				return;
			}
			
			Collection<IDocument> selectedVersions = job.getSelectedVersions();
			CompareDocumentsJob job2 = new CompareDocumentsJob(selectedVersions, window );
			job2.setShowVersionLabels(true);
			job2.setUser(true);
			job2.schedule();
		}
	}
}
