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

package com.ecmdeveloper.plugin.content.wizard;

import java.io.InputStream;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.ecmdeveloper.plugin.content.Activator;

/**
 * @author Ricardo.Belfor
 *
 */
public class DownloadDocumentWizardPage extends WizardNewFileCreationPage {

	private static final String CONFIRM_MESSAGE_FORMAT = "Are you sure you want to overwrite the file \"{0}\"?";
	private static final String WIZARD_DESCRIPTION = "This wizard downloads the content of the document to a file in the workspace.";
	private static final String WIZARD_TITLE = "Download content";
	private InputStream inputStream;

	public DownloadDocumentWizardPage(IStructuredSelection selection) {
		super("downloadPage", selection);
		setTitle(WIZARD_TITLE);
		setDescription(WIZARD_DESCRIPTION);
		setContainerFullPath( Activator.getSelectionRoot().getFullPath() );
		setAllowExistingResources(true);
	}

	public void setInitialContents(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	protected InputStream getInitialContents() {
		return inputStream;
	}

	public boolean deleteExistingFile(IProgressMonitor monitor) throws Exception {
	
		IPath containerPath = getContainerFullPath();
		IPath newFilePath = containerPath.append( getFileName() );
		IFile fileHandle = createFileHandle(newFilePath );
		if ( fileHandle.exists() ) {
			if ( ! getConfirmOverwrite(newFilePath) ) {
				return false;
			} 
			fileHandle.delete(true, monitor);
		}
		return true;
	}

	private boolean getConfirmOverwrite(IPath newFilePath) {
		String confirmMessage = MessageFormat.format(CONFIRM_MESSAGE_FORMAT, newFilePath
				.toString());
		boolean confirm = MessageDialog.openConfirm(getShell(), WIZARD_TITLE, confirmMessage );
		return confirm;
	}
	
	@Override
	public IFile createNewFile() {
		IFile file = super.createNewFile();
		Activator.setSelectionRoot(file);
		return file;
	}
}
