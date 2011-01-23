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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryCondition;
import com.ecmdeveloper.plugin.search.model.QueryOperation;

/**
 * @author ricardo.belfor
 *
 */
public class QueryEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {

		if ( model instanceof Query ) 
			return new QueryEditPart((Query) model);
		
		if ( model instanceof QueryOperation ) 
			return new QueryOperationEditPart((QueryOperation) model);
		
		if ( model instanceof QueryCondition ) 
			return new QueryConditionEditPart((QueryCondition) model);

		throw new IllegalStateException(
				"Couldn't create an edit part for the model object: "
						+ model.getClass().getName());
	}

}
