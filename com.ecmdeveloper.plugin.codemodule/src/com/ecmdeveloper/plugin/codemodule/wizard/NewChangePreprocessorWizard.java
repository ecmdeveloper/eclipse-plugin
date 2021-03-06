/**
 * Copyright 2013, Ricardo Belfor
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

package com.ecmdeveloper.plugin.codemodule.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;

/**
 * @author ricardo.belfor
 *
 */
public class NewChangePreprocessorWizard extends Wizard {

	private static final String ACTION_OBJECT = "Change Preprocessor";
	private static final String CHANGE_PREPROCESSOR_INTERFACE = "com.filenet.api.engine.ChangePreprocessor";
	private static final String TITLE = "Configure Change Preprocessor";

	private ConfigureEventActionWizardPage configureChangePreprocessorWizardPage;
	private final CodeModuleFile codeModuleFile;

	public NewChangePreprocessorWizard(CodeModuleFile codeModuleFile) {
		this.codeModuleFile = codeModuleFile;
		setWindowTitle("New Change Preprocessor");
	}
	
	@Override
	public void addPages() {
		configureChangePreprocessorWizardPage = new ConfigureEventActionWizardPage(codeModuleFile, TITLE,
				CHANGE_PREPROCESSOR_INTERFACE, ACTION_OBJECT );
		addPage(configureChangePreprocessorWizardPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public String getName() {
		return configureChangePreprocessorWizardPage.getName();
	}

	public String getClassName() {
		return configureChangePreprocessorWizardPage.getClassName();
	}

	public boolean isEnabled() {
		return configureChangePreprocessorWizardPage.isEnabled();
	}
}
