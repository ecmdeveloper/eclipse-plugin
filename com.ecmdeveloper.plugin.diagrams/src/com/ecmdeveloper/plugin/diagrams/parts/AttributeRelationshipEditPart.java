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
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
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

//	@Override
//	protected void activateFigure() {
//		super.activateFigure();
//		getFigure().addPropertyChangeListener( Connection.PROPERTY_CONNECTION_ROUTER, this );
//	}
//
//	@Override
//	protected void deactivateFigure() {
//		getFigure().removePropertyChangeListener( Connection.PROPERTY_CONNECTION_ROUTER, this);
//		super.deactivateFigure();
//	}

	public void positionConnections() {
		
		AttributeRelationship attributeRelationship = getAttributeRelationship();

		if ( attributeRelationship.getSourceConnector().getClassId().equals( attributeRelationship.getTargetConnector().getClassId() ) ) {

			Rectangle bounds = ((AbstractGraphicalEditPart)getSource()).getFigure().getBounds();
			System.out.println( "bounds: " + bounds.toString() );

			int outerX = bounds.width/2 + 20;
			int outerY = -(bounds.height/2 + 20);
			
			RelativeBendpoint point = new RelativeBendpoint(getConnectionFigure());
			point.setRelativeDimensions(new Dimension(outerX,0), new Dimension(outerX,0) );
			point.setWeight(0.5f);

			RelativeBendpoint point2 = new RelativeBendpoint(getConnectionFigure());
			point2.setRelativeDimensions(new Dimension(outerX,outerY), new Dimension(outerX,outerY) );
			point2.setWeight(0.5f);
			
			RelativeBendpoint point3 = new RelativeBendpoint(getConnectionFigure());
			point3.setRelativeDimensions(new Dimension(0,outerY), new Dimension(0,outerY) );
			point3.setWeight(0.5f);
			
			List<Bendpoint> constraint = new ArrayList<org.eclipse.draw2d.Bendpoint>();
			constraint.add(point);
			constraint.add(point2);
			constraint.add(point3);
			
			this.getConnectionFigure().setRoutingConstraint(constraint);
			
			System.err.println( attributeRelationship.getSourceConnector().getPropertyName() + " is in a loop" );
		}
	}
	
	@Override
	protected IFigure createFigure() {
		
		AttributeRelationship attributeRelationship = getAttributeRelationship();
		
		PolylineConnection connection = (PolylineConnection) super.createFigure();
		connection.setSourceDecoration(new PolygonDecoration() );
		
		if ( attributeRelationship.getTargetConnector().getPropertyId() != null ) {
			connection.setTargetDecoration( new PolygonDecoration() );
		}
		
		connection.setLineStyle(Graphics.LINE_DASH);
		
		String targetMultiplicity = attributeRelationship.getTargetConnector().getMultiplicity();
		
		if ( !targetMultiplicity.isEmpty() ) {
			ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(connection, true);
			targetEndpointLocator.setVDistance(20);
			targetEndpointLocator.setUDistance(20);
			Label targetMultiplicityLabel = new Label( targetMultiplicity );
			connection.add(targetMultiplicityLabel, targetEndpointLocator);
		}
		
		return connection;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		System.out.println( "Property change?");
	}
	
	
}