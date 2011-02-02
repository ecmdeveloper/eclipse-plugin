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

package com.ecmdeveloper.plugin.search.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class QueryContainerFeedbackBorder extends ContainerBorder {

	public QueryContainerFeedbackBorder() {
		super(null, QueryColorConstants.logicGreen);
	}

	public QueryContainerFeedbackBorder(int width) {
		super(width);
	}

	public void paint(IFigure figure, Graphics graphics, Insets insets) {

		graphics.setForegroundColor(ColorConstants.white);
		graphics.setBackgroundColor(QueryColorConstants.ghostFillColor);
		graphics.setXORMode(true);

		Rectangle r = figure.getBounds();
		graphics.drawRectangle(r.x, r.y, r.width - 1, r.height - 1);
		tempRect.setBounds(new Rectangle(r.x, r.y, grabBarWidth, r.height));
		graphics.fillRectangle(tempRect);
	}
}
