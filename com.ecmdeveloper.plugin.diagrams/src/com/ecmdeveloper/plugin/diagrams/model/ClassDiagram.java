/**
 * Copyright 2009,2010, Ricardo Belfor
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
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagram extends ClassDiagramBase {


	private static IPropertyDescriptor[] descriptors;
	
	public static final String CHILD_ADDED_PROP = "ClassDiagram.ChildAdded";
	public static final String CHILD_REMOVED_PROP = "ClassDiagram.ChildRemoved";
	
	private ArrayList<ClassDiagramClass> classDiagramClasses = new ArrayList<ClassDiagramClass>();
	private ArrayList<ClassDiagramNote> classDiagramNotes = new ArrayList<ClassDiagramNote>();
	
	public static final String DEFAULT_FILL_COLOR_PROP = "ClassDiagram.DefaultFillColor";
	public static final String DEFAULT_LINE_COLOR_PROP = "ClassDiagram.DefaultLineColor";
	public static final String SHOW_ICONS_PROP = "ClassDiagram.showIcons";

	private static final int TRUE_INDEX = 0;
	private static final int FALSE_INDEX = 1;

	static {
		descriptors = new IPropertyDescriptor[] { 
//			new ColorPropertyDescriptor( DEFAULT_FILL_COLOR_PROP, "Default Fill Color"), 
//			new ColorPropertyDescriptor( DEFAULT_LINE_COLOR_PROP, "Default Line Color")
			new ComboBoxPropertyDescriptor(SHOW_ICONS_PROP, "Show Icons", new String[] { "Yes", "No" } )
		};
	}
	
	private RGB defaultFillColor;
	private RGB defaultLineColor;
	private boolean showIcons = true;
	
	public void addClassDiagramElement(ClassDiagramElement object) {
		if ( object instanceof ClassDiagramClass ) {
			addClassDiagramClass((ClassDiagramClass) object);
		} else if ( object instanceof ClassDiagramNote ) {
			addClassDiagramNote((ClassDiagramNote) object);
		}
	}

	public boolean addClassDiagramClass( ClassDiagramClass classDiagramClass )
	{
		if ( classDiagramClass != null && classDiagramClasses.add(classDiagramClass) ) {
			classDiagramClass.setParent(this);
			firePropertyChange(CHILD_ADDED_PROP, null, classDiagramClass);
			connectClass(classDiagramClass);
			return true;
		}
		return false;
	}

	private void connectClass(ClassDiagramClass classDiagramClass) {
		
		connectClassToParent(classDiagramClass);
		connectClassToChildren(classDiagramClass);
		
		connectClassToAttributeClass(classDiagramClass);
		connectAttributeClassToClass(classDiagramClass);
	}

	private void connectClassToParent(ClassDiagramClass classDiagramClass) {
		for ( ClassDiagramClass parentClass : classDiagramClasses ) {
			if ( isChildClass( parentClass,classDiagramClass ) ) {
				parentClass.addChild( classDiagramClass );
				return;
			}
		}
	}

	private void connectClassToAttributeClass(ClassDiagramClass classDiagramClass) {
		for ( AttributeRelationship attributeRelationship : classDiagramClass.getSourceRelations() ) {
			ClassDiagramClass attributeClass = getAttributeRelationshipClass(attributeRelationship);
			if ( attributeClass != null ) {
				classDiagramClass.connectSource( attributeRelationship );
				attributeClass.addTarget( attributeRelationship );
			}
		}
	}

	private void connectAttributeClassToClass(ClassDiagramClass attributeClass) {
		for ( ClassDiagramClass classDiagramClass : classDiagramClasses ) {
			for ( AttributeRelationship attributeRelationship : classDiagramClass.getSourceRelations() ) {
				if ( isAttributeRelationshipClass(attributeRelationship, attributeClass ) ) {
					classDiagramClass.connectSource( attributeRelationship );
					attributeClass.addTarget( attributeRelationship );
				}
			}
		}
	}

	private ClassDiagramClass getAttributeRelationshipClass(
			AttributeRelationship attributeRelationship) {
		for (ClassDiagramClass attributeClass : classDiagramClasses) {
			if (isAttributeRelationshipClass(attributeRelationship, attributeClass)) {
				return attributeClass;
			}
		}
		return null;
	}

	private boolean isAttributeRelationshipClass(AttributeRelationship attributeRelationship,
			ClassDiagramClass attributeClass) {
		return attributeClass.getId().equals( attributeRelationship.getRequiredClassId() );
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
	
	public boolean addClassDiagramNote(ClassDiagramNote classDiagramNote) {
		if ( classDiagramNote != null && classDiagramNotes.add(classDiagramNote) ) {
			classDiagramNote.setParent(this);
			firePropertyChange(CHILD_ADDED_PROP, null, classDiagramNote);
			return true;
		}
		return false;
	}

	public void deleteClassDiagramElement(ClassDiagramElement object) {
		if ( object instanceof ClassDiagramClass ) {
			deleteClassDiagramClass(object);
		} else if ( object instanceof ClassDiagramNote ) {
			deleteClassDiagramNote(object);
		}
		
	}

	private void deleteClassDiagramClass(ClassDiagramElement object) {
		ClassDiagramClass classDiagramClass = (ClassDiagramClass) object;
		
		disconnectClassFromChildren(classDiagramClass);
		disconnectClassFromParent(classDiagramClass);
		
		if ( classDiagramClasses.remove( classDiagramClass ) ) {
			firePropertyChange(CHILD_REMOVED_PROP, null, classDiagramClass );
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

	private void disconnectClassFromParent(ClassDiagramClass classDiagramClass) {
		if ( classDiagramClass.getParentRelation() != null ) {
			InheritRelationship parentRelation = classDiagramClass.getParentRelation();
			classDiagramClass.disconnectParent();
			parentRelation.getParent().disconnectChild(parentRelation);
		}
	}

	private void deleteClassDiagramNote(ClassDiagramElement object) {
		ClassDiagramNote classDiagramNote = (ClassDiagramNote) object;
		if ( classDiagramNotes.remove( classDiagramNote ) ) {
			firePropertyChange(CHILD_REMOVED_PROP, null, classDiagramNote );
		}
	}

	public List<ClassDiagramClass> getClassDiagramClasses() {
		return classDiagramClasses;
	}

	public Collection<ClassDiagramNote> getClassDiagramNotes() {
		return classDiagramNotes;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}
	
	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		
		if (DEFAULT_FILL_COLOR_PROP.equals(propertyId)) {
			defaultFillColor = (RGB) value;
		} else if (DEFAULT_LINE_COLOR_PROP.equals(propertyId)) {
			defaultLineColor = (RGB) value;
		} if ( SHOW_ICONS_PROP.equals(propertyId) ) {
			setShowIcons( value == null || ((Integer)value).intValue() == TRUE_INDEX );
		}
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
		if ( DEFAULT_FILL_COLOR_PROP.equals(propertyId)) {
			return defaultFillColor;
		} else if ( DEFAULT_LINE_COLOR_PROP.equals(propertyId)) {
			return defaultLineColor;
		}  else if ( SHOW_ICONS_PROP.equals(propertyId) ) {
			return new Integer( showIcons ? TRUE_INDEX : FALSE_INDEX );
		}
		
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

	public boolean isShowIcons() {
		return showIcons;
	}

	public void setShowIcons(boolean showIcons) {
		this.showIcons = showIcons;
		firePropertyChange(SHOW_ICONS_PROP, null, new Boolean(showIcons) );
		notifyClassDiagramSettingsChanged(SHOW_ICONS_PROP);
	}

	private void notifyClassDiagramSettingsChanged(String propertyId) {
		for ( ClassDiagramElement classDiagramElement : getClassDiagramClasses() ) {
			classDiagramElement.notifyClassDiagramSettingsChanged(propertyId);
		}
		
		for ( ClassDiagramElement classDiagramNote : getClassDiagramNotes() ) {
			classDiagramNote.notifyClassDiagramSettingsChanged(propertyId);
		}
	}
}
