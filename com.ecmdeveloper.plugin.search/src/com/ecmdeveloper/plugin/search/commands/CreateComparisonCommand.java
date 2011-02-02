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

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.QueryField;
import com.ecmdeveloper.plugin.search.model.QueryFieldType;
import com.ecmdeveloper.plugin.search.wizards.ComparisonWizard;

/**
 * @author ricardo.belfor
 * 
 */
public class CreateComparisonCommand extends CreateCommand {

	@Override
	public void execute() {

		Shell shell = Display.getCurrent().getActiveShell();

		ComparisonWizard wizard = new ComparisonWizard(createMockFields());
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		if ( dialog.open() == Dialog.OK ) {
			((Comparison)child).setField( wizard.getField() );
			((Comparison)child).setComparisonOperation( wizard.getComparisonOperation() );
			super.execute();
		}
	}

	private ArrayList<IQueryField> createMockFields() {
		ArrayList<IQueryField> fields = new ArrayList<IQueryField>();
		for (int i = 0; i < 10; i++) {
			QueryField queryField = new QueryField("Field " + i , QueryFieldType.STRING );
			fields.add(queryField);
		}
		return fields;
	}
}
