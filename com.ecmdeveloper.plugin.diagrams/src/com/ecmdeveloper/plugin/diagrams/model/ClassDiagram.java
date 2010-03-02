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
			connectClassToParent(classDiagramClass);
			connectClassToChildren(classDiagramClass);
			return true;
		}
		return false;
	}

	private void connectClassToParent(ClassDiagramClass classDiagramClass) {
		for ( ClassDiagramClass parentClass : classDiagramClasses ) {
			if ( isChildClass( parentClass,classDiagramClass ) ) {
				parentClass.addChild( classDiagramClass );
				return;
			}
		}
	}

	private void connectClassToChildren(ClassDiagramClass classDiagramClass) {
		for ( ClassDiagramClass childClass : classDiagramClasses ) {
			if ( isChildClass(classDiagramClass, childClass) ) {
				classDiagramClass.addChild(childClass);
			}
		}
	}

	private boolean isChildClass(ClassDiagramClass parentClass, ClassDiagramClass childClass) {
		return childClass.getParentClassId() != null && childClass.getParentClassId().equalsIgnoreCase( parentClass.getId() );
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
			
			ClassDiagramClass classDiagramClass = (ClassDiagramClass) object;
			
			disconnectClassFromChildren(classDiagramClass);
			disconnectClassFromParent(classDiagramClass);
			
			if ( classDiagramClasses.remove( classDiagramClass ) ) {
				firePropertyChange(CHILD_REMOVED_PROP, null, classDiagramClass );
			}
		}
	}

	private void disconnectClassFromParent(ClassDiagramClass classDiagramClass) {
		if ( classDiagramClass.getParentRelation() != null ) {
			InheritRelationship parentRelation = classDiagramClass.getParentRelation();
			classDiagramClass.disconnectParent();
			parentRelation.getParent().disconnectChild(parentRelation);
		}
	}

	private void disconnectClassFromChildren(ClassDiagramClass classDiagramClass) {
		
		ArrayList<InheritRelationship> oldRelationships = new ArrayList<InheritRelationship>(); 
		for ( InheritRelationship childRelation : classDiagramClass.getChildRelations() ) {
			childRelation.getChild().disconnectParent();
			oldRelationships.add(childRelation);
		}
		
		for ( InheritRelationship oldRelation : oldRelationships ) {
			classDiagramClass.disconnectChild(oldRelation);
		}
	}

	public void addClassDiagramElement(ClassDiagramElement object) {
		if ( object instanceof ClassDiagramClass ) {
			addClassDiagramClass((ClassDiagramClass) object);
		}
	}
}
