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

package com.ecmdeveloper.plugin.properties.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.jobs.GetDocumentVersionJob;
import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class EditDocumentVersionHandler extends AbstractEditHandler {

	protected IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {
			
			IObjectStoreItem objectStoreItem = (IObjectStoreItem) iterator.next();
			if ( objectStoreItem instanceof Document ) {
				Document document = (Document) objectStoreItem;
				IJobChangeListener jobChangeListener = new EditDocumentVersionJobChangeListener();
				GetDocumentVersionJob job = new GetDocumentVersionJob(document, window.getShell() );
				job.addJobChangeListener( jobChangeListener );
				job.setUser(true);
				job.schedule();
			}
		}

		return null;
	}

	class EditDocumentVersionJobChangeListener extends JobChangeAdapter {

		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			GetDocumentVersionJob job = (GetDocumentVersionJob) event.getJob();
			
			for ( Document selectedVersion : job.getSelectedVersions() ) {
				openObjectStoreItemEditor(selectedVersion, window);
			}
		}
	}
}
