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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.ecmdeveloper.plugin.search.figures.QueryConditionFigure;
import com.ecmdeveloper.plugin.search.figures.QueryOperationFigure;
import com.ecmdeveloper.plugin.search.model.QueryOperation;
import com.ecmdeveloper.plugin.search.policies.QueryComponentEditPolicy;
import com.ecmdeveloper.plugin.search.policies.QueryLayoutEditPolicy;

/**
 * @author ricardo.belfor
 *
 */
public class QueryOperationEditPart extends QueryBaseEditPart {

	public QueryOperationEditPart(QueryOperation model) {
		super();
		setModel(model);
	}

	public QueryOperation getQueryOperation() {
		return (QueryOperation) getModel();
	}

	@Override
	protected IFigure createFigure() {
		QueryOperationFigure figure = new QueryOperationFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new QueryComponentEditPolicy() );
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new QueryLayoutEditPolicy() );	
	}

	@Override
	protected List<Object> getModelChildren() {
		ArrayList<Object> modelChildren = new ArrayList<Object>();
		modelChildren.addAll( getQueryOperation().getQueryParts() );
		return modelChildren;
	}

	protected void refreshVisuals() { 
		QueryOperationFigure figure = (QueryOperationFigure) getFigure();
		figure.setLayout( new Rectangle(50,50, 200, 400) );
		figure.setName( getModel().toString() );
	}
	
}
