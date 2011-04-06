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

package com.ecmdeveloper.plugin.classes.model.task;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescriptionObject;

/**
 * @author ricardo.belfor
 *
 */
public class CheckContainableClass extends BaseTask {

	private PropertyDescriptionObject propertyDescription;
	private final ObjectStore objectStore;
	private boolean containable;

	public CheckContainableClass(PropertyDescriptionObject propertyDescription, ObjectStore objectStore) {
		this.propertyDescription = propertyDescription;
		this.objectStore = objectStore;
	}

	public boolean isContainable() {
		return containable;
	}

	@Override
	protected Object execute() throws Exception {

		ClassDescription requiredClass = propertyDescription.get_RequiredClass();

		containable = requiredClass.describedIsOfClass("Document")
				|| requiredClass.describedIsOfClass("Folder")
				|| requiredClass.describedIsOfClass("CustomObject");
		
		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}
}
