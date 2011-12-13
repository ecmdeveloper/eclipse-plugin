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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.tools.DeselectAllTracker;
import org.eclipse.gef.tools.MarqueeDragTracker;

import com.ecmdeveloper.plugin.search.policies.QueryCommandFactory;
import com.ecmdeveloper.plugin.search.policies.QueryLayoutEditPolicy;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class QueryDiagramEditPart extends AbstractContainerEditPart implements LayerConstants {

	protected void createEditPolicies() {
		
		super.createEditPolicies();

		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new QueryLayoutEditPolicy(
				(XYLayout) getContentPane().getLayoutManager(), new QueryCommandFactory()));
	}

	protected IFigure createFigure() {
		Figure f = new FreeformLayer();
		f.setBorder(new GroupBoxBorder("Diagram"));
		f.setLayoutManager(new FreeformLayout());
		f.setBorder(new MarginBorder(5));
		return f;
	}

	public DragTracker getDragTracker(Request req) {
		if (req instanceof SelectionRequest && ((SelectionRequest) req).getLastButtonPressed() == 3)
			return new DeselectAllTracker(this);
		return new MarqueeDragTracker();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
	}

	protected void refreshVisuals() {
	}
}
