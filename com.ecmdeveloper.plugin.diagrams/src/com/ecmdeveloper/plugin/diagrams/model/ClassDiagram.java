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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

import com.ecmdeveloper.plugin.diagrams.properties.ClassDiagramProperties;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagram extends ClassDiagramBase implements IAdaptable {

	public static final String CHILD_ADDED_PROP = "ClassDiagram.ChildAdded";
	public static final String CHILD_REMOVED_PROP = "ClassDiagram.ChildRemoved";
	
	private ArrayList<ClassDiagramClass> classDiagramClasses = new ArrayList<ClassDiagramClass>();
	private ArrayList<ClassDiagramNote> classDiagramNotes = new ArrayList<ClassDiagramNote>();
	
	public static final String DEFAULT_FILL_COLOR_PROP = "ClassDiagram.DefaultFillColor";
	public static final String DEFAULT_LINE_COLOR_PROP = "ClassDiagram.DefaultLineColor";
	public static final String SHOW_ICONS_PROP = "ClassDiagram.showIcons";
	public static final String SHOW_DISPLAY_NAMES_PROP = "ClassDiagram.showDisplayNames";
	
	@SuppressWarnings("unused")
	private RGB defaultFillColor;
	@SuppressWarnings("unused")
	private RGB defaultLineColor;
	private boolean showIcons = true;
	private boolean showDisplayNames = false;
	
	public void addClassDiagramElement(ClassDiagramElement object) {
		if ( object instanceof ClassDiagramClass ) {
			addClassDiagramClass((ClassDiagramClass) object);
		} else if ( object instanceof ClassDiagramNote ) {
			addClassDiagramNote((ClassDiagramNote) object);
		}
	}

	public boolean addClassDiagramClass( ClassDiagramClass classDiagramClass )
	{
		if ( isAlreadyAdded(classDiagramClass) ) {
			return false;
		}
		
		if ( classDiagramClass != null && classDiagramClasses.add(classDiagramClass) ) {
			classDiagramClass.setParent(this);
			firePropertyChange(CHILD_ADDED_PROP, null, classDiagramClass);
			connectClass(classDiagramClass);
			return true;
		}
		return false;
	}

	private boolean isAlreadyAdded(ClassDiagramClass classDiagramClass) {
		for ( ClassDiagramClass existingClass : classDiagramClasses ) {
			if ( existingClass.getId().equalsIgnoreCase( classDiagramClass.getId() ) ) {
				return true;
			}
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
			
//			if ( isSourceAttributeActive(classDiagramClass, attributeRelationship) ) {
				ClassDiagramClass attributeClass = getAttributeRelationshipClass(attributeRelationship);
				if ( attributeClass != null) {
					classDiagramClass.connectSource( attributeRelationship );
					attributeClass.addTarget( attributeRelationship );
				}
//			}
		}
	}

	private void connectAttributeClassToClass(ClassDiagramClass attributeClass) {
		for (ClassDiagramClass classDiagramClass : classDiagramClasses) {
			for (AttributeRelationship attributeRelationship : classDiagramClass
					.getSourceRelations()) {
				if (isAttributeRelationshipClass(attributeRelationship, attributeClass)) {
					if (!attributeRelationship.isLoop() ) {
						classDiagramClass.connectSource(attributeRelationship);
						attributeClass.addTarget(attributeRelationship);
					}
				}
			}
		}
	}

	private ClassDiagramClass getAttributeRelationshipClass( AttributeRelationship attributeRelationship) {
		for (ClassDiagramClass attributeClass : classDiagramClasses) {
			if (isAttributeRelationshipClass(attributeRelationship, attributeClass)) {
				return attributeClass;
			}
		}
		return null;
	}

	private boolean isAttributeRelationshipClass(AttributeRelationship attributeRelationship,
			ClassDiagramClass attributeClass) {
		return attributeClass.getId().equals( attributeRelationship.getTargetConnector().getClassId() );
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
		disconnectClass(classDiagramClass);
		
		if ( classDiagramClasses.remove( classDiagramClass ) ) {
			firePropertyChange(CHILD_REMOVED_PROP, null, classDiagramClass );
		}
	}

	private void disconnectClass(ClassDiagramClass classDiagramClass) {
		disconnectClassFromChildren(classDiagramClass);
		disconnectClassFromParent(classDiagramClass);
		disconnectClassFromTarget(classDiagramClass);
		disconnectClassFromSource(classDiagramClass);
	}

	private void disconnectClassFromSource(ClassDiagramClass classDiagramClass) {
		for ( AttributeRelationship targetRelation : classDiagramClass.getTargetRelations() ) {
			String sourceClassId = targetRelation.getSourceConnector().getClassId();
			for ( ClassDiagramClass sourceClass : classDiagramClasses ) {
				if ( sourceClass.getId().equals( sourceClassId ) ) {
					sourceClass.disconnectSource(targetRelation);
					break;
				}
			}
		}
	}

	private void disconnectClassFromTarget(ClassDiagramClass classDiagramClass) {
		for ( AttributeRelationship sourceRelation : classDiagramClass.getSourceRelations() ) {
			String targetClassId = sourceRelation.getTargetConnector().getClassId();
			for ( ClassDiagramClass targetClass : classDiagramClasses ) {
				if ( targetClass.getId().equals( targetClassId ) ) {
					targetClass.removeTarget(sourceRelation);
					break;
				}
			}
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

	public boolean isShowIcons() {
		return showIcons;
	}

	public void setShowIcons(boolean showIcons) {
		this.showIcons = showIcons;
		firePropertyChange(SHOW_ICONS_PROP, null, new Boolean(showIcons) );
		notifyClassDiagramSettingsChanged(SHOW_ICONS_PROP);
	}

	public boolean isShowDisplayNames() {
		return showDisplayNames ;
	}

	public void setShowDisplayNames(boolean showDisplayNames) {
		this.showDisplayNames = showDisplayNames;
		notifyClassDiagramSettingsChanged(SHOW_DISPLAY_NAMES_PROP);
	}

	private void notifyClassDiagramSettingsChanged(String propertyId) {
		for ( ClassDiagramElement classDiagramElement : getClassDiagramClasses() ) {
			classDiagramElement.notifyClassDiagramSettingsChanged(propertyId);
		}
		
		for ( ClassDiagramElement classDiagramNote : getClassDiagramNotes() ) {
			classDiagramNote.notifyClassDiagramSettingsChanged(propertyId);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return this;
		if (adapter == IPropertySource.class)
			return new ClassDiagramProperties(this);
		return null;
	}
}
