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
package com.ecmdeveloper.plugin.model.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.ILoadChildrenTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItemFactory;
import com.ecmdeveloper.plugin.util.CEIterable;
import com.ecmdeveloper.plugin.util.PluginLog;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.PropertyFilter;

/**
 * This task is used for loading the children of an item.
 * 
 * @author Ricardo Belfor
 *
 */
public class LoadChildrenTask extends BaseTask implements ILoadChildrenTask {

	private static final PropertyFilter propertyFilter = new PropertyFilter();
	{
		propertyFilter.addIncludeProperty(0, null, true, PropertyNames.CONTAINEES, null);
		propertyFilter.addIncludeProperty(1, null, false, PropertyNames.SUB_FOLDERS, null);
		propertyFilter.addIncludeProperty(2, null, null, PropertyNames.HEAD, null );
		propertyFilter.addIncludeProperty(1, null, null, PropertyNames.CONTAINMENT_NAME, null );
		
	}
	private ObjectStoreItem objectStoreItem;
	private ArrayList<IObjectStoreItem> children;
	private Folder parentFolder;
	private final Collection<String> extraProperties;
	
	/**
	 * The constructor of this task is used to pass all the relevant input
	 * to perform the task.
	 * 
	 * @param objectStoreItem the object store item
	 */
	public LoadChildrenTask( ObjectStoreItem objectStoreItem, Collection<String> extraProperties ) {
		this.objectStoreItem = objectStoreItem;
		this.extraProperties = extraProperties;
	}

	public LoadChildrenTask( ObjectStoreItem objectStoreItem ) {
		this(objectStoreItem, null);
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
	protected Object execute() throws Exception {

		try {
			children = new ArrayList<IObjectStoreItem>();
			parentFolder = getParentFolder();
			
			com.filenet.api.core.Folder folder = getInternalFolder();
			
			if ( folder != null) {
				ObjectStore objectStore = objectStoreItem.getObjectStore();
				addSubFolders(folder, objectStore);
				addContainees(folder, objectStore);
			}
			
			fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		} catch (Exception e ) {
			PluginLog.error(e);
		}
		return null;
	}

	private void addContainees(com.filenet.api.core.Folder folder, ObjectStore objectStore) {
		
		Iterator<?> iterator = folder.get_Containees().iterator();
		
		while (iterator.hasNext() ) {
			ReferentialContainmentRelationship relation = (ReferentialContainmentRelationship) iterator.next();
			IndependentObject object = relation.get_Head();
			addContainee(object, objectStore, relation.get_ContainmentName() );
		}
	}

	private void addContainee(IndependentObject object, ObjectStore objectStore, String containmentName) {
		
		if ( object instanceof com.filenet.api.core.Document )
		{
			Document document = ObjectStoreItemFactory.createDocument( object, parentFolder, objectStore );
			document.setParentPath( ((Folder) parentFolder).getPathName() );
			document.setContainmentName( containmentName );
			children.add( document );
		}
		else if ( object instanceof com.filenet.api.core.Folder )
		{
			Folder childFolder = ObjectStoreItemFactory.createFolder( object, parentFolder, objectStore );
			childFolder.setContained(true);
			children.add( childFolder );
		}
		else if ( object instanceof com.filenet.api.core.CustomObject )
		{
			children.add( ObjectStoreItemFactory.createCustomObject( object, parentFolder, objectStore ) );
		}
	}

	private ObjectStore addSubFolders(com.filenet.api.core.Folder folder, ObjectStore objectStore) {
		
		for (com.filenet.api.core.Folder subFolder : new CEIterable<com.filenet.api.core.Folder>(
				folder.get_SubFolders())) {
			children.add( ObjectStoreItemFactory.createFolder( subFolder, objectStoreItem, objectStore ) );
		}
		return objectStore;
	}

	private Folder getParentFolder() {

		if ( objectStoreItem instanceof ObjectStore ) {
			com.filenet.api.core.ObjectStore objectStore = (com.filenet.api.core.ObjectStore) objectStoreItem.getObjectStoreObject();
			objectStore.fetchProperties( new String[] { PropertyNames.ROOT_FOLDER } );
			return ObjectStoreItemFactory.createFolder( objectStore.get_RootFolder(), objectStoreItem, objectStoreItem.getObjectStore() );
		}
		
		return (Folder) objectStoreItem;
	}
	
	private com.filenet.api.core.Folder getInternalFolder() {

		com.filenet.api.core.Folder folder = (com.filenet.api.core.Folder) parentFolder.getObjectStoreObject();
		
		if ( extraProperties == null) {
			folder.fetchProperties( propertyFilter );
		} else {
			PropertyFilter customPropertyFilter = getCustomPropertyFilter();
			folder.fetchProperties( customPropertyFilter );
		}
		
//		com.ecmdeveloper.plugin.util.ObjectDumper od = new com.ecmdeveloper.plugin.util.ObjectDumper();
//		try {
//			od.dump(folder);
//		} catch (java.io.IOException e) {
//			e.printStackTrace();
//		}
		return folder;
	}

	private PropertyFilter getCustomPropertyFilter() {

		PropertyFilter customPropertyFilter = new PropertyFilter();
		
		customPropertyFilter.addIncludeProperty(0, null, true, PropertyNames.CONTAINEES, null);
		customPropertyFilter.addIncludeProperty(1, null, false, PropertyNames.SUB_FOLDERS, null);
		customPropertyFilter.addIncludeProperty(2, null, null, PropertyNames.HEAD, null );
		customPropertyFilter.addIncludeProperty(1, null, null, PropertyNames.CONTAINMENT_NAME, null );
		
		for (String extraProperty : extraProperties ) {
			customPropertyFilter.addIncludeProperty(1, null, null, extraProperty, null );
		}
		return customPropertyFilter;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStoreItem.getObjectStore().getConnection();
	}
}
