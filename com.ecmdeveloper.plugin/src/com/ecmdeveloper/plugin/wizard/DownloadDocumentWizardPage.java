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

package com.ecmdeveloper.plugin.wizard;

import java.io.InputStream;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * @author Ricardo.Belfor
 *
 */
public class DownloadDocumentWizardPage extends WizardNewFileCreationPage {

	private static final String WIZARD_DESCRIPTION = "This wizard downloads the content of the document to a file in the workspace.";
	private static final String WIZARD_TITLE = "Download content";
	private InputStream inputStream;

	public DownloadDocumentWizardPage(IStructuredSelection selection) {
		super("downloadPage", selection);
		setTitle(WIZARD_TITLE);
		setDescription(WIZARD_DESCRIPTION);
		setContainerFullPath( new Path("/") );
	}

	public void setInitialContents(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	protected InputStream getInitialContents() {
		return inputStream;
	}
}
