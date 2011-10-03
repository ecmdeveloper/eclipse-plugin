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
package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.ArrayList;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;

import com.ecmdeveloper.plugin.cmis.model.Folder;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;
import com.ecmdeveloper.plugin.cmis.util.PluginLog;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.ILoadChildrenTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;

/**
 * This task is used for loading the children of an item.
 * 
 * @author Ricardo Belfor
 *
 */
public class LoadChildrenTask extends AbstractTask implements ILoadChildrenTask {

	private ObjectStoreItem objectStoreItem;
	private ArrayList<IObjectStoreItem> children;
	
	/**
	 * The constructor of this task is used to pass all the relevant input
	 * to perform the task.
	 * 
	 * @param objectStoreItem the object store item
	 */
	public LoadChildrenTask( ObjectStoreItem objectStoreItem ) {
		this.objectStoreItem = objectStoreItem;
	}

	@Override
	public ObjectStoreItem getObjectStoreItem() {
		return objectStoreItem;
	}

	@Override
	public ArrayList<IObjectStoreItem> getChildren() {
		return children;
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
	public final Object call() throws Exception {

		try {
			children = new ArrayList<IObjectStoreItem>();
			
			org.apache.chemistry.opencmis.client.api.Folder folder = getInternalFolder();
			
			if ( folder != null) {
				ObjectStore objectStore = (ObjectStore) objectStoreItem.getObjectStore();
				addChildren(folder, objectStore);
			}
			
			fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		} catch (Exception e ) {
			PluginLog.error(e);
		}
		return null;
	}

//	private void addContainees(com.filenet.api.core.Folder folder, ObjectStore objectStore) {
//		
//		Iterator<?> iterator = folder.get_Containees().iterator();
//		
//		while (iterator.hasNext() ) {
//			ReferentialContainmentRelationship relation = (ReferentialContainmentRelationship) iterator.next();
//			relation.fetchProperties( new String[]{ PropertyNames.HEAD, PropertyNames.CONTAINMENT_NAME } );
//			IndependentObject object = relation.get_Head();
//			addContainee(object, objectStore, relation.get_ContainmentName() );
//		}
//	}

//	private void addContainee(IndependentObject object, ObjectStore objectStore, String containmentName) {
//		
//		if ( object instanceof com.filenet.api.core.Document )
//		{
//			Document document = ObjectStoreItemFactory.createDocument( object, objectStoreItem, objectStore );
//			document.setParentPath( ((Folder) objectStoreItem).getPathName() );
//			document.setContainmentName( containmentName );
//			children.add( document );
//		}
//		else if ( object instanceof com.filenet.api.core.Folder )
//		{
//			Folder childFolder = ObjectStoreItemFactory.createFolder( object, objectStoreItem, objectStore );
//			childFolder.setContained(true);
//			children.add( childFolder );
//		}
//		else if ( object instanceof com.filenet.api.core.CustomObject )
//		{
//			children.add( ObjectStoreItemFactory.createCustomObject( object, objectStoreItem, objectStore ) );
//		}
//	}

	private ObjectStore addChildren(org.apache.chemistry.opencmis.client.api.Folder folder, ObjectStore objectStore) {

		folder.refresh();
		ItemIterable<CmisObject> children = folder.getChildren();
		
		for (CmisObject cmisObject : children ) {
			if ( cmisObject instanceof org.apache.chemistry.opencmis.client.api.Folder ) {
				this.children.add( ObjectStoreItemFactory.createFolder( cmisObject, objectStoreItem, objectStore ) );
			} else if ( cmisObject instanceof org.apache.chemistry.opencmis.client.api.Document ) {
				this.children.add( ObjectStoreItemFactory.createDocument( cmisObject, objectStoreItem, objectStore ) );
			} 
		}
		return objectStore;
	}

	private org.apache.chemistry.opencmis.client.api.Folder getInternalFolder() {
		
		org.apache.chemistry.opencmis.client.api.Folder folder;

		if ( objectStoreItem instanceof ObjectStore ) {
			Session session = ((ObjectStore) objectStoreItem).getSession();
			folder = session.getRootFolder();
		} else if ( objectStoreItem instanceof Folder ) {
			folder = ((Folder) objectStoreItem).getInternalFolder();
		} else {
			return null;
		}
		return folder;
	}
}
