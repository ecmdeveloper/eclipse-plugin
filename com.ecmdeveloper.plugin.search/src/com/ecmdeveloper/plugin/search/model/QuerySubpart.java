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
import java.io.InputStream;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public abstract class QuerySubpart extends QueryElement {

	private String id;
	protected Point location = new Point(0, 0);
	static final long serialVersionUID = 1;
	protected Dimension size = new Dimension(-1, -1);

	protected static IPropertyDescriptor[] descriptors = null;
	public static String ID_SIZE = "size"; //$NON-NLS-1$
	public static String ID_LOCATION = "location"; //$NON-NLS-1$
	public static String ID_ENABLEMENT = "enablement"; //$NON-NLS-1$

	static {
		descriptors = new IPropertyDescriptor[] { new PropertyDescriptor(ID_SIZE, "Size"),
				new PropertyDescriptor(ID_LOCATION, "Location") };
	}

	protected static Image createImage(Class rsrcClass, String name) {
		InputStream stream = rsrcClass.getResourceAsStream(name);
		Image image = new Image(null, stream);
		try {
			stream.close();
		} catch (IOException ioe) {
		}
		return image;
	}

	public QuerySubpart(Query query) {
		super(query);
		setID(getNewID());
	}

	public Image getIcon() {
		return getIconImage();
	}

	abstract public Image getIconImage();

	public String getID() {
		return id;
	}

	public Point getLocation() {
		return location;
	}

	abstract protected String getNewID();

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	public Object getPropertyValue(Object propName) {
		if (ID_SIZE.equals(propName))
			return new DimensionPropertySource(getSize());
		else if (ID_LOCATION.equals(propName))
			return new LocationPropertySource(getLocation());
		return null;
	}

	public Dimension getSize() {
		return size;
	}

	public boolean isPropertySet() {
		return true;
	}

	/*
	 * Does nothing for the present, but could be used to reset the properties
	 * of this to whatever values are desired.
	 * 
	 * @param id Parameter which is to be reset.
	 * 
	 * public void resetPropertyValue(Object id){ if(ID_SIZE.equals(id)){;}
	 * if(ID_LOCATION.equals(id)){;} }
	 */

	public void setID(String s) {
		id = s;
	}

	public void setLocation(Point p) {
		if (location.equals(p))
			return;
		location = p;
		firePropertyChange("location", null, p);
	}

	public void setPropertyValue(Object id, Object value) {
		if (ID_SIZE.equals(id))
			setSize((Dimension) value);
		else if (ID_LOCATION.equals(id))
			setLocation((Point) value);
	}

	public void setSize(Dimension d) {
		if (size.equals(d))
			return;
		size = d;
		firePropertyChange(ID_SIZE, null, size); //$NON-NLS-1$
	}
}
