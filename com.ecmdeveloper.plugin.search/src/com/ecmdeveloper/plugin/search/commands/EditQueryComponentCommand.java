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

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.ComparisonOperation;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.wizards.ComparisonWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditQueryComponentCommand extends Command {

	protected final QueryComponent queryComponent;
	private IQueryField previousField;
	private ComparisonOperation previousComparisonOperation;
	private IQueryField newField;
	private ComparisonOperation newComparisonOperation;
	
	public EditQueryComponentCommand(QueryComponent queryComponent) {
		this.queryComponent = queryComponent;
	}

	@Override
	public boolean canExecute() {
		return queryComponent != null;
	}

	@Override
	public void execute() {
		Shell shell = Display.getCurrent().getActiveShell();
		Comparison comparison = getComparison();

		ComparisonWizard wizard = new ComparisonWizard( queryComponent.getQuery() );
		wizard.setSelection( comparison.getField() );
		wizard.setComparisonOperation(comparison.getComparisonOperation() );
		
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {

			previousField = comparison.getField();
			previousComparisonOperation = comparison.getComparisonOperation();

			newField = wizard.getField();
			newComparisonOperation = wizard.getComparisonOperation();
			redo();
		}
	}

	private Comparison getComparison() {
		return (Comparison)queryComponent;
	}

	@Override
	public void redo() {
		Comparison comparison = getComparison();
		comparison.setField( newField );
		comparison.setComparisonOperation( newComparisonOperation );
	}

	@Override
	public boolean canUndo() {
		return previousField != null && previousComparisonOperation != null;
	}

	@Override
	public void undo() {
		Comparison comparison = getComparison();
		comparison.setField( previousField );
		comparison.setComparisonOperation( previousComparisonOperation );
	}
}
