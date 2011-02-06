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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Color;

import com.ecmdeveloper.plugin.search.figures.ContainerBorder;
import com.ecmdeveloper.plugin.search.figures.QueryColorConstants;
import com.ecmdeveloper.plugin.search.model.AndContainer;
import com.ecmdeveloper.plugin.search.model.OrContainer;
import com.ecmdeveloper.plugin.search.policies.ContainerHighlightEditPolicy;
import com.ecmdeveloper.plugin.search.policies.QueryCommandFactory;
import com.ecmdeveloper.plugin.search.policies.QueryContainerEditPolicy;


/**
 * @author ricardo.belfor
 *
 */
public class QueryContainerEditPart extends AbstractContainerEditPart {

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new QueryContainerEditPolicy( new QueryCommandFactory() ));
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ContainerHighlightEditPolicy());
	}

	protected IFigure createFigure() {
		Figure figure = new Figure();
		figure.setLayoutManager(new FlowLayout());
		figure.setBorder(new ContainerBorder( getModel().toString(), getContainerColor() ));
		figure.setOpaque(true);
		figure.setPreferredSize(400, 50);

		return figure;
	}

	private Color getContainerColor() {
		if ( getModel() instanceof OrContainer) {
			return QueryColorConstants.orContainer;
		} else if ( getModel() instanceof AndContainer) {
			return QueryColorConstants.andContainer;
		}
		return QueryColorConstants.logicGreen;
	}
}
