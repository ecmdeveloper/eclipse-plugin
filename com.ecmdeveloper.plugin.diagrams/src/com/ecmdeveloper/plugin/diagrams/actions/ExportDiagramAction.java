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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.ui.IWorkbenchPart;

import com.ecmdeveloper.plugin.diagrams.editors.ClassDiagramEditor;

/**
 * @author Ricardo.Belfor
 *
 */
public class ExportDiagramAction extends ExportAction {

	public static final String ID = "com.ecmdeveloper.plugin.diagrams.actions.exportDiagramAction";
	private static final int DIAGRAM_RIGHT_MARGIN = 25; 
	private static final int DIAGRAM_BOTTOM_MARGIN = 10; 
	private static final String ACTION_NAME = "Export diagram to image";
	
	public ExportDiagramAction(IWorkbenchPart part) {
		super(part);
		setId( ID );
		setText( ACTION_NAME );
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	protected IFigure getFigure() {
		ScalableFreeformRootEditPart rootEditPart = getRootEditPart();
		IFigure rootFigure = ((LayerManager)rootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);
		return rootFigure;
	}

	@Override
	protected Rectangle getFigureBounds(IFigure figure) {

		ScalableFreeformRootEditPart rootEditPart = getRootEditPart();
		
		EditPart classDiagramEditPart = (EditPart) rootEditPart.getChildren().get(0);
		Rectangle diagramBounds = new Rectangle(0,0,0,0);
		
		for (int i = 0; i < classDiagramEditPart.getChildren().size(); i++)
		{
			AbstractGraphicalEditPart part = (AbstractGraphicalEditPart) classDiagramEditPart.getChildren().get(i);
			Rectangle bounds = part.getFigure().getBounds();
			Point bottomRight = bounds.getBottomRight();
			if ( bottomRight.x > diagramBounds.width ) {
				diagramBounds.width = bottomRight.x;
			}

			if ( bottomRight.y > diagramBounds.height ) {
				diagramBounds.height = bottomRight.y;
			}
		}
		
		diagramBounds.width += DIAGRAM_RIGHT_MARGIN;
		diagramBounds.height += DIAGRAM_BOTTOM_MARGIN;
		
		return diagramBounds;
	}

	private ScalableFreeformRootEditPart getRootEditPart() {
		GraphicalViewer graphicalViewer = ((ClassDiagramEditor)getWorkbenchPart()).getViewer();
		ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getRootEditPart();
		return rootEditPart;
	}
}
