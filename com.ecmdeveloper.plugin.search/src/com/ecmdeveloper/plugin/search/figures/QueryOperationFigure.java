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

package com.ecmdeveloper.plugin.search.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author ricardo.belfor
 *
 */
public class QueryOperationFigure extends Figure {
	
	private static final int BORDER_SIZE = 5;
	private static final int INSET_SIZE = 2;
	private static final Insets CLIENT_AREA_INSETS = new Insets(INSET_SIZE, INSET_SIZE, INSET_SIZE, INSET_SIZE);
	private static final int OPERATION_WIDTH = 30;

	public static final int OPERATION_OFFSET = OPERATION_WIDTH + 2*INSET_SIZE + BORDER_SIZE;

	private Label labelName = new Label();

	public QueryOperationFigure() {
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);

//      FlowLayout layout = new FlowLayout();
//      layout.setMajorAlignment(FlowLayout.ALIGN_CENTER);
//      layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
//      setLayoutManager(layout);
//      labelName.setTextAlignment(PositionConstants.LEFT);
      add(labelName);
      setConstraint(labelName, new Rectangle(5, 5, -1, -1));
		
//		setBorder(new LineBorder(BORDER_SIZE));
	}
	
	public void setName(String name) {
		labelName.setText(name);
	}
	
	@Override
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);

		Rectangle clientArea = getClientArea();
		clientArea.setSize( OPERATION_WIDTH, clientArea.getSize().height );
//		clientArea.crop(CLIENT_AREA_INSETS);
		graphics.drawRoundRectangle(clientArea,10,10);
		// TODO compute exact position
		labelName.setLocation( new Point( clientArea.x + 4, clientArea.y + clientArea.height/2 - 10 ));
	}

	public void setLayout(Rectangle rect) { 
		setBounds(rect); 
	}	
}
