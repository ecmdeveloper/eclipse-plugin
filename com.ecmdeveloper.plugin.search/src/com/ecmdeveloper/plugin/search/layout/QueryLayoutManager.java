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

package com.ecmdeveloper.plugin.search.layout;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * @author ricardo.belfor
 *
 */
public class QueryLayoutManager extends ToolbarLayout {

	@Override
	protected Dimension calculateMinimumSize(IFigure container, int hint, int hint2) {
		// TODO Auto-generated method stub
		return super.calculateMinimumSize(container, hint, hint2);
	}

	@Override
	protected Dimension getChildMinimumSize(IFigure child, int hint, int hint2) {
		// TODO Auto-generated method stub
		return super.getChildMinimumSize(child, hint, hint2);
	}

	@Override
	protected Dimension getChildPreferredSize(IFigure child, int hint, int hint2) {
		// TODO Auto-generated method stub
		return super.getChildPreferredSize(child, hint, hint2);
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int hint, int hint2) {
		Dimension calculatePreferredSize = super.calculatePreferredSize(container, hint, hint2);
		return calculatePreferredSize;
	}

	
//	@Override
//	protected Dimension calculatePreferredSize(IFigure arg0, int arg1, int arg2) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void layout(IFigure arg0) {
//		// TODO Auto-generated method stub
//		
//	}

}
