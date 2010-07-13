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

package com.ecmdeveloper.plugin.content.jobs;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class DownloadCurrentDocumentJob extends AbstractCurrentDocumentJob {

	private static final String JOB_NAME = "Download Current Document";
	private static final String TASK_NAME = "Downloading Current Document \"{0}\"";
	
	public DownloadCurrentDocumentJob(Document document, IWorkbenchWindow window) {
		super(JOB_NAME, document, window);
	}

	@Override
	protected void scheduleNextJob(IProgressMonitor monitor) {
		String taskName = MessageFormat.format(TASK_NAME, getDocumentName() );
		monitor.beginTask( taskName, IProgressMonitor.UNKNOWN );
		DownloadDocumentJob job = new DownloadDocumentJob(getCurrentVersionDocument(), getWindow());
		job.setProgressGroup(monitor, IProgressMonitor.UNKNOWN );
		job.setUser(true);
		job.schedule();
	}


}
