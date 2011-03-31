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
	
	private List<QueryElement> children = new ArrayList<QueryElement>();

	public QueryDiagram(Query query) {
		super(query);
		
		size.width = 400;
		size.height = 100;
		location.x = 20;
		location.y = 20;
	}

	public void addChild(QueryElement child) {
		addChild(child, -1);
	}

	public void addChild(QueryElement child, int index) {

		child.setParent(this);

		if (index >= 0) {
			children.add(index, child);
		} else {
			children.add(child);
		}
		
		fireChildAdded(CHILDREN, child, new Integer(index));
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	public void removeChild(QueryElement child) {
		child.setParent(null);
		children.remove(child);
		fireChildRemoved(CHILDREN, child);
	}

	public List<QueryElement> getChildren() {
		return children;
	}

	public Image getIconImage() {
		return LOGIC_ICON;
	}

	public String getNewID() {
		return Integer.toString(count++);
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return super.getPropertyDescriptors();
	}

	public Object getPropertyValue(Object propName) {
		return super.getPropertyValue(propName);
	}

	private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
	}

	public void setPropertyValue(Object id, Object value) {
		super.setPropertyValue(id, value);
	}

	@Override
	public String toSQL() {
		
		List<QueryElement> children = getChildren();
		if ( !children.isEmpty() ) {
			
			if ( children.size() == 1 ) {
				return children.iterator().next().toSQL();
			} else {
				for ( QueryElement child : children ) {
					if ( child.isMainQuery() ) {
						return child.toSQL();
					}
				}
			}
		}
		return "";
	}

	public void refresh() {
		super.refresh();
		for ( QueryElement child : children ) {
			child.refresh();
		}
	}
}
