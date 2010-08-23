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

import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.filenet.api.core.Factory;

/**
 * @author ricardo.belfor
 *
 */
public class CreateFolderTask extends CreateTask {

	private Folder newFolder;

	public CreateFolderTask(Folder parent, String className, Map<String,Object> propertiesMap) {
		super( parent, className, propertiesMap );
	}

	public Folder getNewFolder() {
		return newFolder;
	}

	@Override
	public ObjectStoreItem getNewObjectStoreItem() {
		return getNewFolder();
	}

	@Override
	public Object call() throws Exception {

		createNewFolder();
		setProperties(newFolder);
		newFolder.save();
		newFolder.refresh();
	
		getParent().addChild( newFolder );
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}

	private void createNewFolder() {
		com.filenet.api.core.Folder internalFolder = createInternalFolder();
		newFolder = new Folder(internalFolder, getParent(), getParent().getObjectStore(), false );
	}

	private com.filenet.api.core.Folder createInternalFolder() {
		com.filenet.api.core.ObjectStore internalObjectStore = getInternalObjectStore();
		com.filenet.api.core.Folder internalFolder = Factory.Folder.createInstance(internalObjectStore, getClassName() );
		com.filenet.api.core.Folder internalParent = (com.filenet.api.core.Folder) getParent().getObjectStoreObject();
		internalFolder.set_Parent(internalParent);
		return internalFolder;
	}
}
