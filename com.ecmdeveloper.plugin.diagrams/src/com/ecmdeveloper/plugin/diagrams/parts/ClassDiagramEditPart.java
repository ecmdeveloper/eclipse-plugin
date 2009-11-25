/**
// * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.policies.ClassDiagramLayoutEditPolicy;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramEditPart extends AbstractClassesGraphicalEditPart {

	public ClassDiagramEditPart(ClassDiagram classDiagram) {
		setModel(classDiagram);
	}

	public ClassDiagram getClassDiagram() {
		return (ClassDiagram) getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
	   IFigure figure = new FreeformLayer();
	   figure.setLayoutManager(new FreeformLayout());
//     figure.setToolTip(createToolTipLabel());
	   return figure;
	}

	protected List<Object> getModelChildren() {
		return new ArrayList<Object>(getClassDiagram().getClassDiagramClasses());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
      installEditPolicy(EditPolicy.LAYOUT_ROLE, new ClassDiagramLayoutEditPolicy());
	}
}
