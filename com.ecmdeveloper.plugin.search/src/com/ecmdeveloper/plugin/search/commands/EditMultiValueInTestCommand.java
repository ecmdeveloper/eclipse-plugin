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
import com.ecmdeveloper.plugin.search.model.MultiValueInTest;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.wizards.MultiValueInTestWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditMultiValueInTestCommand extends EditQueryComponentCommand {

	private IQueryField previousField;
	private IQueryField newField;
	private Object previousValue;
	private Object newValue;

	public EditMultiValueInTestCommand(QueryComponent queryComponent) {
		super(queryComponent);
		setLabel("Edit Multi Value In Test");
	}

	@Override
	public void execute() {
		Shell shell = Display.getCurrent().getActiveShell();
		MultiValueInTest inTest = getMultiValueInTest();

		MultiValueInTestWizard wizard = getMultiValueInTestWizard(inTest);
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

	private MultiValueInTestWizard getMultiValueInTestWizard(MultiValueInTest inTest) {
		MultiValueInTestWizard wizard = new MultiValueInTestWizard( queryComponent.getQuery() );
		wizard.setSelection( inTest.getField() );
		wizard.setValue( inTest.getValue() );
		
		return wizard;
	}

	private MultiValueInTest getMultiValueInTest() {
		return (MultiValueInTest)queryComponent;
	}

	@Override
	public void redo() {
		MultiValueInTest inTest = getMultiValueInTest();
		inTest.setField( newField );
		inTest.setValue( newValue );
	}

	@Override
	public boolean canUndo() {
		return previousField != null;
	}

	@Override
	public void undo() {
		MultiValueInTest inTest = getMultiValueInTest();
		inTest.setField( previousField );
		inTest.setValue( previousValue );
	}
}
