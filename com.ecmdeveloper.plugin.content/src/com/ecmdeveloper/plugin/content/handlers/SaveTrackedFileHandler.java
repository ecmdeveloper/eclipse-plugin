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

import java.net.FileNameMap;
import java.net.URLConnection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.jobs.SaveTrackedFileJob;
import com.ecmdeveloper.plugin.tracker.model.TrackedFile;

/**
 * @author ricardo.belfor
 *
 */
public class SaveTrackedFileHandler extends AbstractTrackedFileHandler {

	private static final String MIME_TYPE_MESSAGE = "Document Mime Type:";
	private static final String HANDLER_NAME = "Save Tracked File";

	@Override
	protected void handleSelectedFile(IWorkbenchWindow window, TrackedFile trackedFile, IFile file) {
		if ( trackedFile != null ) {
			String mimeType = getMimeType(window, file);
			SaveTrackedFileJob job = new SaveTrackedFileJob(trackedFile, file, window, mimeType );
			job.setUser(true);
			job.schedule();
		}
	}

	private String getMimeType(IWorkbenchWindow window, IFile file) {
		
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String mimeType = fileNameMap.getContentTypeFor( file.getName() );
		
		InputDialog inputDialog = new InputDialog( window.getShell(), HANDLER_NAME, MIME_TYPE_MESSAGE, mimeType, null );
		int open = inputDialog.open();
		
		if ( open == InputDialog.OK ) {
			return inputDialog.getValue();
		}
		
		return mimeType;
	}

}
