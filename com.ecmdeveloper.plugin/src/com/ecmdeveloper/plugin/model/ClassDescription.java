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
package com.ecmdeveloper.plugin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

import com.ecmdeveloper.plugin.core.model.ClassesManager;
import com.ecmdeveloper.plugin.core.model.ClassesPlaceholder;
import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IPropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;
import com.ecmdeveloper.plugin.model.tasks.GetChildClassDescriptionsTask;
import com.filenet.api.collection.PropertyDescriptionList;

/**
 * 
 * @author Ricardo Belfor
 * 
 */
public class ClassDescription implements IAdaptable, IClassDescription {

	private com.filenet.api.meta.ClassDescription classDescription;
	private IObjectStore objectStore;
	private String name;
	private String displayName;
	private Object parent;
	private ArrayList<Object> children;
	private Boolean hasChildren;
	private String id;
	List<IPropertyDescription> propertyDescriptions;
	private final Boolean cbrEnabled;
	private String namePropertyName;

	public ClassDescription(Object classDescription, Object parent, IObjectStore objectStore) {

		if (classDescription != null) {
			this.classDescription = (com.filenet.api.meta.ClassDescription) classDescription;
			this.objectStore = objectStore;
			this.parent = parent;
			this.id = this.classDescription.get_Id().toString();
			this.cbrEnabled = this.classDescription.get_IsCBREnabled();
			Integer namePropertyIndex = this.classDescription.get_NamePropertyIndex();
			if (namePropertyIndex != null) {
				PropertyDescription namePropertyDescription = (PropertyDescription) this.classDescription
						.get_PropertyDescriptions().get(namePropertyIndex);
				namePropertyName = namePropertyDescription.getName();

			}
			refreshInternal();
		} else {
			this.cbrEnabled = false;
		}
	}

	@Override
	public void refresh() {
		if (classDescription != null) {
			classDescription.refresh();
			propertyDescriptions = null;
			refreshInternal();
		}
	}

	private void refreshInternal() {
		name = classDescription.get_SymbolicName();
		displayName = classDescription.get_DisplayName();
		hasChildren = !classDescription.get_ImmediateSubclassDescriptions().isEmpty();
		children = null;
		initializePropertyDescriptions();
	}

	public Object getObjectStoreObject() {
		return classDescription;
	}

	@Override
	public IObjectStore getObjectStore() {
		return objectStore;
	}

	@Override
	public Collection<Object> getChildren() {

		if (children == null) {
			children = new ArrayList<Object>();
			ClassesPlaceholder placeholder = new ClassesPlaceholder();
			children.add(placeholder);

			ITaskFactory taskFactory = objectStore.getTaskFactory();
			GetChildClassDescriptionsTask task = new GetChildClassDescriptionsTask(this,
					placeholder);
			ClassesManager.getManager().executeTaskASync(task);
		}

		return children;
	}

	@Override
	public Collection<Object> getChildren(boolean synchronous) throws Exception {

		if (synchronous) {
			if (children == null) {
				children = new ArrayList<Object>();
				GetChildClassDescriptionsTask task = new GetChildClassDescriptionsTask(this, null);
				ClassesManager.getManager().executeTaskSync(task);
			}

			return children;
		} else {
			return getChildren();
		}
	}

	@Override
	public String getDisplayName() {
		return displayName;
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
	@Override
	public boolean hasChildren() {
		if (hasChildren == null) {
			return false;
		} else {
			return hasChildren;
		}
	}

	@Override
	public void setChildren(Collection<IClassDescription> children) {
		this.children = new ArrayList<Object>();
		this.children.addAll(children);
		hasChildren = !children.isEmpty();
	}

	@Override
	public Collection<IPropertyDescription> getAllPropertyDescriptions() {
		return propertyDescriptions;
	}

	@Override
	public Collection<IPropertyDescription> getPropertyDescriptions() {

		Collection<IPropertyDescription> filteredPropertyDescriptions = new ArrayList<IPropertyDescription>();
		for (IPropertyDescription propertyDescription : propertyDescriptions) {
			if (!propertyDescription.isHidden()) {
				filteredPropertyDescriptions.add(propertyDescription);
			}
		}
		return filteredPropertyDescriptions;
	}

	private void initializePropertyDescriptions() {

		if (propertyDescriptions == null) {

			PropertyDescriptionList propertyDescriptionsList = classDescription
					.get_PropertyDescriptions();
			propertyDescriptions = new ArrayList<IPropertyDescription>();
			Iterator<?> iterator = propertyDescriptionsList.iterator();

			while (iterator.hasNext()) {
				com.filenet.api.meta.PropertyDescription internalPropertyDescription = (com.filenet.api.meta.PropertyDescription) iterator
						.next();
				propertyDescriptions.add(new PropertyDescription(internalPropertyDescription,
						(ObjectStore) objectStore));
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {

		if (adapter.isInstance(classDescription)) {
			return classDescription;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	@Override
	public String getNamePropertyName() {
		return namePropertyName;
	}
}
