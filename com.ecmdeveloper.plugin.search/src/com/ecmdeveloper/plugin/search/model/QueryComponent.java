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


/**
 * @author ricardo.belfor
 *
 */
public abstract class QueryComponent extends QuerySubpart{

	private static final long serialVersionUID = 1L;

	private static int count;

	public QueryComponent() {
		super();
		size.width = 200;
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
}
