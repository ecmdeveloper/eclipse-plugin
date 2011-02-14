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

import java.awt.Dialog;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import com.ecmdeveloper.plugin.search.editor.GraphicalQueryEditor;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryTable;

/**
 * @author ricardo.belfor
 *
 */
public class RemoveTableAction extends SelectionAction {

	public static final String ID = "com.ecmdeveloper.plugin.search.actions.removeTableAction";
	private static final String ACTION_NAME = "Remove Table from Query";

	public RemoveTableAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	@Override
	public void run() {
		
		Shell shell = getWorkbenchPart().getSite().getShell();
		GraphicalQueryEditor editor = (GraphicalQueryEditor) getWorkbenchPart();
		Query query = editor.getQuery();
		Object input = query.getQueryTables();
		ListSelectionDialog dlg = new ListSelectionDialog(shell, input, new ArrayContentProvider(), new LabelProvider(),  "Select the tables to remove:");
		dlg.setTitle("Table Selection");
		if ( dlg.open() == Window.OK ) {
			Object[] result = dlg.getResult();
			if ( result != null ) {
				for (Object queryTable : result) { 
					query.remove((QueryTable) queryTable);
				}
			}
		}
	}

	@Override
	protected boolean calculateEnabled() {
		GraphicalQueryEditor editor = (GraphicalQueryEditor) getWorkbenchPart();
		Query query = editor.getQuery();
		return query.getQueryTables().size() != 0;
	}
}
