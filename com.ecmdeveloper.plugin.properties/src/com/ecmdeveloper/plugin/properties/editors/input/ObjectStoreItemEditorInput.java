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

package com.ecmdeveloper.plugin.properties.editors.input;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.model.PropertiesObject;
import com.ecmdeveloper.plugin.properties.model.SavedPropertiesObject;
import com.ecmdeveloper.plugin.properties.model.UnsavedPropertiesObject;
import com.ecmdeveloper.plugin.properties.util.IconFiles;

/**
 * @author Ricardo Belfor
 *
 */
public class ObjectStoreItemEditorInput implements IEditorInput {

	protected ObjectStoreItem objectStoreItem;
	private ClassDescription classDescription;
	protected PropertiesObject propertiesObject;
	
	public ObjectStoreItemEditorInput(ObjectStoreItem objectStoreItem, ClassDescription classDescription) {
		this.objectStoreItem = objectStoreItem;
		this.classDescription = classDescription;

		if ( objectStoreItem == null ) {
			propertiesObject = new UnsavedPropertiesObject();
		} else {
			propertiesObject = new SavedPropertiesObject(objectStoreItem);
		}
	}

	public PropertiesObject getPropertiesObject() {
		return propertiesObject;
	}

	public ClassDescription getClassDescription() {
		return classDescription;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptor( IconFiles.FOLDER_EDIT );
	}

	@Override
	public String getName() {
		return propertiesObject.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		if ( adapter.equals( ObjectStoreItem.class) ) {
			return objectStoreItem;
		} else if ( adapter.equals( ClassDescription.class ) ) {
			return classDescription;
		} else if ( adapter.equals( PropertiesObject.class ) ) {
			return propertiesObject;
		}
		return null;
	}
}
