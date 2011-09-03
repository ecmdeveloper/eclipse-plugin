/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Color;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class NoteFigure extends Figure {
	
	private static final int INNER_MARGIN = 2;
	private static final int FOLDED_CORNER_LENGTH = 12;
	public static Color noteColor = new Color(null, 255, 255, 206);
   
	private TextFlow textFlow;
	private FlowPage flowPage;

	public NoteFigure() {
		super();

		setBackgroundColor(noteColor);
		setOpaque(true);

		flowPage = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow, ParagraphTextLayout.WORD_WRAP_SOFT));
		flowPage.add(textFlow);
		setLayoutManager(new FreeformLayout());
		add(flowPage);
	}

	public void setText(String text) {
		textFlow.setText(text);
	}
	
	protected void paintFigure(Graphics g) {
		super.paintFigure(g);

		Rectangle r = getClientArea();
		drawRectangleWithoutTopLeftCorner(g, r);
		drawFoldedCorner(g, r);

		setConstraint(flowPage, new Rectangle(r.x + INNER_MARGIN, r.y + INNER_MARGIN, r.width
				- FOLDED_CORNER_LENGTH, r.height - INNER_MARGIN));
	}

	private void drawRectangleWithoutTopLeftCorner(Graphics g, Rectangle r) {
		g.drawLine(r.x, r.y, getFoldLeft(r), r.y); // top
		g.drawLine(r.x, r.y, r.x, getNoteBottom(r)); // left
		g.drawLine(r.x, getNoteBottom(r), getFoldRight(r), getNoteBottom(r)); // bottom
		g.drawLine(getFoldRight(r), getFoldBottom(r) - 1, getFoldRight(r), getNoteBottom(r)); // right
	}

	private void drawFoldedCorner(Graphics g, Rectangle r) {
		
		Point topLeftCorner = new Point(getFoldLeft(r), r.y);
		Point bottomLeftCorner = new Point(getFoldLeft(r), getFoldBottom(r));
		Point bottomRightCorner = new Point(getFoldRight(r), getFoldBottom(r));
		Point topRightCorner  = new Point(getNoteRight(r), r.y);
		
		PointList trianglePolygon;
		trianglePolygon = new PointList(3);
		trianglePolygon.addPoint(topLeftCorner);
		trianglePolygon.addPoint(topRightCorner);
		trianglePolygon.addPoint(bottomRightCorner);
	
		g.setBackgroundColor(noteColor);
		g.fillPolygon(trianglePolygon);
	
		g.drawLine(topLeftCorner, bottomLeftCorner);
		g.drawLine(bottomLeftCorner, bottomRightCorner);
		g.drawLine(bottomRightCorner, topLeftCorner);
	}

	private int getNoteBottom(Rectangle r) {
		return r.y + r.height - 1;
	}

   	private int getNoteRight(Rectangle r) {
		return r.x + r.width;
	}

	private int getFoldBottom(Rectangle r) {
		return r.y + FOLDED_CORNER_LENGTH;
	}

	private int getFoldRight(Rectangle r) {
		return getNoteRight(r) - 1;
	}

	private int getFoldLeft(Rectangle r) {
		return getNoteRight(r) - FOLDED_CORNER_LENGTH - 1;
	}
}