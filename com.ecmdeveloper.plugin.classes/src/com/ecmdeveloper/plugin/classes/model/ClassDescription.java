/**
 * Copyright 2009,2010, Ricardo Belfor
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
package com.ecmdeveloper.plugin.classes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

import com.ecmdeveloper.plugin.classes.model.task.GetChildClassDescriptionsTask;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.collection.PropertyDescriptionList;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassDescription implements IAdaptable {

	private com.filenet.api.meta.ClassDescription classDescription;
	private ObjectStore objectStore;
	private String name;
	private String displayName; 
	private Object parent;
	private ArrayList<Object> children;
	private Boolean hasChildren;
	private String id;
	List<PropertyDescription> propertyDescriptions;
	private final Boolean cbrEnabled;
	
	public ClassDescription(Object classDescription, Object parent, ObjectStore objectStore) {
		
		if ( classDescription != null ) {
			this.classDescription = (com.filenet.api.meta.ClassDescription) classDescription;
			this.objectStore = objectStore;
			this.parent = parent;
			this.id = this.classDescription.get_Id().toString();
			this.cbrEnabled = this.classDescription.get_IsCBREnabled();
			refreshInternal();
		} else {
			this.cbrEnabled = false;
		}
	}

	public void refresh() {
		if ( classDescription != null) {
			classDescription.refresh();
			propertyDescriptions = null;
			refreshInternal();
		}
	}

	private void refreshInternal() {
		name = classDescription.get_SymbolicName();
		displayName = classDescription.get_DisplayName(); 
		hasChildren = ! classDescription.get_ImmediateSubclassDescriptions().isEmpty();
		children = null;
		getPropertyDescriptions();
	}

	public Object getObjectStoreObject() {
		return classDescription;
	}

	public ObjectStore getObjectStore() {
		return objectStore;
	}

	public Collection<Object> getChildren() {

		if ( children == null ) {
			children = new ArrayList<Object>();
			Placeholder placeholder = new Placeholder();
			children.add( placeholder );

			GetChildClassDescriptionsTask task = new GetChildClassDescriptionsTask( this, placeholder );
			ClassesManager.getManager().executeTaskASync(task);
		}
		
		return children;
	}

	public Collection<Object> getChildren( boolean synchronous) throws Exception {

		if ( synchronous ) {
			if ( children == null ) {
				children = new ArrayList<Object>();
				GetChildClassDescriptionsTask task = new GetChildClassDescriptionsTask( this, null );
				ClassesManager.getManager().executeTaskSync(task);
			}
			
			return children;
		} else {
			return getChildren();
		}
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public Boolean getCBREnabled() {
		return cbrEnabled;
	}

	@Override
	public String toString() {
		return getName();
	}

	public Object getParent() {
		return parent;
	}

	/**
	 * Checks for children.
	 * 
	 * @return true, if checks for children
	 * 
	 */
	public boolean hasChildren() 
	{
		if ( hasChildren == null ) {
			return false;
		} else {
			return hasChildren;
		}
	}
	
	public void setChildren(Collection<ClassDescription> children) {
		this.children = new ArrayList<Object>();
		this.children.addAll(children);
		hasChildren = ! children.isEmpty();
	}
	
	public Collection<PropertyDescription> getPropertyDescriptions() {

		if ( propertyDescriptions == null ) {
			
			PropertyDescriptionList propertyDescriptionsList = classDescription.get_PropertyDescriptions();
			propertyDescriptions = new ArrayList<PropertyDescription>();
			Iterator<?> iterator = propertyDescriptionsList.iterator();
			
			while (iterator.hasNext()) {
				com.filenet.api.meta.PropertyDescription internalPropertyDescription = (com.filenet.api.meta.PropertyDescription) iterator.next();
				
				if (  ! internalPropertyDescription.get_IsHidden()) {
//					System.out.println( "Adding " + internalPropertyDescription.get_Name() );
					propertyDescriptions.add( new PropertyDescription(internalPropertyDescription, objectStore) );
				} else {
//					if ( internalPropertyDescription.get_IsHidden() ) {
//						System.out.println( "Skipping hidden " + internalPropertyDescription.get_Name() );
//					} else {
//						System.out.println( "Skipping " + internalPropertyDescription.get_Name() );
//					}
				}
			}
		}
		
		return propertyDescriptions;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {

		if ( adapter.isInstance(classDescription) ) {
			return classDescription;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
