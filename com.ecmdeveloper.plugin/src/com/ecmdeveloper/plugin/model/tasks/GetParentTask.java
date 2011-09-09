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

package com.ecmdeveloper.plugin.model.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IGetParentTask;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoreItemFactory;
import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Containable;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;

/**
 * @author ricardo.belfor
 *
 */
public class GetParentTask extends BaseTask implements IGetParentTask {

	private final ObjectStoreItem child;
	private final Collection<IFolder> parents;
	
	public GetParentTask(IObjectStoreItem child) {
		this.child = (ObjectStoreItem) child;
		parents = new ArrayList<IFolder>();
	}

	@Override
	public Collection<IFolder> getParents() {
		return parents;
	}

	@Override
	protected Object execute() throws Exception {
		
		ReferentialContainmentRelationshipSet containers = getContainers();

		if (containers != null && !containers.isEmpty() ) {
			Iterator<?> iterator = containers.iterator();
			while (iterator.hasNext() ) {
				ReferentialContainmentRelationship relationsship = (ReferentialContainmentRelationship) iterator.next();
				Folder folder = getFolderFromRelationship(relationsship);
				parents.add( folder );
			}
		}

		if ( childIsFolder() ) {
			Folder folder = getParentFolder();
			parents.add( folder );
		}			

		return null;
	}

	private ReferentialContainmentRelationshipSet getContainers() {
		
		IndependentlyPersistableObject objectStoreObject = child.getObjectStoreObject();
		ReferentialContainmentRelationshipSet containers = null;
		if ( objectStoreObject instanceof Containable ) {
			PropertyFilter pf = new PropertyFilter();
			pf.addIncludeProperty( new FilterElement( null, null, null, PropertyNames.CONTAINERS, null ) );
			objectStoreObject.fetchProperties(pf);
	
			Containable containable = (Containable) objectStoreObject;
			containers = containable.get_Containers();
		}
		return containers;
	}

	private Folder getFolderFromRelationship(ReferentialContainmentRelationship relationsship) {
		relationsship.fetchProperties( new String[] { PropertyNames.TAIL } );
		com.filenet.api.core.Folder internalFolder = (com.filenet.api.core.Folder) relationsship.get_Tail();
		Folder folder = ObjectStoreItemFactory.createFolder(internalFolder, null, child.getObjectStore() );
		folder.refresh();
		return folder;
	}

	private boolean childIsFolder() {
		return child instanceof Folder;
	}

	private Folder getParentFolder() {
		com.filenet.api.core.Folder internalChildFolder = (com.filenet.api.core.Folder) child.getObjectStoreObject();
		internalChildFolder.fetchProperties( new String[] { PropertyNames.PARENT } );
		Folder folder = ObjectStoreItemFactory.createFolder(internalChildFolder.get_Parent(), null, child.getObjectStore() );
		folder.refresh();
		return folder;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return child.getObjectStore().getConnection();
	}
}
