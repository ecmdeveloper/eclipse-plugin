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

import com.ecmdeveloper.plugin.search.model.ClassTest;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.wizards.ClassTestWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditClassTestCommand extends EditQueryComponentCommand {

	private IQueryField previousField;
	private IQueryField newField;
	private String previousClassName;
	private String newClassName;
	
	public EditClassTestCommand(QueryComponent queryComponent) {
		super(queryComponent);
		setLabel("Edit Class Test");
	}

	@Override
	public void execute() {
		
		Shell shell = Display.getCurrent().getActiveShell();
		ClassTest classTest = getClassTest();

		ClassTestWizard wizard = getClassTestWizard(classTest);
		
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();

		if ( dialog.open() == Dialog.OK ) {
			previousField = classTest.getField();
			previousClassName = classTest.getClassName();
			
			newField = wizard.getField();
			newClassName = wizard.getClassName();
			redo();
		}
	}

	private ClassTestWizard getClassTestWizard(ClassTest classTest) {
		ClassTestWizard wizard = new ClassTestWizard( classTest.getQuery() );
		wizard.setSelection( classTest.getField() );
		wizard.setClassName( classTest.getClassName() );
		return wizard;
	}

	private ClassTest getClassTest() {
		return (ClassTest)queryComponent;
	}

	@Override
	public void redo() {
		ClassTest classTest = getClassTest();
		classTest.setField( newField );
		classTest.setClassName( newClassName );
	}

	@Override
	public boolean canUndo() {
		return previousField != null;
	}

	@Override
	public void undo() {
		ClassTest classTest = getClassTest();
		classTest.setField( previousField );
		classTest.setClassName( previousClassName );
	}
}
