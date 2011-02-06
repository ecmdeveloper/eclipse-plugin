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
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class RoundedCornerFigure extends Figure {

	protected static int DEFAULT_CORNER_SIZE = 10;

	private int cornerSize;
	private boolean fill;
	
	public RoundedCornerFigure() {
		setBackgroundColor(ColorConstants.tooltipBackground);
		setForegroundColor(ColorConstants.tooltipForeground);
		setCornerSize(DEFAULT_CORNER_SIZE);
		setFill(true);
	}

	public int getCornerSize() {
		return cornerSize;
	}

	public void setCornerSize(int newSize) {
		cornerSize = newSize;
	}

	protected void paintFigure(Graphics graphics) {

		Rectangle rect = getBounds().getCopy();
		graphics.translate(getLocation());

		Rectangle roundRectangle = new Rectangle(1,1, rect.width-3, rect.height-3);
		if ( fill )
		{
			graphics.fillRoundRectangle(roundRectangle, cornerSize,cornerSize );
		}
		graphics.drawRoundRectangle(roundRectangle, cornerSize,cornerSize );
		graphics.translate(getLocation().getNegated());
	}

	public void setFill(boolean fill) {
		this.fill = fill;
	}

	public boolean isFill() {
		return fill;
	}
}
