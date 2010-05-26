/**
 * Copyright 2009,2010, Ricardo Belfor
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
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.jobs.GetDocumentVersionJob;
import com.ecmdeveloper.plugin.jobs.ViewDocumentJob;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.filenet.api.constants.PropertyNames;

/**
 * @author Ricardo.Belfor
 *
 */
public class ViewDocumentVersionHandler extends AbstractHandler implements IHandler {

	public static final String ID = "com.ecmdeveloper.plugin.viewDocumentVersion";
	private IWorkbenchWindow window;
	
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
			GetDocumentVersionJob job = new GetDocumentVersionJob((Document) objectStoreItem, window.getShell() );
			job.addJobChangeListener( new GetDocumentVersionJobListener() );
			job.setUser(true);
			job.schedule();
		}

		return null;
	}
	
	class GetDocumentVersionJobListener extends JobChangeAdapter {

		private static final String VERSION_FORMAT = "Version {0}.{1} ";
		
		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			GetDocumentVersionJob job = (GetDocumentVersionJob) event.getJob();
			for ( Document document : job.getSelectedVersions() ) {
				viewDocument(document);
			}
			
			Activator.getDefault().getContentCache().registerAsListener(window);
		}

		private void viewDocument(Document document) {
			 				
			Object majorVersionNumber = document.getValue( PropertyNames.MAJOR_VERSION_NUMBER );
			Object minorVersionNumber = document.getValue( PropertyNames.MINOR_VERSION_NUMBER );
			String filePrefix = MessageFormat.format( VERSION_FORMAT, majorVersionNumber, minorVersionNumber );
;
			ViewDocumentJob job = new ViewDocumentJob( document, filePrefix, window );
			job.setUser(true);
			job.schedule();
		}
	}
}
