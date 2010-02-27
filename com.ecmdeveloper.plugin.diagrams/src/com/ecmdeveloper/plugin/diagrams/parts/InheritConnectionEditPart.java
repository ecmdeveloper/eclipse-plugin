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

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import com.ecmdeveloper.plugin.diagrams.model.InheritRelationship;

/**
 * @author Ricardo.Belfor
 *
 */
public class InheritConnectionEditPart extends AbstractClassesConnectionEditPart {

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
	public IFigure getFigure() {
		PolylineConnection conn = (PolylineConnection) super.createFigure();
		conn.setConnectionRouter(new BendpointConnectionRouter());
		conn.setTargetDecoration(new PolygonDecoration());
		return conn;
	}
}
