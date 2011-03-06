/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
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

import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.model.QueryContainer;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class GraphicalPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart child = null;

		if (model instanceof QueryContainer) {
			child = new QueryContainerEditPart();
		} else if (model instanceof QueryComponent) {
			child = new QueryComponentEditPart();
		} else if (model instanceof QueryDiagram) {
			child = new QueryDiagramEditPart();
		}
		
		if ( child == null ) {
			throw new IllegalStateException("Couldn't create an edit part for the model object: "
					+ model.getClass().getName());
		}
		child.setModel(model);
		return child;

	}
}
