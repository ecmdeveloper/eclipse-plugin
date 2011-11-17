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

package com.ecmdeveloper.plugin.search.commands;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.search.model.InTest;
import com.ecmdeveloper.plugin.search.wizards.InTestWizard;

/**
 * @author ricardo.belfor
 * 
 */
public class CreateInTestCommand extends CreateCommand {

	@Override
	public void execute() {

		Shell shell = Display.getCurrent().getActiveShell();

		InTestWizard wizard = new InTestWizard( child.getQuery() );
		wizard.setSelection( getQueryField() );
		wizard.setSkipFieldSelection( getQueryField() != null );
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {
			InTest comparison = (InTest)child;
			comparison.setField( wizard.getField() );
			comparison.setValue( wizard.getValue() );
			super.execute();
		}
	}
}
