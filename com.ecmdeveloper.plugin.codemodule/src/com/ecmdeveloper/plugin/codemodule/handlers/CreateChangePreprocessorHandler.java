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

package com.ecmdeveloper.plugin.codemodule.handlers;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.codemodule.jobs.CreateChangePreprocessorJob;
import com.ecmdeveloper.plugin.codemodule.jobs.CreateEventActionJob;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.wizard.NewChangePreprocessorWizard;

/**
 * @author ricardo.belfor
 *
 */
public class CreateChangePreprocessorHandler extends AbstractCreateActionHandler {

	private static final String HANDLER_NAME = "Create Change Preprocessor Action";

	protected String getHandlerName() {
		return HANDLER_NAME;
	}

	protected void createAction(final IWorkbenchWindow window, CodeModuleFile codeModuleFile) {
		NewChangePreprocessorWizard wizard = new NewChangePreprocessorWizard(codeModuleFile);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {
			scheduleCreateChangePreprocessorJob(window, codeModuleFile, wizard);
		}
	}

	private void scheduleCreateChangePreprocessorJob(final IWorkbenchWindow window, CodeModuleFile codeModuleFile,
			NewChangePreprocessorWizard wizard) {
		String name = wizard.getName();
		String className = wizard.getClassName();
		boolean enabled = wizard.isEnabled();
		CreateChangePreprocessorJob job = new CreateChangePreprocessorJob(codeModuleFile, name, className, enabled, window.getShell() );
		job.setUser(true);
		job.schedule();
	}
}
