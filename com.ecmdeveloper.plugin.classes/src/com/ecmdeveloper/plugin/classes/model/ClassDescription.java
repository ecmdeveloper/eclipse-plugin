/**
 * Copyright 2009, Ricardo Belfor
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

	protected com.filenet.api.meta.ClassDescription classDescription;
	protected ObjectStore objectStore;
	protected String name;
	protected Object parent;
	protected ArrayList<Object> children;
	protected Boolean hasChildren;
	List<PropertyDescription> propertyDescriptions;
	
	public ClassDescription(Object classDescription, Object parent, ObjectStore objectStore) {
		
		if ( classDescription != null ) {
			this.classDescription = (com.filenet.api.meta.ClassDescription) classDescription;
			this.objectStore = objectStore;
			this.name = this.classDescription.get_Name();
			this.parent = parent;
			this.hasChildren = ! this.classDescription.get_ImmediateSubclassDescriptions().isEmpty();

			getPropertyDescriptions();
		}
	}

	public Object getObjectStoreObject() {
		return classDescription;
	}

	public ObjectStore getObjectStore() {
		return objectStore;
	}

	public Collection<Object> getChildren() {

		if ( children == null )
		{
			children = new ArrayList<Object>();
			children.add( new Placeholder() );

			GetChildClassDescriptionsTask task = new GetChildClassDescriptionsTask( this );
			ClassesManager.getManager().executeTaskASync(task);
		}
		
		return children;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return name;
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
				
				if ( ! internalPropertyDescription.get_IsSystemGenerated() &&
						/*! internalPropertyDescription.get_IsSystemOwned() && */ 
						! internalPropertyDescription.get_IsHidden()) {
					System.out.println( "Adding " + internalPropertyDescription.get_Name() );
					propertyDescriptions.add( new PropertyDescription(internalPropertyDescription) );
				} else {
					System.out.println( "Skipping " + internalPropertyDescription.get_Name() );
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
