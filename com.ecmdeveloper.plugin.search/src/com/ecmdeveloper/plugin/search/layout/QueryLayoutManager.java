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

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.ecmdeveloper.plugin.search.figures.QueryConditionFigure;
import com.ecmdeveloper.plugin.search.figures.QueryOperationFigure;

/**
 * @author ricardo.belfor
 *
 */
public class QueryLayoutManager extends AbstractLayout {

	
	@Override
	protected Dimension calculatePreferredSize(IFigure figure, int arg1, int arg2) {
		System.out.println( "calculatePreferredSize : "+ figure.toString() );
		return null;
	}

	@Override
	public void layout(IFigure figure) {
		System.out.println( "layout: " + figure.toString() );
		layoutFigure(figure, 0, 5 );
		
//		for ( Object childFigure : figure.getChildren() )
//		{
//			System.out.println( childFigure.toString() );
//			if ( childFigure instanceof QueryOperationFigure ) {

//				QueryOperationFigure queryOperationFigure = (QueryOperationFigure) childFigure;
//				queryOperationFigure.setBounds( new Rectangle(10,10, 400, 400) );
//				
//				int i = 0;
//				for ( Object childFigure2 : queryOperationFigure.getChildren() ) {
//					IFigure childFigure22 = (IFigure)childFigure2;
//					childFigure22.setBounds( new Rectangle( new Point(10, (i++)*40), childFigure22.getSize() ) );
//				}
//			}
			
//		}
	}
	
	private Rectangle layoutFigure(IFigure parentFigure, int left, int top ) {

		int bottom = top;
		
		System.out.println( "bottomY 1: " + bottom );
		for ( Object childFigure : parentFigure.getChildren() ) {
			
			if (childFigure instanceof QueryOperationFigure) {
				Rectangle rectangle = layoutFigure( (QueryOperationFigure) childFigure, left + QueryOperationFigure.OPERATION_OFFSET,  bottom );
				bottom = rectangle.bottom();
				System.out.println( "bottomY 2: " + bottom );
			} else if ( childFigure instanceof QueryConditionFigure ) { 
				IFigure childFigure2 = (IFigure)childFigure;
				Point position = new Point(left + QueryOperationFigure.OPERATION_OFFSET, bottom);
				Rectangle bounds = new Rectangle( position, childFigure2.getSize() );
				childFigure2.setBounds( bounds );
				bottom += childFigure2.getSize().height;
				System.out.println( "bottomY 3: " + bottom );
			}
		}

		Rectangle parentBounds = new Rectangle(left,top, 400, bottom - top);
		parentFigure.setBounds( parentBounds );
		System.out.println( "parentBounds: " + parentBounds.toString() );
		
		return parentBounds;
	}

}
