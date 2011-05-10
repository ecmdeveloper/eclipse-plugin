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

package com.ecmdeveloper.plugin.search.model;

import java.io.IOException;

import org.eclipse.draw2d.geometry.Dimension;

import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;


/**
 * @author ricardo.belfor
 *
 */
public abstract class QueryComponent extends QuerySubpart{

	public static final String FIELD_CHANGED = "fieldChanged";
	public static final String PROPERTY_CHANGED = "propertyChanged";
	
	private static final long serialVersionUID = 1L;

	private static int count;
	private IQueryField field;

	public QueryComponent(Query query) {
		super(query);
		size.width = 200;
	}

	public void setField(IQueryField queryField) {
		IQueryField oldField = field;
		this.field = queryField;
		firePropertyChange(FIELD_CHANGED, oldField, queryField);
	}

	public IQueryField getField() {
		return field;
	}
	
	protected String getNewID() {
		return Integer.toString(count++);
	}

	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
	}

	public Dimension getSize() {
		return new Dimension(size.width, -1);
	}

	public void setSize(Dimension d) {
		d.height = -1;
		super.setSize(d);
	}

	protected void appendField(StringBuffer result, boolean strict) {
		if ( strict ) {
			result.append("[");
		}
		result.append(getField());
		if ( strict ) {
			result.append("]");
		}
	}
	
	public abstract QueryComponentType getType();
}
