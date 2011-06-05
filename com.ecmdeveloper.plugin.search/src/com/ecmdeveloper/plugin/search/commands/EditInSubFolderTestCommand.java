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
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.wizards.FolderTestWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditInSubFolderTestCommand extends EditQueryComponentCommand {

	private IQueryField previousField;
	private IQueryField newField;
	private String previousFolder;
	private String newFolder;
	
	public EditInSubFolderTestCommand(QueryComponent queryComponent) {
		super(queryComponent);
		setLabel("Edit In Subfolder Test");
	}

	@Override
	public void execute() {
		
		Shell shell = Display.getCurrent().getActiveShell();
		InSubFolderTest inSubFolderTest = getInSubFolderTest();
		FolderTestWizard wizard = getFolderTestWizard(inSubFolderTest);

		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();

		if ( dialog.open() == Dialog.OK ) {
			previousField = inSubFolderTest.getField();
			previousFolder = inSubFolderTest.getFolder();

			newField = wizard.getField();
			newFolder = wizard.getFolder();
			
			redo();
		}
	}

	private FolderTestWizard getFolderTestWizard(InSubFolderTest inSubFolderTest) {
		FolderTestWizard wizard = new FolderTestWizard( inSubFolderTest.getQuery() );
		wizard.setSelection( inSubFolderTest.getField() );
		wizard.setFolder( inSubFolderTest.getFolder() );
		return wizard;
	}

	private InSubFolderTest getInSubFolderTest() {
		return (InSubFolderTest)queryComponent;
	}

	@Override
	public void redo() {
		InSubFolderTest inSubFolderTest = getInSubFolderTest();
		inSubFolderTest.setField( newField );
		inSubFolderTest.setFolder(newFolder);
	}

	@Override
	public boolean canUndo() {
		return previousField != null;
	}

	@Override
	public void undo() {
		InSubFolderTest inSubFolderTest = getInSubFolderTest();
		inSubFolderTest.setField( previousField );
		inSubFolderTest.setFolder( previousFolder );
	}
}
