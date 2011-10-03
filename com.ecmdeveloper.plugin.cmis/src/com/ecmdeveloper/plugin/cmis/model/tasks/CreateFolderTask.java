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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.PropertyIds;

import com.ecmdeveloper.plugin.cmis.model.Folder;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.ICreateFolderTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;

/**
 * @author ricardo.belfor
 *
 */
public class CreateFolderTask extends AbstractTask implements ICreateFolderTask {

	private Folder newFolder;
	private final ObjectStoreItem parent;
	private final String className;
	private final Map<String, Object> propertiesMap;

	public CreateFolderTask(ObjectStoreItem parent, String className, Map<String,Object> propertiesMap) {
		this.parent = parent;
		this.className = className;
		this.propertiesMap = propertiesMap;
	}

	@Override
	public IFolder getNewFolder() {
		return newFolder;
	}

	@Override
	public IObjectStoreItem getNewObjectStoreItem() {
		return getNewFolder();
	}

	@Override
	public Object call() throws Exception {

		org.apache.chemistry.opencmis.client.api.Folder internalFolder = ((Folder)parent).getInternalFolder();
		
		Map<String, Object> folderPropertiesMap = new HashMap<String, Object>();
		folderPropertiesMap.putAll( propertiesMap );
		folderPropertiesMap.put(PropertyIds.OBJECT_TYPE_ID, className );
		folderPropertiesMap.remove("FolderName");
		org.apache.chemistry.opencmis.client.api.Folder folder = internalFolder.createFolder(folderPropertiesMap);
		
		newFolder = ObjectStoreItemFactory.createFolder(folder, getParent(), (ObjectStore) getParent().getObjectStore(), false );
		newFolder.save();
		newFolder.refresh();
	
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}

//	private com.filenet.api.core.Folder getInternalParent() {
//		ObjectStoreItem parent = getParent();
//		if ( parent instanceof Folder ) {
//			return (com.filenet.api.core.Folder) getParent().getObjectStoreObject();
//		} else if ( parent instanceof ObjectStore ) {
//			com.filenet.api.core.ObjectStore internalObjectStore = (com.filenet.api.core.ObjectStore) parent.getObjectStoreObject();
//			internalObjectStore.fetchProperties( new String[] { PropertyNames.ROOT_FOLDER } );
//			return internalObjectStore.get_RootFolder();
//		} else {
//			throw new UnsupportedOperationException("Invalid parent type" );
//		}
//	}

	@Override
	public IObjectStoreItem getParent() {
		return parent;
	}

	@Override
	public boolean isAddedToParent() {
		return false;
	}
}
