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

import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.ComparisonOperation;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.wizards.ComparisonWizard;

/**
 * @author ricardo.belfor
 *
 */
public class EditComparisonCommand extends EditQueryComponentCommand {

	private IQueryField previousField;
	private IQueryField newField;
	private ComparisonOperation previousComparisonOperation;
	private ComparisonOperation newComparisonOperation;
	private Object previousValue;
	private Object newValue;

	public EditComparisonCommand(QueryComponent queryComponent) {
		super(queryComponent);
		setLabel("Edit Comparison");
	}

	@Override
	public void execute() {
		Shell shell = Display.getCurrent().getActiveShell();
		Comparison comparison = getComparison();

		ComparisonWizard wizard = getComparisonWizard(comparison);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {

			previousField = comparison.getField();
			previousComparisonOperation = comparison.getComparisonOperation();
			previousValue = comparison.getValue();
			
			newField = wizard.getField();
			newComparisonOperation = wizard.getComparisonOperation();
			newValue = wizard.getValue();
			
			redo();
		}
	}

	private ComparisonWizard getComparisonWizard(Comparison comparison) {
		ComparisonWizard wizard = new ComparisonWizard( queryComponent.getQuery() );
		wizard.setSelection( comparison.getField() );
		wizard.setComparisonOperation(comparison.getComparisonOperation() );
		wizard.setValue( comparison.getValue() );
		
		return wizard;
	}

	private Comparison getComparison() {
		return (Comparison)queryComponent;
	}

	@Override
	public void redo() {
		Comparison comparison = getComparison();
		comparison.setField( newField );
		comparison.setComparisonOperation( newComparisonOperation );
		comparison.setValue( newValue );
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
		comparison.setValue( previousValue );
	}
}
