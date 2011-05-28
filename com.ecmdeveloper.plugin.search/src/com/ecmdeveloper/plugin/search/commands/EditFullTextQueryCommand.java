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

import com.ecmdeveloper.plugin.search.model.FullTextQuery;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.model.constants.FullTextQueryType;
import com.ecmdeveloper.plugin.search.wizards.FullTextQueryWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditFullTextQueryCommand extends EditQueryComponentCommand {

	private IQueryField previousField;
	private IQueryField newField;
	private FullTextQueryType previousFullTextQueryType;
	private FullTextQueryType newFullTextQueryType;
	private String previousText;
	private String newText;
	private boolean previousAllFields;
	private boolean newAllFields;

	public EditFullTextQueryCommand(QueryComponent queryComponent) {
		super(queryComponent);
		setLabel("Edit Full Text Query");
	}

	@Override
	public void execute() {
		Shell shell = Display.getCurrent().getActiveShell();
		FullTextQuery fullTextQuery = getFullTextQuery();

		FullTextQueryWizard wizard = getFullTextQueryWizard(fullTextQuery);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {

			previousField = fullTextQuery.getField();
			previousFullTextQueryType = fullTextQuery.getFullTextQueryType();
			previousText = fullTextQuery.getText();
			previousAllFields = fullTextQuery.isAllFields();
			
			newField = wizard.getField();
			newFullTextQueryType = wizard.getFullTextQueryType();
			newText = wizard.getText();
			newAllFields = wizard.isAllFields();
			
			redo();
		}
	}

	private FullTextQueryWizard getFullTextQueryWizard(FullTextQuery fullTextQuery) {
		FullTextQueryWizard wizard = new FullTextQueryWizard( queryComponent.getQuery() );
		wizard.setSelection( fullTextQuery.getField() );
		wizard.setFullTextQueryType( fullTextQuery.getFullTextQueryType() );
		wizard.setText( fullTextQuery.getText() );
		wizard.setAllFields( fullTextQuery.isAllFields() );
		
		return wizard;
	}

	private FullTextQuery getFullTextQuery() {
		return (FullTextQuery)queryComponent;
	}

	@Override
	public void redo() {
		FullTextQuery fullTextQuery = getFullTextQuery();
		fullTextQuery.setField( newField );
		fullTextQuery.setText( newText );
		fullTextQuery.setAllFields(newAllFields);
		fullTextQuery.setFullTextQueryType( newFullTextQueryType );
	}

	@Override
	public boolean canUndo() {
		return previousField != null && previousFullTextQueryType != null;
	}

	@Override
	public void undo() {
		FullTextQuery fullTextQuery = getFullTextQuery();
		fullTextQuery.setField( previousField );
		fullTextQuery.setFullTextQueryType( previousFullTextQueryType );
		fullTextQuery.setText(  previousText );
		fullTextQuery.setAllFields( previousAllFields );
	}
}
