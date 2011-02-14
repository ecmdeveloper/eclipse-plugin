/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.ecmdeveloper.plugin.search.model.QuerySubpart;
import com.ecmdeveloper.plugin.search.policies.QueryElementEditPolicy;

/**
 * @author ricardo.belfor
 *
 */
public abstract class QueryEditPart extends AbstractGraphicalEditPart implements 
		PropertyChangeListener {

	private AccessibleEditPart acc;

	public void activate(){
		if (isActive())
			return;
		super.activate();
		getLogicSubpart().addPropertyChangeListener(this);
	}

	public void deactivate(){
		if (!isActive())
			return;
		super.deactivate();
		getLogicSubpart().removePropertyChangeListener(this);
	}

	protected AccessibleEditPart getAccessibleEditPart() {
		if (acc == null)
			acc = createAccessible();
		return acc;
	}
	
	protected void createEditPolicies(){
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new QueryElementEditPolicy());
		//installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new LogicNodeEditPolicy());
	}

	abstract protected AccessibleEditPart createAccessible();

	protected QuerySubpart getLogicSubpart(){
		return (QuerySubpart)getModel();
	}

//	protected NodeFigure getNodeFigure(){
//		return (NodeFigure) getFigure();
//	}

	public void propertyChange(PropertyChangeEvent evt){
		String prop = evt.getPropertyName();
		if (QuerySubpart.CHILDREN.equals(prop)) {
			if (evt.getOldValue() instanceof Integer)
				// new child
				addChild(createChild(evt.getNewValue()), ((Integer)evt
						.getOldValue()).intValue());
			else
				// remove child
				removeChild((EditPart)getViewer().getEditPartRegistry().get(
						evt.getOldValue()));
		}
		else if (prop.equals(QuerySubpart.ID_SIZE) || prop.equals(QuerySubpart.ID_LOCATION))
			refreshVisuals();
	}

	/**
	 * Updates the visual aspect of this. 
	 */
	protected void refreshVisuals() {
		Point loc = getLogicSubpart().getLocation();
		Dimension size= getLogicSubpart().getSize();
		Rectangle r = new Rectangle(loc ,size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(
			this,
			getFigure(),
			r);
	}
}
