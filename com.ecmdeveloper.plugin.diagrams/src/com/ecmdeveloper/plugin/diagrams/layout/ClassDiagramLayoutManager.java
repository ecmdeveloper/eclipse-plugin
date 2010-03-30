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

package com.ecmdeveloper.plugin.diagrams.layout;

import java.util.List;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.ecmdeveloper.plugin.diagrams.model.AttributeRelationship;
import com.ecmdeveloper.plugin.diagrams.parts.AttributeRelationshipEditPart;
import com.ecmdeveloper.plugin.diagrams.parts.ClassDiagramEditPart;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramLayoutManager extends FreeformLayout {

	private ClassDiagramEditPart classDiagramEditPart;
	
	public ClassDiagramLayoutManager(ClassDiagramEditPart classDiagramEditPart) {
		this.classDiagramEditPart = classDiagramEditPart;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void layout(IFigure parent) {
		
		super.layout(parent);
		//System.err.println( "Doing diagram layout! ");
		
		for (int i = 0; i < classDiagramEditPart.getChildren().size(); i++)
		{
			AbstractGraphicalEditPart part = (AbstractGraphicalEditPart) classDiagramEditPart.getChildren().get(i);
			List sourceConnections = part.getSourceConnections();
			for (Object sourceConnection : sourceConnections ) {
				if ( sourceConnection instanceof AttributeRelationshipEditPart ) {
					((AttributeRelationshipEditPart)sourceConnection).positionConnections();
				}
			}
		}
	}

}
