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

package com.ecmdeveloper.plugin.search.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import com.ecmdeveloper.plugin.search.editor.GraphicalQueryEditor;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.wizards.TableSelectionWizard;

/**
 * @author ricardo.belfor
 *
 */
public class AddTableAction extends SelectionAction {

	public static final String ID = "com.ecmdeveloper.plugin.search.actions.addTableAction";
	private static final String ACTION_NAME = "Add Table to Query";

	public AddTableAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	@Override
	public void run() {
		GraphicalQueryEditor editor = (GraphicalQueryEditor) getWorkbenchPart();
		Query query = editor.getQuery();

		Shell shell = getWorkbenchPart().getSite().getShell();
		TableSelectionWizard wizard = new TableSelectionWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();
		
		if ( dialog.open() == Window.OK ) {
			IQueryTable queryTable = wizard.getQueryTable();
			query.add(queryTable);
		}
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
}
