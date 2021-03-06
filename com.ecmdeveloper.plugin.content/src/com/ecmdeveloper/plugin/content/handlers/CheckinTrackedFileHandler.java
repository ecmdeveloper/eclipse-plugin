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

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.jobs.CheckinTrackedFileJob;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.tracker.model.TrackedFile;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinTrackedFileHandler extends AbstractTrackedFileHandler {

	@Override
	protected void handleSelectedFile(IWorkbenchWindow window, TrackedFile trackedFile, IFile file, IObjectStore objectStore) {
		if ( trackedFile != null ) {
			CheckinTrackedFileJob job = new CheckinTrackedFileJob(trackedFile, file, window, objectStore);
			job.setUser(true);
			job.schedule();
		}
	}

}
