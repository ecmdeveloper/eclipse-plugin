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

import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import com.ecmdeveloper.plugin.diagrams.model.AttributeRelationship;

/**
 * @author Ricardo.Belfor
 *
 */
public class AttributeRelationshipEditPart extends AbstractClassesConnectionEditPart {

	public AttributeRelationshipEditPart(AttributeRelationship model) {
		setModel(model);
	}

	public AttributeRelationship getAttributeRelationship() {
		return (AttributeRelationship) getModel();
	}
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
	}

	@Override
	protected IFigure createFigure() {
		
		AttributeRelationship attributeRelationship = getAttributeRelationship();
		
		PolylineConnection connection = (PolylineConnection) super.createFigure();
		connection.setTargetDecoration(new PolygonDecoration() );
		
		if ( attributeRelationship.getReflectivePropertyId() != null ) {
			connection.setSourceDecoration( new PolygonDecoration() );
		}
		
		connection.setLineStyle(Graphics.LINE_DASH);
		
		String targetMultiplicity = attributeRelationship.getTargetMultiplicity();
		
		if ( !targetMultiplicity.isEmpty() ) {
			ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(connection, true);
			targetEndpointLocator.setVDistance(15);
			Label targetMultiplicityLabel = new Label( targetMultiplicity );
			connection.add(targetMultiplicityLabel, targetEndpointLocator);
		}
		return connection;
	}}
