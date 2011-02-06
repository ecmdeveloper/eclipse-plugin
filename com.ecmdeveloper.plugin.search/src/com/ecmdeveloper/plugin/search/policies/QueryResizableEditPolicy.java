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
package com.ecmdeveloper.plugin.search.policies;

import java.util.Iterator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;

import com.ecmdeveloper.plugin.search.figures.QueryColorConstants;
import com.ecmdeveloper.plugin.search.figures.QueryContainerFeedbackFigure;
import com.ecmdeveloper.plugin.search.figures.RoundedCornerFeedbackFigure;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.QueryContainer;

/**
 * 
 * @author ricardo.belfor
 * 
 */
public class QueryResizableEditPolicy extends ResizableEditPolicy {

	protected IFigure createDragSourceFeedbackFigure() {
		IFigure figure = createFigure((GraphicalEditPart) getHost(), null);
		figure.setBounds(getInitialFeedbackBounds());
		addFeedback(figure);
		return figure;
	}

	@SuppressWarnings("unchecked")
	protected IFigure createFigure(GraphicalEditPart part, IFigure parent) {

		IFigure child = getCustomFeedbackFigure(part.getModel());

		if (parent != null)
			parent.add(child);

		Rectangle childBounds = part.getFigure().getBounds().getCopy();
		IFigure walker = part.getFigure().getParent();

		while (walker != ((GraphicalEditPart) part.getParent()).getFigure()) {
			walker.translateToParent(childBounds);
			walker = walker.getParent();
		}

		child.setBounds(childBounds);

		Iterator i = part.getChildren().iterator();

		while (i.hasNext()) {
			createFigure((GraphicalEditPart) i.next(), child);
		}
		
		return child;
	}

	protected IFigure getCustomFeedbackFigure(Object modelPart) {
		
		IFigure figure;

		if (modelPart instanceof QueryContainer) {
			figure = new QueryContainerFeedbackFigure();
		} else if (modelPart instanceof Comparison) {
			figure = new RoundedCornerFeedbackFigure();
		}else {
			figure = new RectangleFigure();
			((RectangleFigure) figure).setXOR(true);
			((RectangleFigure) figure).setFill(true);
			figure.setBackgroundColor(QueryColorConstants.ghostFillColor);
			figure.setForegroundColor(ColorConstants.white);
		}

		return figure;
	}

	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}

	protected Rectangle getInitialFeedbackBounds() {
		return getHostFigure().getBounds();
	}
}
