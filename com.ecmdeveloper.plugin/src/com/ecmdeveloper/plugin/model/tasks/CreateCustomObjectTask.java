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

package com.ecmdeveloper.plugin.model.tasks;

import java.util.Map;

import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItemFactory;
import com.filenet.api.core.Factory;

/**
 * @author ricardo.belfor
 *
 */
public class CreateCustomObjectTask extends CreateTask {

	private CustomObject newCustomObject;

	public CreateCustomObjectTask(ObjectStoreItem parent, String className, Map<String, Object> propertiesMap) {
		super(parent, className, propertiesMap);
	}

	@Override
	public ObjectStoreItem getNewObjectStoreItem() {
		return newCustomObject;
	}

	@Override
	protected Object execute() throws Exception {
		
		createCustomObject();
		setProperties(newCustomObject);
		newCustomObject.save();
		newCustomObject.refresh();
		if ( getParent() instanceof Folder ) {
			fileInParent();
		}
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );

		return null;
	}

	private void createCustomObject() {
		com.filenet.api.core.CustomObject internalCustomObject = createInternalCustomObject();
		ObjectStoreItem parent = getParent();
		newCustomObject = ObjectStoreItemFactory.createCustomObject(internalCustomObject, parent, parent.getObjectStore(), false );
	}

	private com.filenet.api.core.CustomObject createInternalCustomObject() {
		com.filenet.api.core.ObjectStore internalObjectStore = getInternalObjectStore();
		com.filenet.api.core.CustomObject internalCustomObject = Factory.CustomObject.createInstance(internalObjectStore, getClassName() );
		return internalCustomObject;
	}
}
