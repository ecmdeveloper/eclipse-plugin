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

import org.eclipse.draw2d.IFigure;
import org.eclipse.ui.IWorkbenchPart;

import com.ecmdeveloper.plugin.diagrams.parts.ClassDiagramClassEditPart;

/**
 * @author Ricardo.Belfor
 *
 */
public class ExportDiagramClassAction extends ExportAction {

	public static final String ID = "com.ecmdeveloper.plugin.diagrams.actions.exportDiagramClassAction";

	private static final String ACTION_NAME = "Export class to image";

	public ExportDiagramClassAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	@Override
	protected boolean calculateEnabled() {
		if (getSelectedObjects().size() != 1
				|| !(getSelectedObjects().get(0) instanceof ClassDiagramClassEditPart)) {
			return false;
		}
		return true;
	}

	protected IFigure getFigure() {
		if (getSelectedObjects().size() != 1
				|| !(getSelectedObjects().get(0) instanceof ClassDiagramClassEditPart)) {
			throw new UnsupportedOperationException();
		}
		
		ClassDiagramClassEditPart part = (ClassDiagramClassEditPart) getSelectedObjects().get(0);
		IFigure figure = part.getFigure();
		return figure;
	}
}
