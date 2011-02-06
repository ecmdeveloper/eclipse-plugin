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

package com.ecmdeveloper.plugin.search.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * 
 * @author ricardo.belfor
 * 
 */
public class QueryDiagram extends QuerySubpart {
	
	static final long serialVersionUID = 1;

	private static int count;
	private static Image LOGIC_ICON = createImage(QueryDiagram.class, "icons/circuit16.gif"); //$NON-NLS-1$

	protected List children = new ArrayList();
	// protected LogicRuler leftRuler, topRuler;
	protected Integer connectionRouter = null;
	private boolean rulersVisibility = false;
	private boolean snapToGeometry = false;
	private boolean gridEnabled = false;
	private double zoom = 1.0;

	public QueryDiagram(Query query) {
		super(query);
		
		size.width = 400;
		size.height = 100;
		location.x = 20;
		location.y = 20;
		// createRulers();
	}

	public void addChild(QueryElement child) {
		addChild(child, -1);
	}

	public void addChild(QueryElement child, int index) {
		if (index >= 0)
			children.add(index, child);
		else
			children.add(child);
		fireChildAdded(CHILDREN, child, new Integer(index));
	}

	// protected void createRulers() {
	// leftRuler = new LogicRuler(false);
	// topRuler = new LogicRuler(true);
	// }

	public List getChildren() {
		return children;
	}

	public Image getIconImage() {
		return LOGIC_ICON;
	}

	public String getNewID() {
		return Integer.toString(count++);
	}

	public double getZoom() {
		return zoom;
	}

	/**
	 * Returns <code>null</code> for this model. Returns normal descriptors for
	 * all subclasses.
	 * 
	 * @return Array of property descriptors.
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		// if(getClass().equals(LogicDiagram.class)){
		// ComboBoxPropertyDescriptor cbd = new ComboBoxPropertyDescriptor(
		// ID_ROUTER,
		// LogicMessages.PropertyDescriptor_LogicDiagram_ConnectionRouter,
		// new String[]{
		// LogicMessages.PropertyDescriptor_LogicDiagram_Manual,
		// LogicMessages.PropertyDescriptor_LogicDiagram_Manhattan,
		// LogicMessages.PropertyDescriptor_LogicDiagram_ShortestPath});
		// cbd.setLabelProvider(new ConnectionRouterLabelProvider());
		// return new IPropertyDescriptor[]{cbd};
		// }
		return super.getPropertyDescriptors();
	}

	public Object getPropertyValue(Object propName) {
		return super.getPropertyValue(propName);
	}

	// public LogicRuler getRuler(int orientation) {
	// LogicRuler result = null;
	// switch (orientation) {
	// case PositionConstants.NORTH :
	// result = topRuler;
	// break;
	// case PositionConstants.WEST :
	// result = leftRuler;
	// break;
	// }
	// return result;
	// }

	public boolean getRulerVisibility() {
		return rulersVisibility;
	}

	public boolean isGridEnabled() {
		return gridEnabled;
	}

	public boolean isSnapToGeometryEnabled() {
		return snapToGeometry;
	}

	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
	}

	public void removeChild(QueryElement child) {
		children.remove(child);
		fireChildRemoved(CHILDREN, child);
	}

	public void setPropertyValue(Object id, Object value) {
		super.setPropertyValue(id, value);
	}

	public void setRulerVisibility(boolean newValue) {
		rulersVisibility = newValue;
	}

	public void setGridEnabled(boolean isEnabled) {
		gridEnabled = isEnabled;
	}

	public void setSnapToGeometry(boolean isEnabled) {
		snapToGeometry = isEnabled;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}
}
