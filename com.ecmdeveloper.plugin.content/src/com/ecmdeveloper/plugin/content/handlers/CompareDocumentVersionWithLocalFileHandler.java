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

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.content.jobs.CompareDocumentJob;
import com.ecmdeveloper.plugin.ui.jobs.GetDocumentVersionJob;
import com.ecmdeveloper.plugin.ui.handlers.AbstractDocumentVersionHandler;
import com.ecmdeveloper.plugin.core.model.IDocument;

/**
 * @author Ricardo.Belfor
 *
 */
public class CompareDocumentVersionWithLocalFileHandler extends AbstractDocumentVersionHandler {

	private static final String FILE_SELECTION_TITLE = "File Selection";
	private static final String SELECT_FILE_MESSAGE = "Select file to compare version of document \"{0}\" to:";

	@Override
	protected IJobChangeListener getJobChangeListener(IDocument document) {
		
		IFile compareFile = getCompareFile(window, document);
		if ( compareFile != null ) {
			return new ComparedDocumentVersionsJobListener( compareFile );
		} else {
			return null;
		}
	}

	private IFile getCompareFile(IWorkbenchWindow window, IDocument document) {
		
		SelectionDialog fileSelectionDialog = getFileSelectionDialog(document, window.getShell());
		fileSelectionDialog.open();
		if (fileSelectionDialog.getReturnCode() == Dialog.OK ) {
			if ( isFileSelected( fileSelectionDialog.getResult() ) ) {
				IFile file = (IFile) fileSelectionDialog.getResult()[0];
				Activator.setSelectionRoot( file );
				return file;
			}
		}
		return null;
	}

	private boolean isFileSelected(Object[] result) {
		return result != null && result.length > 0 && result[0] instanceof IFile;
	}

	private SelectionDialog getFileSelectionDialog(IDocument document, Shell shell) {
		
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell,
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		String message = MessageFormat.format( SELECT_FILE_MESSAGE, document.getName() );
		dialog.setTitle(FILE_SELECTION_TITLE);
		dialog.setMessage( message );
		dialog.setInput( ResourcesPlugin.getWorkspace().getRoot() );
		dialog.setInitialSelection( Activator.getSelectionRoot() );
		dialog.setAllowMultiple(false);
		return dialog;
	}
	
	class ComparedDocumentVersionsJobListener extends JobChangeAdapter {

		private IFile compareFile;

		public ComparedDocumentVersionsJobListener(IFile compareFile) {
			this.compareFile = compareFile;
		}

		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			GetDocumentVersionJob job = (GetDocumentVersionJob) event.getJob();

			if ( job.getSelectedVersions().size() == 0 ) {
				return;
			}

			for ( IDocument document : job.getSelectedVersions() ) {
				Job downloadJob = new CompareDocumentJob(document, window, compareFile );
				downloadJob.setUser(true);
				downloadJob.schedule();
			}
		}			
	}
}
