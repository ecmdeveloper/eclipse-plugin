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
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author ricardo.belfor
 *
 */
public class QueryConditionFigure extends Figure {
	
	private Label labelName = new Label();

	public QueryConditionFigure() {
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);
		add(labelName); 
		setConstraint(labelName, new Rectangle(5, 5, -1, -1));
		setBorder(new LineBorder(1));
	}
	
	public void setName(String name) {
		labelName.setText(name);
	}
	
	public void setLayout(Rectangle rect) {
		getParent().setConstraint(this, rect);
	}	
}
