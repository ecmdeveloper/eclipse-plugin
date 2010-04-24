/**
 * Copyright 2009,2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.diagrams.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramFile;
import com.ecmdeveloper.plugin.diagrams.util.PluginTagNames;

public class NewClassDiagramWizardPage extends WizardNewFileCreationPage {

	private static final String WIZARD_DESCRIPTION = "This wizard creates a new file with the .classdiagram extension that can be opened by the Content Engine Class Diagram editor.";
	private static final String WIZARD_TITLE = "New Content Engine Class Diagram";
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public NewClassDiagramWizardPage(IStructuredSelection selection) {
		super("wizardPage",selection);
		setTitle(WIZARD_TITLE);
		setDescription(WIZARD_DESCRIPTION);
		setFileExtension( "classdiagram" );
	}

	@Override
	protected InputStream getInitialContents() {
		String contents = XML_HEADER + "<" + PluginTagNames.CLASSDIAGRAM + " "
				+ PluginTagNames.SHOW_DISPLAY_NAMES + "='false' " + PluginTagNames.SHOW_ICONS
				+ "='true' " + PluginTagNames.VERSION_TAG + "='"
				+ ClassDiagramFile.CURRENT_FILE_VERSION + "'>" + "</" + PluginTagNames.CLASSDIAGRAM
				+ ">";
		return new ByteArrayInputStream(contents.getBytes());
	}
}