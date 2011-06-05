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
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.wizards.FolderTestWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditInFolderTestCommand extends EditQueryComponentCommand {

	private IQueryField previousField;
	private IQueryField newField;
	private String previousFolder;
	private String newFolder;
	
	public EditInFolderTestCommand(QueryComponent queryComponent) {
		super(queryComponent);
		setLabel("Edit In Folder Test");
	}

	@Override
	public void execute() {
		
		Shell shell = Display.getCurrent().getActiveShell();
		InFolderTest inFolderTest = getInFolderTest();
		FolderTestWizard wizard = getFolderTestWizard(inFolderTest);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();

		if ( dialog.open() == Dialog.OK ) {
			previousField = inFolderTest.getField();
			previousFolder = inFolderTest.getFolder();
			
			newField = wizard.getField();
			newFolder = wizard.getFolder();
			
			redo();
		}
	}

	private FolderTestWizard getFolderTestWizard(InFolderTest inFolderTest) {
		FolderTestWizard wizard = new FolderTestWizard( inFolderTest.getQuery() );
		wizard.setSelection( inFolderTest.getField() );
		wizard.setFolder( inFolderTest.getFolder() );
		return wizard;
	}

	private InFolderTest getInFolderTest() {
		return (InFolderTest)queryComponent;
	}

	@Override
	public void redo() {
		InFolderTest inFolderTest = getInFolderTest();
		inFolderTest.setField( newField );
		inFolderTest.setFolder(newFolder);
	}

	@Override
	public boolean canUndo() {
		return previousField != null;
	}

	@Override
	public void undo() {
		InFolderTest inFolderTest = getInFolderTest();
		inFolderTest.setField( previousField );
		inFolderTest.setFolder( previousFolder );
	}
}
