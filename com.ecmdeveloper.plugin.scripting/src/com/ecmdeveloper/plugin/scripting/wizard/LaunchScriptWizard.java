/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.scripting.wizard;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.scripting.Activator;

/**
 * @author ricardo.belfor
 *
 */
public class LaunchScriptWizard extends Wizard {

	private ConfigureScriptWizardPage configureScriptWizardPage;
	private ConfigureCredentialsWizardPage configureCredentialsWizardPage;
	private final IPreferenceStore preferenceStore;
	private final String projectNatureId;

	public LaunchScriptWizard(IPreferenceStore preferenceStore, String projectNatureId) {
		super();
		this.preferenceStore = preferenceStore;
		this.projectNatureId = projectNatureId;
		setWindowTitle("Run Console Project");
	}
	
	@Override
	public void addPages() {
		
		configureScriptWizardPage = new ConfigureScriptWizardPage(preferenceStore, projectNatureId);
		addPage(configureScriptWizardPage);
		
		configureCredentialsWizardPage = new ConfigureCredentialsWizardPage(preferenceStore );
		addPage(configureCredentialsWizardPage);
	}

	@Override
	public boolean performFinish() {
		configureScriptWizardPage.store();
		configureCredentialsWizardPage.store();
		return true;
	}

	public IMethod getMethod() {
		return configureScriptWizardPage.getMethod();
	}
	
	public boolean isDebug() {
		return configureScriptWizardPage.isDebug();
	}

	public boolean isUseExistingCredentials() {
		return configureCredentialsWizardPage.isUseExistingCredentials();
	}

	public String getUsername() {
		return configureCredentialsWizardPage.getUsername();
	}

	public String getPassword() {
		return configureCredentialsWizardPage.getPassword();
	}
}
