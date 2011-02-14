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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.ecmdeveloper.plugin.search.editor.GraphicalQueryEditor;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryTable;

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
		ElementTreeSelectionDialog dialog = createTableSelectionDialog(shell);
		if ( dialog.open() == Window.OK ) {
			for ( Object result : dialog.getResult() ) {
				IQueryTable queryTable = new QueryTable( result.toString() );
				query.add(queryTable);
			}
		}
	}

	private ElementTreeSelectionDialog createTableSelectionDialog(Shell shell) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell,
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle("Table Selection");
		dialog.setMessage("Select the table from the tree:");
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		return dialog;
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
}
