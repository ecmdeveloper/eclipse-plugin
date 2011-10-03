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

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.content.jobs.ViewDocumentJob;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.ui.handlers.AbstractDocumentVersionHandler;
import com.ecmdeveloper.plugin.ui.jobs.GetDocumentVersionJob;

/**
 * @author Ricardo.Belfor
 *
 */
public class ViewDocumentVersionHandler extends AbstractDocumentVersionHandler {

	@Override
	protected IJobChangeListener getJobChangeListener(IDocument document) {
		return new GetDocumentVersionJobListener();
	}
	
	class GetDocumentVersionJobListener extends JobChangeAdapter {

		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			GetDocumentVersionJob job = (GetDocumentVersionJob) event.getJob();
			for ( IDocument document : job.getSelectedVersions() ) {
				viewDocument(document);
			}
			
			Activator.getDefault().getContentCache().registerAsListener(window);
		}

		private void viewDocument(IDocument document) {
			 				
			ViewDocumentJob job = new ViewDocumentJob( document, document.getVersionLabel(), window );
			job.setUser(true);
			job.schedule();
		}
	}
}
