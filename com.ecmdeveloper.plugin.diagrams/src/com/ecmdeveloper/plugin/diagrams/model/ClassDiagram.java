/**
 * Copyright 2009, Ricardo Belfor
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagram extends ClassDiagramBase {

	/** Property ID to use when a child is added to this diagram. */
	public static final String CHILD_ADDED_PROP = "ClassDiagram.ChildAdded";
	/** Property ID to use when a child is removed from this diagram. */
	public static final String CHILD_REMOVED_PROP = "ClassDiagram.ChildRemoved";
	
	private ArrayList<ClassDiagramClass> classDiagramClasses = new ArrayList<ClassDiagramClass>();
	
	public boolean addClassDiagramClass( ClassDiagramClass classDiagramClass )
	{
		if ( classDiagramClass != null && classDiagramClasses.add(classDiagramClass) ) {
			classDiagramClass.setParent(this);
			firePropertyChange(CHILD_ADDED_PROP, null, classDiagramClass);
			return true;
		}
		return false;
	}

	public List<ClassDiagramClass> getClassDiagramClasses() {
		return classDiagramClasses;
	}

	@Override
	public Object getPropertyValue(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub
		
	}

	public void deleteClassDiagramElement(ClassDiagramElement object) {
		if ( object instanceof ClassDiagramClass ) {
			if ( classDiagramClasses.remove( object ) ) {
				firePropertyChange(CHILD_REMOVED_PROP, null, object );
			}
		}
	}

	public void addClassDiagramElement(ClassDiagramElement object) {
		if ( object instanceof ClassDiagramClass ) {
			addClassDiagramClass((ClassDiagramClass) object);
		}
	}
}
