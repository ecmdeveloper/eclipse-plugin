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
package com.ecmdeveloper.plugin.model.tasks;

import java.util.ArrayList;
import java.util.Iterator;

import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ReferentialContainmentRelationship;

/**
 * This task is used for loading the children of an item.
 * 
 * @author Ricardo Belfor
 *
 */
public class LoadChildrenTask extends BaseTask {

	final protected ObjectStoreItem objectStoreItem;
	
	/**
	 * The constructor of this task is used to pass all the relevant input
	 * to perform the task.
	 * 
	 * @param objectStoreItem the object store item
	 */
	public LoadChildrenTask( ObjectStoreItem objectStoreItem ) {
		this.objectStoreItem = objectStoreItem;
	}

	/**
	 * Loads the children of the object store item. If the child item is a 
	 * folder then a difference is made between a folder with a child relation
	 * and a folder with a containment relation. As this method run asynchronously
	 * exceptions are logged.
	 * 
	 * @return null
	 * 
	 * @throws Exception not throw, exceptions are logged.
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public String call() throws Exception {

		try {
			ArrayList<IObjectStoreItem> children = new ArrayList<IObjectStoreItem>();
			
			com.filenet.api.core.Folder folder;
	
			if ( objectStoreItem instanceof ObjectStore ) {
				com.filenet.api.core.ObjectStore objectStore = (com.filenet.api.core.ObjectStore) objectStoreItem.getObjectStoreObject();
				objectStore.fetchProperties( new String[] { PropertyNames.ROOT_FOLDER } );
				folder = objectStore.get_RootFolder();
				folder.fetchProperties( new String[] { PropertyNames.CONTAINEES, PropertyNames.SUB_FOLDERS } );
			} else if ( objectStoreItem instanceof Folder ) {
				folder = (com.filenet.api.core.Folder) objectStoreItem.getObjectStoreObject();
			} else {
				return null;
			}
			
			Iterator<?> iterator = folder.get_SubFolders().iterator();
			ObjectStore objectStore = objectStoreItem.getObjectStore();
			while (iterator.hasNext()) {
				children.add( new Folder( iterator.next(), objectStoreItem, objectStore ) );
			}
			
			iterator = folder.get_Containees().iterator();
			
			while (iterator.hasNext() ) {
				
				ReferentialContainmentRelationship relation = (ReferentialContainmentRelationship) iterator.next();
				relation.fetchProperties( new String[]{ PropertyNames.HEAD } );
	
				IndependentObject object = relation.get_Head();
	
				if ( object instanceof com.filenet.api.core.Document )
				{
					children.add( new Document( object, objectStoreItem, objectStore ) );
				}
				else if ( object instanceof com.filenet.api.core.Folder )
				{
					Folder childFolder = new Folder( object, objectStoreItem, objectStore );
					childFolder.setContained(true);
					children.add( childFolder );
				}
				else if ( object instanceof com.filenet.api.core.CustomObject )
				{
					children.add( new CustomObject( object, objectStoreItem, objectStore ) );
				}
			}
			
			objectStoreItem.setChildren(children);
			fireObjectStoreItemsChanged(null, null, new ObjectStoreItem[] { objectStoreItem} );
		
		} catch (Exception e ) {
			PluginLog.error(e);
		}
		return null;
	}

}
