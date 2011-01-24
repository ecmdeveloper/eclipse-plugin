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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import com.ecmdeveloper.plugin.search.layout.QueryLayoutManager;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.policies.QueryLayoutEditPolicy;

/**
 * @author ricardo.belfor
 *
 */
public class QueryEditPart extends AbstractGraphicalEditPart {

	public QueryEditPart(Query model) {
		setModel(model);
	}
	
	public Query getQuery() {
		return (Query) getModel();
	}
	
	@Override
	protected IFigure createFigure() {
		   IFigure figure = new FreeformLayer();
		   figure.setLayoutManager(new QueryLayoutManager() );
		   return figure;
	}

	@Override
	protected void createEditPolicies() {
		
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ContainerEditPolicy() {

			@Override
			protected Command getCreateCommand(CreateRequest arg0) {
				return null;
			}
			
		});
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());    	
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new QueryLayoutEditPolicy());		
	}

	@Override
	protected List<Object> getModelChildren() {
		ArrayList<Object> modelChildren = new ArrayList<Object>();
		modelChildren.add(getQuery().getQueryOperation() );
		return modelChildren;
	}
}
