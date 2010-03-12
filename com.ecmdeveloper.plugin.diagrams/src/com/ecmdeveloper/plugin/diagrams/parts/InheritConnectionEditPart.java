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

package com.ecmdeveloper.plugin.diagrams.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import com.ecmdeveloper.plugin.diagrams.model.InheritRelationship;

/**
 * @author Ricardo.Belfor
 *
 */
public class InheritConnectionEditPart extends AbstractClassesConnectionEditPart {

	private static final int INHERIT_DECORTATION_SIZE = 3;

	public InheritConnectionEditPart(InheritRelationship model ) {
		setModel(model);
	}
	
	public InheritRelationship getInheritRelationship() {
		return (InheritRelationship) getModel();
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
//		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RelationshipEditPolicy());
	}

	@Override
	public IFigure createFigure() {
		PolylineConnection connection = (PolylineConnection) super.createFigure();
		PolygonDecoration decoration = createInheritDecoration();
		connection.setSourceDecoration(decoration);
		return connection;
	}

	private PolygonDecoration createInheritDecoration() {
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setFill(true);
		decoration.setBackgroundColor( org.eclipse.draw2d.ColorConstants.white );
		PointList decorationPointList = createDecorationPointList();
		decoration.setTemplate(decorationPointList);
		return decoration;
	}

	private PointList createDecorationPointList() {
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0,0);
		decorationPointList.addPoint(-INHERIT_DECORTATION_SIZE,INHERIT_DECORTATION_SIZE);
		decorationPointList.addPoint(-INHERIT_DECORTATION_SIZE,-INHERIT_DECORTATION_SIZE);
		return decorationPointList;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
