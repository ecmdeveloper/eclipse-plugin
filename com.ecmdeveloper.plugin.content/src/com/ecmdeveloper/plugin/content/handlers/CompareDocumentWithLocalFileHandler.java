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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.ecmdeveloper.plugin.content.jobs.CompareDocumentJob;
import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class CompareDocumentWithLocalFileHandler extends AbstractDocumentHandler {

	private static final String SELECT_FILE_MESSAGE = "Select file to compare document \"{0}\" to:";
	private static final String FILE_SELECTION_TITLE = "File Selection";

	@Override
	protected void handleDocument(Document document) {
		IFile compareFile = getCompareFile(getWorkbenchWindow(), document);
		if (compareFile != null) {
			CompareDocumentJob job = new CompareDocumentJob(document, getWorkbenchWindow(),
					compareFile);
			job.setUser(true);
			job.schedule();
		}
	}

	private IFile getCompareFile(IWorkbenchWindow window, Document document) {
		
		SelectionDialog fileSelectionDialog = getFileSelectionDialog(document, window.getShell());
		fileSelectionDialog.open();
		if (fileSelectionDialog.getReturnCode() == Dialog.OK ) {
			if ( isFileSelected( fileSelectionDialog.getResult() ) ) {
				return (IFile) fileSelectionDialog.getResult()[0];
			}
		}
		return null;
	}

	private boolean isFileSelected(Object[] result) {
		return result != null && result.length > 0 && result[0] instanceof IFile;
	}

	private SelectionDialog getFileSelectionDialog(Document document, Shell shell) {
		
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell,
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		String message = MessageFormat.format( SELECT_FILE_MESSAGE, document.getName() );
		dialog.setTitle( FILE_SELECTION_TITLE );
		dialog.setMessage( message );
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(false);
		return dialog;
	}
}