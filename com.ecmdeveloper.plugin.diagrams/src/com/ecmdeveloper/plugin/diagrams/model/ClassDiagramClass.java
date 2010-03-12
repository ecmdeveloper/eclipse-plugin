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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.ClassesManager;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.classes.model.constants.PropertyType;
import com.ecmdeveloper.plugin.classes.model.task.GetRequiredClassDescription;
import com.ecmdeveloper.plugin.diagrams.util.PluginLog;
import com.filenet.api.meta.PropertyDescriptionObject;


/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramClass extends ClassDiagramElement {

	private String name;
	private String displayName;
	private boolean abstractClass;
	private String id;
	private String parentClassId;
	private List<InheritRelationship> childRelations = new ArrayList<InheritRelationship>();
	private InheritRelationship parentRelation;
	private List<AttributeRelationship> sourceRelations = new ArrayList<AttributeRelationship>();
	private List<AttributeRelationship> targetRelations = new ArrayList<AttributeRelationship>();
	private ArrayList<ClassDiagramAttribute> attributes = new ArrayList<ClassDiagramAttribute>();

	private static IPropertyDescriptor[] classDescriptors = { XPOS_PROPERTY_DESCRIPTOR,
			YPOS_PROPERTY_DESCRIPTOR };
	
	public ClassDiagramClass(String name, String displayName,
			boolean abstractClass, String id, String parentClassId) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.abstractClass = abstractClass;
		this.parentClassId = parentClassId;
		this.id = id;
	}

	public ClassDiagramClass(IAdaptable adaptableObject) {
		
		com.filenet.api.meta.ClassDescription internalClassDescription = (com.filenet.api.meta.ClassDescription) adaptableObject
				.getAdapter(com.filenet.api.meta.ClassDescription.class);
		
		abstractClass = ! internalClassDescription.get_AllowsInstances();
		id = internalClassDescription.get_Id().toString();
		displayName = internalClassDescription.get_DisplayName();
		name = internalClassDescription.get_Name();
		
		ClassDescription classDescription = (ClassDescription) adaptableObject.getAdapter(ClassDescription.class);
		createClassDiagramAttributes(classDescription);
		createClassDiagramAttributeRelations(classDescription);
		
		if ( classDescription.getParent() instanceof ClassDescription ) {
			parentClassId = ((ClassDescription)classDescription.getParent()).getId();
		}
	}

	private void createClassDiagramAttributeRelations(ClassDescription classDescription) {
		for ( PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			if (!propertyDescription.isReadOnly()
					&& propertyDescription.getPropertyType().equals(PropertyType.OBJECT)) {
				createClassDiagramAttributeRelation(propertyDescription);
			}
		}		
	}

	private void createClassDiagramAttributeRelation(PropertyDescription propertyDescription) {
		com.filenet.api.meta.PropertyDescription internalPropertyDescription = (com.filenet.api.meta.PropertyDescription) propertyDescription
		.getAdapter(com.filenet.api.meta.PropertyDescription.class );
		PropertyDescriptionObject objectPropertyDescription = (PropertyDescriptionObject) internalPropertyDescription;
		try {
			AttributeRelationship attributeRelationship = new AttributeRelationship(objectPropertyDescription,this);
			sourceRelations.add(attributeRelationship);
		} catch (Exception e) {
			PluginLog.error(e);
		}
	}

//	private AttributeRelationship createAttributeRelationship(PropertyDescriptionObject objectPropertyDescription)
//			throws Exception {
//		// TODO: make this asynchronous?
//		GetRequiredClassDescription task = new GetRequiredClassDescription( objectPropertyDescription );
//		com.filenet.api.meta.ClassDescription requiredClass = (com.filenet.api.meta.ClassDescription) ClassesManager.getManager().executeTaskSync( task );
//		
//		AttributeRelationship attributeRelationship = new AttributeRelationship();
//		attributeRelationship.setRequiredClassId( requiredClass.get_Id().toString() );
//		attributeRelationship.setRequiredClassName( requiredClass.get_Name() );
//		attributeRelationship.setName( objectPropertyDescription.get_DisplayName() );
//		String multiplicity = MultiplicityFormatter.getMultiplicity( objectPropertyDescription );
//		attributeRelationship.setTargetMultiplicity( multiplicity );
//		
//		return attributeRelationship;
//	}

	private void createClassDiagramAttributes(ClassDescription classDescription) {
		for ( PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			if ( ! propertyDescription.isReadOnly() ) {
				ClassDiagramAttribute attribute = (ClassDiagramAttribute) propertyDescription.getAdapter(ClassDiagramAttribute.class);
				addAttribute(attribute);
			}
		 }
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getId() {
		return id;
	}

	
	public String getParentClassId() {
		return parentClassId;
	}

	public boolean isAbstractClass() {
		return abstractClass;
	}
	
	public void addAttribute(ClassDiagramAttribute attribute ) {
		attributes.add(attribute);
	}
	
	public List<ClassDiagramAttribute> getAttributes() {
		return attributes;
	}
	
	public void connectParent(InheritRelationship inheritRelationship ) {
		parentRelation = inheritRelationship;
		firePropertyChange(TARGET_CONNECTIONS_PROP, null, parentRelation );
	}
	
	public void disconnectParent() {
		InheritRelationship oldParentRelation = parentRelation;
		parentRelation = null;
		firePropertyChange(TARGET_CONNECTIONS_PROP, oldParentRelation, null );
	}

	public void addChild(ClassDiagramClass childClass ) {
		
		InheritRelationship childRelation = getChildRelation( childClass);
		
		if ( childRelation == null ) {
			childRelation = new InheritRelationship(this, childClass );
			childRelations.add( childRelation );
			childClass.connectParent( childRelation );
			firePropertyChange(SOURCE_CONNECTIONS_PROP, null, childRelation );
		} else {
			childClass.connectParent( childRelation );
		}
	}

	private InheritRelationship getChildRelation(ClassDiagramClass childClass) {
		for ( InheritRelationship childRelation : childRelations ) {
			if ( childRelation.getChild().equals( childClass ) ) {
				return childRelation;
			}
		}
		return null;
	}

	public void disconnectChild(InheritRelationship inheritRelationship ) {
		if ( childRelations.remove( inheritRelationship ) ) {
			firePropertyChange(SOURCE_CONNECTIONS_PROP, inheritRelationship, null);
		}
	}

	public List<InheritRelationship> getChildRelations() {
		return childRelations;
	}
	
	public InheritRelationship getParentRelation() {
		return parentRelation;
	}

	@Override
	public boolean equals(Object object) {
		if ( object instanceof ClassDiagramClass )
		{
			return ((ClassDiagramClass)object).getId().equalsIgnoreCase( getId() );
		}
		return false;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return classDescriptors;
	}

	public void addTarget(AttributeRelationship relationship) {
		if ( ! targetRelations.contains(relationship) ) {
			targetRelations.add( relationship );
			firePropertyChange(TARGET_CONNECTIONS_PROP, null, relationship);
		}
	}
	
	public List<AttributeRelationship> getSourceRelations() {
		return sourceRelations;
	}

	public List<AttributeRelationship> getTargetRelations() {
		return targetRelations;
	}

	public void connectSource(AttributeRelationship attributeRelationship) {
		attributeRelationship.setConnected(true);
		firePropertyChange(SOURCE_CONNECTIONS_PROP, null, attributeRelationship);
	}
}
