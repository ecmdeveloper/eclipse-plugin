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

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.InTest;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.wizards.InTestWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditInTestCommand extends EditQueryComponentCommand {

	private IQueryField previousField;
	private IQueryField newField;
	private Object previousValue;
	private Object newValue;

	public EditInTestCommand(QueryComponent queryComponent) {
		super(queryComponent);
		setLabel("Edit In Test");
	}

	@Override
	public void execute() {
		Shell shell = Display.getCurrent().getActiveShell();
		InTest inTest = getInTest();

		InTestWizard wizard = getInTestWizard(inTest);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {

			previousField = inTest.getField();
			previousValue = inTest.getValue();
			
			newField = wizard.getField();
			newValue = wizard.getValue();
			
			redo();
		}
	}

	private InTestWizard getInTestWizard(InTest inTest) {
		InTestWizard wizard = new InTestWizard( queryComponent.getQuery() );
		wizard.setSelection( inTest.getField() );
		wizard.setValue( inTest.getValue() );
		
		return wizard;
	}

	private InTest getInTest() {
		return (InTest)queryComponent;
	}

	@Override
	public void redo() {
		InTest inTest = getInTest();
		inTest.setField( newField );
		inTest.setValue( newValue );
	}

	@Override
	public boolean canUndo() {
		return previousField != null;
	}

	@Override
	public void undo() {
		InTest inTest = getInTest();
		inTest.setField( previousField );
		inTest.setValue( previousValue );
	}
}
