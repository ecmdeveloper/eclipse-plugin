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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.content.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public class DownloadDocumentWizard extends Wizard {

	private static final String WIZARD_TITLE = "Download document content";
	private DownloadDocumentWizardPage page;
	private IStructuredSelection selection;
	private IWorkbench workbench;
	private String filename;
	private InputStream inputStream;
	private boolean openEditor;
	private IFile newFile;
	
	public DownloadDocumentWizard(String filename, InputStream inputStream, boolean openEditor) {
		super();
		setWindowTitle(WIZARD_TITLE);
		this.filename = filename;
		this.inputStream = inputStream;
		this.openEditor = openEditor;
	}
	
	public void addPages() {
		page = new DownloadDocumentWizardPage(selection);
		page.setFileName(filename);
		page.setInitialContents(inputStream);
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	@Override
	public boolean performFinish() {
		
		try {
			if ( ! page.deleteExistingFile( new NullProgressMonitor() ) ) {
				return false;
			}
			newFile = page.createNewFile();
		} catch (Exception e) {
			PluginMessage.openError(getShell(), WIZARD_TITLE, e.getLocalizedMessage(), e );
			return false;
		}

		if ( newFile != null && openEditor ) {
			return openEditor();
		}
		
		return true;
	}

	private boolean openEditor() {
		try {
			IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(activePage, newFile, true);
			return true;
		} catch (PartInitException e) {
			PluginMessage.openError(getShell(), WIZARD_TITLE, e.getLocalizedMessage(), e);
			return false;
		}
	}
	
	public IFile getNewFile() {
		return newFile;
	}
}
