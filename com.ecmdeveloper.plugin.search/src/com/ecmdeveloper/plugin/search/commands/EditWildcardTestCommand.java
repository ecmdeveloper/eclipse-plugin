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
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.model.WildcardTest;
import com.ecmdeveloper.plugin.search.model.constants.WildcardType;
import com.ecmdeveloper.plugin.search.wizards.WildcardTestWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditWildcardTestCommand extends EditQueryComponentCommand {

	private IQueryField previousField;
	private IQueryField newField;
	private WildcardType previousWildcardType;
	private WildcardType newWildcardType;
	private String previousValue;
	private String newValue;
	
	public EditWildcardTestCommand(QueryComponent queryComponent) {
		super(queryComponent);
		setLabel("Edit Wildcard Test");
	}

	@Override
	public void execute() {
		Shell shell = Display.getCurrent().getActiveShell();
		WildcardTest wildcardTest = getWildcardTest();

		WildcardTestWizard wizard = getWildcardTestWizard(wildcardTest);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {

			previousField = wildcardTest.getField();
			previousWildcardType = wildcardTest.getWildcardType();
			previousValue = wildcardTest.getValue();
			
			newField = wizard.getField();
			newWildcardType = wizard.getWildcardType();
			newValue = wizard.getValue();
			redo();
		}
	}

	private WildcardTestWizard getWildcardTestWizard(WildcardTest wildcardTest) {
		WildcardTestWizard wizard = new WildcardTestWizard( queryComponent.getQuery() );
		wizard.setSelection( wildcardTest.getField() );
		wizard.setWildcardType(wildcardTest.getWildcardType() );
		wizard.setValue(wildcardTest.getValue() );
		return wizard;
	}

	private WildcardTest getWildcardTest() {
		return (WildcardTest)queryComponent;
	}

	@Override
	public void redo() {
		WildcardTest wildcardTest = getWildcardTest();
		wildcardTest.setField( newField );
		wildcardTest.setWildcardType( newWildcardType );
		wildcardTest.setValue( newValue );
	}

	@Override
	public boolean canUndo() {
		return previousField != null && previousWildcardType != null;
	}

	@Override
	public void undo() {
		WildcardTest wildcardTest = getWildcardTest();
		wildcardTest.setField( previousField );
		wildcardTest.setWildcardType( previousWildcardType );
		wildcardTest.setValue(previousValue);
	}
}
