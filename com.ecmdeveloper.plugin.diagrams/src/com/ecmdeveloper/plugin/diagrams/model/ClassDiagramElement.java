/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.diagrams.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public abstract class ClassDiagramElement extends ClassDiagramBase {

	private static IPropertyDescriptor[] descriptors;
	
	private static final String HEIGHT_PROP = "ClassDiagramElement.Height";
	private static final String WIDTH_PROP = "ClassDiagramElement.Width";
	private static final String XPOS_PROP = "ClassDiagramElement.xPos";
	private static final String YPOS_PROP = "ClassDiagramElement.yPos";

	public static final String LOCATION_PROP = "ClassDiagramElement.Location";
	public static final String SIZE_PROP = "ClassDiagramElement.Size";
	
	public static final String SOURCE_CONNECTIONS_PROP = "ClassDiagramElement.SourceConn";
	public static final String TARGET_CONNECTIONS_PROP = "ClassDiagramElement.TargetConn";

	static {
		descriptors = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(XPOS_PROP, "X"), // id and description pair
				new TextPropertyDescriptor(YPOS_PROP, "Y"),
				new TextPropertyDescriptor(WIDTH_PROP, "Width"),
				new TextPropertyDescriptor(HEIGHT_PROP, "Height"),
		};
		// use a custom cell editor validator for all four array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
				public String isValid(Object value) {
					int intValue = -1;
					try {
						intValue = Integer.parseInt((String) value);
					} catch (NumberFormatException exc) {
						return "Not a number";
					}
					return (intValue >= 0) ? null : "Value must be >=  0";
				}
			});
		}
	}

	/** Location of this shape. */
	private Point location = new Point(0, 0);
	/** Size of this shape. */
	private Dimension size = new Dimension(50, 50);
	/** List of outgoing Connections. */
	private List sourceConnections = new ArrayList();
	/** List of incoming Connections. */
	private List targetConnections = new ArrayList();
	
	protected ClassDiagram parent;
	
	public void setParent( ClassDiagram parent ) {
		this.parent = parent;
	}
	
	public ClassDiagram getParent() {
		return parent;
	}
	
	public Point getLocation() {
		return location.getCopy();
	}

	public void setLocation(Point location) {
		if (location == null) {
			throw new IllegalArgumentException();
		}
		this.location.setLocation(location);
		firePropertyChange(LOCATION_PROP, null, this.location);
	}

	public Dimension getSize() {
		return size.getCopy();
	}

	public void setSize(Dimension size) {
		if (size != null) {
			this.size.setSize(size);
			firePropertyChange(SIZE_PROP, null, this.size);
		}
	}

	/**
	 * Returns an array of IPropertyDescriptors for this shape.
	 * <p>The returned array is used to fill the property view, when the edit-part corresponding
	 * to this model element is selected.</p>
	 * 
	 * @return the property descriptors
	 * 
	 * @see #descriptors
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/**
	 * Return the property value for the given propertyId, or null.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array 
	 * to obtain the value of the corresponding properties.</p>
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public Object getPropertyValue(Object propertyId) {
		if (XPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.x);
		}
		if (YPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.y);
		}
		if (HEIGHT_PROP.equals(propertyId)) {
			return Integer.toString(size.height);
		}
		if (WIDTH_PROP.equals(propertyId)) {
			return Integer.toString(size.width);
		}
		return null;
	}

	/**
	 * Set the property value for the given property id.
	 * If no matching id is found, the call is forwarded to the superclass.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array to set the values
	 * of the corresponding properties.</p>
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public void setPropertyValue(Object propertyId, Object value) {
		if (XPOS_PROP.equals(propertyId)) {
			int x = Integer.parseInt((String) value);
			setLocation(new Point(x, location.y));
		} else if (YPOS_PROP.equals(propertyId)) {
			int y = Integer.parseInt((String) value);
			setLocation(new Point(location.x, y));
		} else if (HEIGHT_PROP.equals(propertyId)) {
			int height = Integer.parseInt((String) value);
			setSize(new Dimension(size.width, height));
		} else if (WIDTH_PROP.equals(propertyId)) {
			int width = Integer.parseInt((String) value);
			setSize(new Dimension(width, size.height));
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// Do nothing
	}

	public String getName() {
		return null;
	}
}
