/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.cmis.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;

import com.ecmdeveloper.plugin.cmis.model.tasks.GetChildClassDescriptionsTask;
import com.ecmdeveloper.plugin.core.model.ClassesManager;
import com.ecmdeveloper.plugin.core.model.ClassesPlaceholder;
import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;

/**
 * @author ricardo.belfor
 *
 */
public class ClassDescription implements IClassDescription {

	private final ObjectType objectType;
	private final IObjectStore objectStore;
	private final String id;
	private final Object parent;
	private String name;
	private String displayName;
	private boolean hasChildren;
	private ArrayList<Object> children;
	List<IPropertyDescription> propertyDescriptions;
	private boolean isAbstract;
	private ClassesPlaceholder placeholder;

	public ClassDescription(ObjectType objectType, Object parent, IObjectStore objectStore ) {
		this.objectType = objectType;
		this.parent = parent;
		this.objectStore = objectStore;
		id = objectType.getId();
		refreshInternal();
	}

	private void refreshInternal() {
		name = objectType.getQueryName();
		displayName = objectType.getDisplayName();
		isAbstract = !objectType.isCreatable();
		hasChildren = true;
		children = null;
		initializePropertyDescriptions();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
	
	@Override
	public boolean hasChildren() {
		return hasChildren;	
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	@Override
	public Collection<Object> getChildren() {

		if ( children == null ) {
			children = new ArrayList<Object>();
			placeholder = new ClassesPlaceholder();
			children.add( placeholder );

			ITaskFactory taskFactory = objectStore.getTaskFactory();
			GetChildClassDescriptionsTask task = new GetChildClassDescriptionsTask( this, placeholder );
			ClassesManager.getManager().executeTaskASync(task);
		}
		
		return children;
	}

	public void addChild(ClassDescription childClassDescription ) {
		if ( children == null ) {
			children = new ArrayList<Object>();
			hasChildren = true;
		}
		
		if ( placeholder != null) {
			children.remove(placeholder);
		}
		children.add(childClassDescription);
	}
	
	@Override
	public IObjectStore getObjectStore() {
		return objectStore;
	}

	@Override
	public Object getParent() {
		return parent;
	}

	public ObjectType getTypeDefinition() {
		return objectType;
	}
	
	@Override
	public Boolean getCBREnabled() {
		return objectType.isFulltextIndexed();
	}

	/* (non-Javadoc)
	 * @see com.ecmdeveloper.plugin.core.model.IClassDescription#getChildren(boolean)
	 */
	@Override
	public Collection<Object> getChildren(boolean synchronous) throws Exception {

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

	@Override
	public void setChildren(Collection<IClassDescription> children) {
		this.children = new ArrayList<Object>();
		this.children.addAll(children);
		hasChildren = ! children.isEmpty();
	}

	private void initializePropertyDescriptions() {
		
		if ( propertyDescriptions == null ) {
			propertyDescriptions = new ArrayList<IPropertyDescription>();
			Map<String, PropertyDefinition<?>> propertyDefinitions = objectType.getPropertyDefinitions();
			if ( propertyDefinitions != null && !propertyDefinitions.isEmpty() ) {
				for ( PropertyDefinition<?> propertyDefinition : propertyDefinitions.values() ) {
					propertyDescriptions.add( new PropertyDescription(propertyDefinition, (ObjectStore) objectStore) );
				}
			}
		}
	}

	@Override
	public Collection<IPropertyDescription> getAllPropertyDescriptions() {
		return propertyDescriptions;
	}

	@Override
	public Collection<IPropertyDescription> getPropertyDescriptions() {
		return propertyDescriptions;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getNamePropertyName() {
		return PropertyIds.NAME;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
}
