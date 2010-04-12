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
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.classes.model.constants.PropertyType;
import com.ecmdeveloper.plugin.diagrams.properties.ClassDiagramClassProperties;
import com.ecmdeveloper.plugin.diagrams.util.PluginLog;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.meta.PropertyDescriptionObject;


/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramClass extends ClassDiagramElement implements IAdaptable {

	public static final String VISIBLE_ATTRIBUTE_PROP = "ClassDiagramClass.VisibleAttribute";

	private String name;
	private String displayName;
	private boolean abstractClass;
	private String id;
	private String parentClassId;
	private String connectionName;
	private String connectionDisplayName;
	private String objectStoreName;
	private String objectStoreDisplayName;
	private boolean parentVisible;
	private List<InheritRelationship> childRelations = new ArrayList<InheritRelationship>();
	private InheritRelationship parentRelation;
	private List<AttributeRelationship> sourceRelations = new ArrayList<AttributeRelationship>();
	private List<AttributeRelationship> targetRelations = new ArrayList<AttributeRelationship>();
	private ArrayList<ClassDiagramAttribute> attributes = new ArrayList<ClassDiagramAttribute>();

	public ClassDiagramClass(String name, String displayName, boolean abstractClass, String id,
			String parentClassId, String connectionName, String connectionDisplayName,
			String objectStoreName, String objectStoreDisplayName) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.abstractClass = abstractClass;
		this.parentClassId = parentClassId;
		this.id = id;
		this.connectionName = connectionName;
		this.connectionDisplayName = connectionDisplayName;
		this.objectStoreName = objectStoreName;
		this.objectStoreDisplayName = objectStoreDisplayName;
		parentVisible = true;
	}

	public ClassDiagramClass(IAdaptable adaptableObject) {
		
		com.filenet.api.meta.ClassDescription internalClassDescription = (com.filenet.api.meta.ClassDescription) adaptableObject
				.getAdapter(com.filenet.api.meta.ClassDescription.class);
		
		abstractClass = ! internalClassDescription.get_AllowsInstances();
		id = internalClassDescription.get_Id().toString();
		displayName = internalClassDescription.get_DisplayName();
		name = internalClassDescription.get_SymbolicName();
		ClassDescription classDescription = (ClassDescription) adaptableObject.getAdapter(ClassDescription.class);
		createClassDiagramAttributes(classDescription);
		createClassDiagramAttributeRelations(classDescription);
		
		if ( classDescription.getParent() instanceof ClassDescription ) {
			parentClassId = ((ClassDescription)classDescription.getParent()).getId();
		}
		parentVisible = true;

		initializeObjectStoreConfiguration(classDescription);
	}

	private void initializeObjectStoreConfiguration(ClassDescription classDescription) {
		
		ObjectStore objectStore = classDescription.getObjectStore();
		objectStoreName = objectStore.getName();
		objectStoreDisplayName = objectStore.getDisplayName();

		ContentEngineConnection connection = objectStore.getConnection();
		connectionName = connection.getName();
		connectionDisplayName = connection.getDisplayName();
	}

	private void createClassDiagramAttributeRelations(ClassDescription classDescription) {
		for ( PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			if ( isAllowedAttribute(propertyDescription) && !propertyDescription.isReadOnly()
					&& propertyDescription.getPropertyType().equals(PropertyType.OBJECT) ) {
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

	private boolean isAllowedAttribute(PropertyDescription propertyDescription) {
		return !Boolean.TRUE.equals(propertyDescription.getSystemOwned());
	}

	private void createClassDiagramAttributes(ClassDescription classDescription) {
		for ( PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
			if ( isAllowedAttribute(propertyDescription) ) {
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

	public void setParentClassId(String parentClassId) {
		this.parentClassId = parentClassId;
	}

	public boolean isAbstractClass() {
		return abstractClass;
	}
	
	public String getConnectionName() {
		return connectionName;
	}

	public String getConnectionDisplayName() {
		return connectionDisplayName;
	}

	public String getObjectStoreName() {
		return objectStoreName;
	}

	public String getObjectStoreDisplayName() {
		return objectStoreDisplayName;
	}

	public void addAttribute(ClassDiagramAttribute attribute ) {
		attributes.add(attribute);
	}
	
	public List<ClassDiagramAttribute> getAttributes() {
		return attributes;
	}
	
	public ClassDiagramAttribute getClassDiagramAttribute(String attributeId ) {
		
		for ( ClassDiagramAttribute attribute : attributes ) {
			if ( attribute.getName().equals(attributeId) ) {
				return attribute; 
			}
		}
		return null;
	}
	
	public void setAttributeVisible(String attributeName, boolean visible) {
		ClassDiagramAttribute classDiagramAttribute = getClassDiagramAttribute(attributeName);
		if ( classDiagramAttribute != null ) {
			classDiagramAttribute.setVisible( visible );
			firePropertyChange(VISIBLE_ATTRIBUTE_PROP, null, attributeName );
		}
	}

	public void setAttributeActive(String attributeName, boolean visible) {
		ClassDiagramAttribute classDiagramAttribute = getClassDiagramAttribute(attributeName);
		if ( classDiagramAttribute != null ) {
			classDiagramAttribute.setVisible( visible );
			firePropertyChange(VISIBLE_ATTRIBUTE_PROP, null, attributeName );
		}
	}
	
	public void connectParent(InheritRelationship inheritRelationship ) {
		parentRelation = inheritRelationship;
		setParentAttributesVisibility(false);
		firePropertyChange(TARGET_CONNECTIONS_PROP, null, parentRelation );
	}

	private void setParentAttributesVisibility(boolean visible ) {
		
		for ( ClassDiagramAttribute attribute : parentRelation.getParent().getAttributes() ) {
			String attributeName = attribute.getName();
			ClassDiagramAttribute classDiagramAttribute = getClassDiagramAttribute(attributeName );
			if ( classDiagramAttribute != null ) {
				setParentAttributeVisibility(classDiagramAttribute, visible);
			}
		}
	}

	private void setParentAttributeVisibility( ClassDiagramAttribute classDiagramAttribute, boolean visibile ) {
		String attributeName = classDiagramAttribute.getName();
		classDiagramAttribute.setActive( visibile );
		firePropertyChange(VISIBLE_ATTRIBUTE_PROP, null, attributeName );
		
		for ( AttributeRelationship sourceRelation : sourceRelations ) {
			if ( attributeName.equals( sourceRelation.getSourceConnector().getPropertyName() ) ) {
				sourceRelation.setVisible(visibile);
			}
		}
	}
	
	public void disconnectParent() {
		setParentAttributesVisibility(true);
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

	public void addTarget(AttributeRelationship relationship) {
		if ( ! targetRelations.contains(relationship) ) {
			targetRelations.add( relationship );
			firePropertyChange(TARGET_CONNECTIONS_PROP, null, relationship);
		}
	}
	
	public void removeTarget(AttributeRelationship relationship) {
		if ( targetRelations.remove( relationship ) ) {
			firePropertyChange(TARGET_CONNECTIONS_PROP, relationship, null);
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

	public void disconnectSource(AttributeRelationship attributeRelationship) {
		attributeRelationship.setConnected(false);
		firePropertyChange(SOURCE_CONNECTIONS_PROP, attributeRelationship, null );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class)
			return this;
		if (adapter == IPropertySource.class)
			return new ClassDiagramClassProperties(this);
		return null;
	}

	public void addSource(AttributeRelationship attributeRelationship) {
		sourceRelations.add( attributeRelationship );
	}

	public void setParentVisible(boolean parentVisible) {
		this.parentVisible = parentVisible;
	}

	public boolean isParentVisible() {
		return parentVisible;
	}
}
