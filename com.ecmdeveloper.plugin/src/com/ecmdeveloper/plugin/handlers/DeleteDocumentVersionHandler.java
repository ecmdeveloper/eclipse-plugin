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

package com.ecmdeveloper.plugin.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;

import com.ecmdeveloper.plugin.jobs.DeleteJob;
import com.ecmdeveloper.plugin.jobs.GetDocumentVersionJob;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.util.Messages;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class DeleteDocumentVersionHandler extends AbstractDocumentVersionHandler {

	@Override
	protected IJobChangeListener getJobChangeListener() {
		return new DeleteDocumentVersionsJobListener();
	}

	class DeleteDocumentVersionsJobListener extends JobChangeAdapter {

		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			GetDocumentVersionJob job = (GetDocumentVersionJob) event.getJob();

			if ( job.getSelectedVersions().size() == 0 ) {
				return;
			}
			
			ArrayList<IObjectStoreItem> itemsDeleted = new ArrayList<IObjectStoreItem>();
			itemsDeleted.addAll( job.getSelectedVersions() );
			Job deleteJob = new DeleteJob(itemsDeleted, window.getShell(), false );
			deleteJob.setUser(true);
			deleteJob.schedule();
		}			
	}
}
