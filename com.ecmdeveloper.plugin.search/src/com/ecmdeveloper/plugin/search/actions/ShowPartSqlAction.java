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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import com.ecmdeveloper.plugin.search.editor.GraphicalQueryEditor;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryElement;
import com.ecmdeveloper.plugin.search.parts.QueryEditPart;
import com.ecmdeveloper.plugin.search.ui.ShowQueryDialog;

/**
 * @author ricardo.belfor
 *
 */
public class ShowPartSqlAction extends SelectionAction {

	public static final String ID = "com.ecmdeveloper.plugin.search.actions.showPartSqlAction";

	private static final String ACTION_NAME = "Show SQL";

	public ShowPartSqlAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	@Override
	protected boolean calculateEnabled() {
		return isSingleItemSelected();
	}

	private boolean isSingleItemSelected() {
		return getSelectedObjects().size() == 1;
	}

	@Override
	public void run() {
		Shell shell = getWorkbenchPart().getSite().getShell();
		QueryEditPart part = (QueryEditPart) getSelectedObjects().get(0);
		QueryElement queryComponent = (QueryElement) part.getModel();
		ShowQueryDialog dialog = new ShowQueryDialog(shell, queryComponent.toSQL() );
		dialog.create();
		dialog.open();
	}
}
