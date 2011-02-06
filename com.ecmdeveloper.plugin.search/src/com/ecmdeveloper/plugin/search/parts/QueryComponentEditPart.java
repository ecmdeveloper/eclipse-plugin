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

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import com.ecmdeveloper.plugin.search.figures.TextFigure;
import com.ecmdeveloper.plugin.search.policies.QueryTextEditPolicy;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class QueryComponentEditPart extends LogicEditPart {

	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getValue(AccessibleControlEvent e) {
				e.result = getModel().toString();
			}

			public void getName(AccessibleEvent e) {
				e.result = "Label";
			}
		};
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		// installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new
		// LabelDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new QueryTextEditPolicy());
	}

	protected IFigure createFigure() {
		TextFigure label = new TextFigure();
		return label;
	}

	private void performDirectEdit() {
		// new LogicLabelEditManager(this,
		// new LabelCellEditorLocator((StickyNoteFigure)getFigure())).show();
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("labelContents"))//$NON-NLS-1$
			refreshVisuals();
		else
			super.propertyChange(evt);
	}

	protected void refreshVisuals() {
		((TextFigure) getFigure()).setText(getModel().toString());
		super.refreshVisuals();
	}
}
