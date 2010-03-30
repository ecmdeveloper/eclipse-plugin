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

package com.ecmdeveloper.plugin.diagrams.properties;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.ecmdeveloper.plugin.diagrams.model.AttributeRelationship;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.InheritRelationship;

/**
 * @author Ricardo.Belfor
 *
 */
public class VisibleRelationsProperties implements IPropertySource {

	private static final String RELATIONSHIP_FORMAT = "From Property \"{0}\" to Class \"{1}\"";
	private static final String CHILD_CLASS_PROPERTY_ID_PREFIX = "Child";
	private static final String SOURCE_PROPERTY_ID_PREFIX = "Source";
	private static final String TARGET_PROPERTY_ID_PREFIX = "Target";
	private static final String PARENT_CLASS_DISPLAY_NAME = "To Parent Class";
	private static final String PARENT_CLASS_PROPERTY_ID = "Parent Class";
	private ClassDiagramClass classDiagramClass;
	
	public VisibleRelationsProperties(ClassDiagramClass classDiagramClass) {
		this.classDiagramClass = classDiagramClass;
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {

		ArrayList<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		
		if ( classDiagramClass.getParentRelation() != null ) {
			IPropertyDescriptor propertyDescriptor = new CheckBoxPropertyDescriptor(PARENT_CLASS_PROPERTY_ID, PARENT_CLASS_DISPLAY_NAME );  
			descriptors.add( propertyDescriptor );
		}
		
		descriptors.addAll( getInheritRelationshipDescriptors() );
		descriptors.addAll( getSourceRelationshipDescriptors() );
		descriptors.addAll( getTargetRelationshipDescriptors() );
		
		return descriptors.toArray( new IPropertyDescriptor[ descriptors.size()] );
	}

	private ArrayList<IPropertyDescriptor> getSourceRelationshipDescriptors() {
		
		ArrayList<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		
		for ( AttributeRelationship sourceRelation : classDiagramClass.getSourceRelations() ) {
			if ( sourceRelation.isConnected() ) {
				IPropertyDescriptor propertyDescriptor = getSourceRelationshipDescriptor(sourceRelation);  
				descriptors.add( propertyDescriptor );
			}
		}
		return descriptors;
	}

	private IPropertyDescriptor getSourceRelationshipDescriptor(AttributeRelationship sourceRelation) {
		String propertyId = SOURCE_PROPERTY_ID_PREFIX + sourceRelation.getSourceConnector().getPropertyId();
		String displayName = MessageFormat.format(RELATIONSHIP_FORMAT,
				sourceRelation.getSourceConnector().getPropertyName(), sourceRelation
						.getTargetConnector().getClassName());
		
		IPropertyDescriptor propertyDescriptor = new CheckBoxPropertyDescriptor(propertyId, displayName );
		return propertyDescriptor;
	}

	private ArrayList<IPropertyDescriptor> getTargetRelationshipDescriptors() {
		
		ArrayList<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
		
		for ( AttributeRelationship targetRelation : classDiagramClass.getTargetRelations() ) {
			String targetPropertyId = targetRelation.getTargetConnector().getPropertyId();
			if ( targetPropertyId != null && ! targetRelation.isLoop() && targetRelation.isConnected() ) {
				IPropertyDescriptor propertyDescriptor = getTargetRelationshipDescriptor(targetRelation);  
				descriptors.add( propertyDescriptor );
			}
		}
		return descriptors;
	}

	private IPropertyDescriptor getTargetRelationshipDescriptor(AttributeRelationship targetRelation) {
		String propertyId = TARGET_PROPERTY_ID_PREFIX + targetRelation.getTargetConnector().getPropertyId();
		String displayName = MessageFormat.format(RELATIONSHIP_FORMAT,
				 targetRelation.getTargetConnector().getPropertyName(),
				 targetRelation.getSourceConnector().getClassName());
		
		IPropertyDescriptor propertyDescriptor = new CheckBoxPropertyDescriptor(propertyId, displayName );
		return propertyDescriptor;
	}
	
	private ArrayList<IPropertyDescriptor> getInheritRelationshipDescriptors() {

		ArrayList<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();

		for ( InheritRelationship inheritRelationship : classDiagramClass.getChildRelations() ) {
			String propertyId = CHILD_CLASS_PROPERTY_ID_PREFIX + inheritRelationship.getChild().getId();
			String displayName = MessageFormat.format( "To Child Class \"{0}\"", inheritRelationship.getChild().getDisplayName() );
			IPropertyDescriptor propertyDescriptor = new CheckBoxPropertyDescriptor(propertyId, displayName );  
			descriptors.add( propertyDescriptor );
		}
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id) {
		
		String propertyId = (String) id;
		if ( PARENT_CLASS_PROPERTY_ID.equals( propertyId ) ) {
			return new Boolean( classDiagramClass.getParentRelation().isVisible() );
		} else if ( propertyId.startsWith( CHILD_CLASS_PROPERTY_ID_PREFIX ) ) {
			InheritRelationship childRelationship  = getChildFromPropertyId(propertyId);
			if ( childRelationship != null ) {
				return new Boolean( childRelationship.isVisible() );
			}
		} else if ( propertyId.startsWith( SOURCE_PROPERTY_ID_PREFIX ) ) {
			AttributeRelationship relationship = getSourceRelationshipFromPropertyId(propertyId );
			if ( relationship != null ) {
				return new Boolean( relationship.isVisible() );
			}
		} else if ( propertyId.startsWith( TARGET_PROPERTY_ID_PREFIX ) ) {
			AttributeRelationship relationship = getTargetRelationshipFromPropertyId(propertyId );
			if ( relationship != null ) {
				return new Boolean( relationship.isVisible() );
			}
		}
		return null;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		String propertyId = (String) id;
		if ( PARENT_CLASS_PROPERTY_ID.equals( propertyId ) ) {
			classDiagramClass.getParentRelation().setVisible( (Boolean) value );
		} else if ( propertyId.startsWith( CHILD_CLASS_PROPERTY_ID_PREFIX ) ) {
			InheritRelationship childRelationship  = getChildFromPropertyId(propertyId);
			if ( childRelationship != null ) {
				childRelationship.setVisible( (Boolean) value );
			}
		} else if ( propertyId.startsWith( SOURCE_PROPERTY_ID_PREFIX ) ) {
			AttributeRelationship relationship = getSourceRelationshipFromPropertyId(propertyId );
			if ( relationship != null ) {
				relationship.setVisible( (Boolean) value );
			}
		} else if ( propertyId.startsWith( TARGET_PROPERTY_ID_PREFIX ) ) {
			AttributeRelationship relationship = getTargetRelationshipFromPropertyId(propertyId );
			if ( relationship != null ) {
				relationship.setVisible( (Boolean) value );
			}
		}
	}

	private InheritRelationship getChildFromPropertyId(String propertyId) {
		String childId = propertyId.substring( CHILD_CLASS_PROPERTY_ID_PREFIX.length() );
		for ( InheritRelationship inheritRelationship : classDiagramClass.getChildRelations() ) {
			if ( childId.equalsIgnoreCase( inheritRelationship.getChild().getId() ) ) {
				return inheritRelationship;
			}
		}
		return null;
	}

	private AttributeRelationship getSourceRelationshipFromPropertyId(String propertyId ) {
		String sourceId = propertyId.substring( SOURCE_PROPERTY_ID_PREFIX.length() );
		for ( AttributeRelationship sourceRelation : classDiagramClass.getSourceRelations() ) {
			if ( sourceId.equalsIgnoreCase( sourceRelation.getSourceConnector().getPropertyId() ) ) {
				return sourceRelation;
			}
		}
		return null;
	}
	
	private AttributeRelationship getTargetRelationshipFromPropertyId(String propertyId ) {
		String targetId = propertyId.substring( TARGET_PROPERTY_ID_PREFIX.length() );
		for ( AttributeRelationship targetRelation : classDiagramClass.getTargetRelations() ) {
			if ( targetId.equalsIgnoreCase( targetRelation.getTargetConnector().getPropertyId() ) ) {
				return targetRelation;
			}
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public String toString() {
		return "";
	}
}
