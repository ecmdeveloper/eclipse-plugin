/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**
 * @author Ricardo.Belfor
 *
 */
public class ExportDiagramAction extends SelectionAction {

	public static final String ID = "com.ecmdeveloper.plugin.diagrams.actions.exportDiagramAction";

	private static final String ACTION_NAME = "Export Diagram";
	
	public ExportDiagramAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public void run() {
		Shell shell = getWorkbenchPart().getSite().getShell();
		MessageDialog.openInformation( shell, ACTION_NAME, "Exporting diagram " + getWorkbenchPart().getTitle() + " to file" );
	}
}
