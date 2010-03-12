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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;

import com.ecmdeveloper.plugin.diagrams.layout.ClassDiagramLayoutManager;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.policies.ClassDiagramLayoutEditPolicy;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramEditPart extends AbstractClassesGraphicalEditPart
		implements PropertyChangeListener {

	public ClassDiagramEditPart(ClassDiagram classDiagram) {
		setModel(classDiagram);
	}

	public ClassDiagram getClassDiagram() {
		return (ClassDiagram) getModel();
	}

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getClassDiagram().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getClassDiagram().removePropertyChangeListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
	   IFigure figure = new FreeformLayer();
	   figure.setLayoutManager(new ClassDiagramLayoutManager(this) );
//     figure.setToolTip(createToolTipLabel());
	   return figure;
	}

	protected List<Object> getModelChildren() {
		
		ArrayList<Object> modelChildren = new ArrayList<Object>();
		modelChildren.addAll(getClassDiagram().getClassDiagramClasses() );
		modelChildren.addAll(getClassDiagram().getClassDiagramNotes() );
		return modelChildren;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
      installEditPolicy(EditPolicy.LAYOUT_ROLE, new ClassDiagramLayoutEditPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (ClassDiagram.CHILD_ADDED_PROP.equals(prop)
				|| ClassDiagram.CHILD_REMOVED_PROP.equals(prop)) {
			refreshChildren();
		}
	}

	@Override
	protected void registerVisuals() {
		ConnectionLayer cLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		cLayer.setConnectionRouter(new ShortestPathConnectionRouter(getFigure()));
	}
}
