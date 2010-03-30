/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.model;

/**
 * @author Ricardo.Belfor
 *
 */
public class InheritRelationship extends ClassDiagramBase {

	public static String VISIBLE_PROPERTY = "InheritRelationship.Visible";
	
	private ClassDiagramClass parent;
	private ClassDiagramClass child;
	private boolean visible = true;
	
	public InheritRelationship(ClassDiagramClass parent, ClassDiagramClass child) {
		this.parent = parent;
		this.child = child;
		visible = child.isParentVisible();
	}

	public ClassDiagramClass getParent() {
		return parent;
	}

	public ClassDiagramClass getChild() {
		return child;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		child.setParentVisible( visible );
		firePropertyChange(VISIBLE_PROPERTY, null, new Boolean( visible ) );
	}
}
