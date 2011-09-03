/**
 * Copyright 2009,2010, Ricardo Belfor
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.filenet.api.collection.ReferentialContainmentRelationshipSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Containable;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;

/**
 * This task moves object store items to the specified destination.
 * 
 * @author Ricardo Belfor
 *
 */
public class MoveTask extends BaseTask {

	protected IObjectStoreItem[] objectStoreItems;
	protected IObjectStoreItem destination;
	private Set<IObjectStoreItem> updateSet;
	
	/**
	 * Instantiates a new move task.
	 * 
	 * @param objectStoreItems the object store items
	 * @param destination the destination
	 */
	public MoveTask(IObjectStoreItem[] objectStoreItems, IObjectStoreItem destination ) {
		super();
		this.objectStoreItems = objectStoreItems;
		this.destination = destination;
	}

	/**
	 * Moves the object store items and notifies the listeners of the changes.
	 * An extra check is made for folders as they can be child items or
	 * contained items.
	 * 
	 * @return the object
	 * 
	 * @throws Exception the exception
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	protected Object execute() throws Exception {

		updateSet = new HashSet<IObjectStoreItem>();

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
		
			if ( !(objectStoreItem instanceof Folder) ) {
				moveContainedObject(objectStoreItem, destination);
			} else {
				if ( ! ((Folder)objectStoreItem).isContained() ) {
					moveChildObject( objectStoreItem, destination );
				} else {
					moveContainedObject(objectStoreItem, destination);
				}
			}

			if ( objectStoreItem.getParent() != null ) {
				objectStoreItem.getParent().removeChild( objectStoreItem );
				destination.addChild( objectStoreItem );
				updateSet.add( objectStoreItem.getParent() );
			}
		}
		updateSet.add( destination );
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}

	public IObjectStoreItem[] getObjectStoreItems() {
		return objectStoreItems;
	}

	public IObjectStoreItem[] getUpdatedObjectStoreItems() {
		return updateSet.toArray( new IObjectStoreItem[0]);
	}

	/**
	 * Move an object with a parent-child relationship.
	 * 
	 * @param objectStoreItem the object store item
	 * @param destination the destination
	 */
	private void moveChildObject(IObjectStoreItem objectStoreItem, IObjectStoreItem destination) {

		com.filenet.api.core.Folder folder = (com.filenet.api.core.Folder) ((ObjectStoreItem) objectStoreItem).getObjectStoreObject();

		if ( destination instanceof Folder ) {
			folder.set_Parent( (com.filenet.api.core.Folder) ((Folder) destination).getObjectStoreObject() );
		} else if (destination instanceof ObjectStore ) {
			com.filenet.api.core.ObjectStore objectStore = (com.filenet.api.core.ObjectStore) ((ObjectStoreItem) destination).getObjectStoreObject();
			objectStore.fetchProperties( new String[] { PropertyNames.ROOT_FOLDER } ); 
			folder.set_Parent( objectStore.get_RootFolder() );
		}
		folder.save( RefreshMode.REFRESH );
	}

	/**
	 * Move a contained object.
	 * 
	 * @param objectStoreItem the object store item
	 * @param destination the destination
	 */
	@SuppressWarnings("unchecked")
	private void moveContainedObject(IObjectStoreItem objectStoreItem, IObjectStoreItem destination) {

		IndependentlyPersistableObject object = ((ObjectStoreItem) objectStoreItem).getObjectStoreObject();

		if ( ! ( object instanceof Containable) ) {
			throw new UnsupportedOperationException( "An object of this type cannot be moved" );
		}

		object.fetchProperties(new String[] { PropertyNames.CONTAINERS } );
		PropertyFilter propertyFilter = new PropertyFilter();
		propertyFilter.addIncludeProperty( new FilterElement(null, null, null, PropertyNames.TAIL, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.ID, null ) );

		ReferentialContainmentRelationshipSet relations = ((Containable) object).get_Containers();

		Iterator<ReferentialContainmentRelationship> iterator = relations.iterator();
		
		while ( iterator.hasNext() ) {
			
			ReferentialContainmentRelationship relationship = iterator.next();
			relationship.fetchProperties( propertyFilter );
			com.filenet.api.core.Folder container = (com.filenet.api.core.Folder) relationship.get_Tail();
			
			if ( container.get_Id().toString().equals( objectStoreItem.getParent().getId() ) ) {
				relationship.set_Tail( ((ObjectStoreItem)destination).getObjectStoreObject() );
				relationship.save(RefreshMode.REFRESH);
				return;
			}
		}
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return (ContentEngineConnection) destination.getObjectStore().getConnection();
	}
}
