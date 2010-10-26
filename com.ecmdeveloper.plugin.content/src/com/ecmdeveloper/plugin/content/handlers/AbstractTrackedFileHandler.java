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

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.tracker.model.FilesTracker;
import com.ecmdeveloper.plugin.tracker.model.TrackedFile;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractTrackedFileHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		
		while ( iterator.hasNext() ) {
			Object selectedObject = iterator.next();
			handleSelectedObject(window, selectedObject);
		}
		
		return null;
	}

	private void handleSelectedObject(IWorkbenchWindow window, Object selectedObject) {
		IFile file = null;
		TrackedFile trackedFile;
		if ( selectedObject instanceof IFile ) {
			file = (IFile) selectedObject;
			String filename = file.getFullPath().toString();
			trackedFile = FilesTracker.getInstance().getTrackedFile(filename);
		} else if ( selectedObject instanceof TrackedFile ) {
			trackedFile = (TrackedFile) selectedObject;
			file = trackedFile.getFile();
		} else if ( selectedObject instanceof Document ) { 
			Document document = (Document) selectedObject;
			trackedFile = FilesTracker.getInstance().getVersionSeriesTrackedFile( document.getVersionSeriesId() );
			if ( trackedFile != null ) {
				file = trackedFile.getFile();
			}
		} else {
			throw new UnsupportedOperationException("Invalid selection for " + this.getClass().getName() );
		}
		handleSelectedFile(window, trackedFile, file);
	}

	protected abstract void handleSelectedFile(IWorkbenchWindow window, TrackedFile trackedFile, IFile file);
}
