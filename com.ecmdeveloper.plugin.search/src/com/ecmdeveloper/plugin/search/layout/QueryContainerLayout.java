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

import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author ricardo.belfor
 *
 */
//public class QueryContainerLayout extends AbstractLayout {
public class QueryContainerLayout extends FlowLayout  {

	/** Space in pixels between Figures **/
	protected int spacing;

	public QueryContainerLayout() {
		spacing = 0;
	}

	@SuppressWarnings("unchecked")
	private Dimension calculateChildrenSize(List children, int wHint, int hHint, boolean preferred) {
		int height = 0;
		int width = 0;
		
		for (int i = 0; i < children.size(); i++) {
			IFigure child = (IFigure)children.get(i);
			Dimension childSize = preferred ? getChildPreferredSize(child, wHint, hHint)
					: getChildMinimumSize(child, wHint, hHint);
			height += childSize.height;
			width = Math.max(width, childSize.width);
		}
		return new Dimension(width, height);
	}

	/**
	 * Calculates the minimum size of the container based on the given hints. If this is a
	 * vertically-oriented Toolbar Layout, then only the widthHint is respected (which means
	 * that the children can be as tall as they desire).   In this case, the minimum width
	 * is that of the widest child, and the minimum height is the sum of the minimum
	 * heights of all children, plus the spacing between them. The border and insets of the
	 * container figure are also accounted for.
	 * 
	 * @param container the figure whose minimum size has to be calculated
	 * @param wHint the width hint (the desired width of the container)
	 * @param hHint the height hint (the desired height of the container)
	 * @return the minimum size of the container
	 * @see #getMinimumSize(IFigure, int, int)
	 * @since 2.1
	 */
	@SuppressWarnings("unchecked")
	protected Dimension calculateMinimumSize(IFigure container, int wHint, int hHint) {
		Insets insets = container.getInsets();
//		if (isHorizontal()) {
//			wHint = -1;
//			if (hHint >= 0)
//				hHint = Math.max(0, hHint - insets.getHeight());
//		} else {
			hHint = -1;
			if (wHint >= 0)
				wHint = Math.max(0, wHint - insets.getWidth());
//		}
		
		List children = container.getChildren();
		Dimension minSize = calculateChildrenSize(children, wHint, hHint, false);
		// Do a second pass, if necessary
		if (wHint >= 0 && minSize.width > wHint) {
			minSize = calculateChildrenSize(children, minSize.width, hHint, false);	
		} else if (hHint >= 0 && minSize.width > hHint) {
			minSize = calculateChildrenSize(children, wHint, minSize.width, false);
		}
		
		minSize.height += Math.max(0, children.size() - 1) * spacing;
		return minSize.expand(insets.getWidth(), insets.getHeight()).union(
				getBorderPreferredSize(container));
	}

	/** 
	 * Calculates the preferred size of the container based on the given hints. If this is a
	 * vertically-oriented Toolbar Layout, then only the widthHint is respected (which means
	 * that the children can be as tall as they desire).   In this case, the preferred width
	 * is that of the widest child, and the preferred height is the sum of the preferred
	 * heights of all children, plus the spacing between them.  The border and insets of the
	 * container figure are also accounted for.
	 * 
	 * @param container the figure whose preferred size has to be calculated
	 * @param wHint the width hint (the desired width of the container)
	 * @param hHint the height hint (the desired height of the container)
	 * @return the preferred size of the container
	 * @see #getPreferredSize(IFigure, int, int)
	 * @since 2.0
	 */
	@SuppressWarnings("unchecked")
	protected Dimension calculatePreferredSize(IFigure container, int wHint, int hHint) {
		Insets insets = container.getInsets();
//		if (isHorizontal()) {
//			wHint = -1;
//			if (hHint >= 0)
//				hHint = Math.max(0, hHint - insets.getHeight());
//		} else {
			hHint = -1;
			if (wHint >= 0)
				wHint = Math.max(0, wHint - insets.getWidth());
//		}
		
		List children = container.getChildren();
		Dimension prefSize = calculateChildrenSize(children, wHint, hHint, true);
		// Do a second pass, if necessary
		if (wHint >= 0 && prefSize.width > wHint) {
			prefSize = calculateChildrenSize(children, prefSize.width, hHint, true);	
		} else if (hHint >= 0 && prefSize.height > hHint) {
			prefSize = calculateChildrenSize(children, wHint, prefSize.height, true);
		}
		
		prefSize.height += Math.max(0, children.size() - 1) * spacing;

		return prefSize.expand(insets.getWidth(), insets.getHeight()).union(
				getBorderPreferredSize(container));
	}

	protected Dimension getChildMinimumSize(IFigure child, int wHint, int hHint) {
		return child.getMinimumSize(wHint, hHint);
	}

	protected Dimension getChildPreferredSize(IFigure child, int wHint, int hHint) {
		return child.getPreferredSize(wHint, hHint);
	}

//	/**
//	 * @see org.eclipse.draw2d.AbstractHintLayout#isSensitiveHorizontally(IFigure)
//	 */
//	protected boolean isSensitiveHorizontally(IFigure parent) {
//		return true;
//	}
//
//	/**
//	 * @see org.eclipse.draw2d.AbstractHintLayout#isSensitiveVertically(IFigure)
//	 */
//	protected boolean isSensitiveVertically(IFigure parent) {
//		return false;
//	}

	@SuppressWarnings("unchecked")
	public void layout(IFigure parent) {

		List children = parent.getChildren();
		int numChildren = children.size();
		Rectangle clientArea = parent.getClientArea();
		int x = clientArea.x;
		int y = clientArea.y;
		
		int wHint = parent.getClientArea(Rectangle.SINGLETON).width;
		int hHint = -1;    

		int totalHeight = 0;
		int totalMinHeight = 0;
		Dimension prefSizes [] = new Dimension[numChildren];
		Dimension minSizes [] = new Dimension[numChildren];

		for (int i = 0; i < numChildren; i++) {
			
			IFigure child = (IFigure)children.get(i);
			
			prefSizes[i] = getChildPreferredSize(child, wHint, hHint);
			minSizes[i] = getChildMinimumSize(child, wHint, hHint);
			
			totalHeight += prefSizes[i].height;
			totalMinHeight += minSizes[i].height;
		}
		totalHeight += (numChildren - 1) * spacing;
		totalMinHeight += (numChildren - 1) * spacing;

		int availableHeight = clientArea.height;
		int amntExpandHeight = Math.max(totalHeight, totalMinHeight) - availableHeight;

		if (amntExpandHeight < 0) {
			amntExpandHeight = 0;
		}

		if ( amntExpandHeight != 0) {
			Rectangle parentBounds = parent.getBounds();
			parentBounds.height += amntExpandHeight;
			parent.setBounds( parentBounds );
			parent.repaint();
		}
		
		for (int i = 0; i < numChildren; i++) {
			int prefHeight = prefSizes[i].height;
			int minHeight = minSizes[i].height;
			int prefWidth = prefSizes[i].width;
			int minWidth = minSizes[i].width;
			Rectangle newBounds = new Rectangle(x, y, prefWidth, Math.max(prefHeight, minHeight) );

			IFigure child = (IFigure)children.get(i);

		    int width = Math.min(prefWidth, child.getMaximumSize().width);
		    width = child.getMaximumSize().width;
			width = Math.max(minWidth, Math.min(clientArea.width, width));
			newBounds.width = width;
			child.setBounds(newBounds);
			y += newBounds.height + spacing;
		}
	}				

	public int getSpacing() {
		return spacing;
	}

	public void setSpacing(int space) {
		spacing = space;
	}
}
