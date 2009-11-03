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

package com.ecmdeveloper.plugin.model.tasks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
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
 * @author Ricardo Belfor
 *
 */
public class MoveTask extends BaseTask {

	protected IObjectStoreItem[] objectStoreItems;
	protected IObjectStoreItem destination;
	
	public MoveTask(IObjectStoreItem[] objectStoreItems, IObjectStoreItem destination ) {
		super();
		this.objectStoreItems = objectStoreItems;
		this.destination = destination;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Object call() throws Exception {

		Set<IObjectStoreItem> updateSet = new HashSet<IObjectStoreItem>();

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
		
			if ( !(objectStoreItem instanceof Folder) ) { // TODO: add check for contained folder
				moveContainedObject(objectStoreItem, destination);
			} else {
				moveChildObject( objectStoreItem, destination );
			}

			if ( objectStoreItem.getParent() != null ) {
				objectStoreItem.getParent().removeChild( objectStoreItem );
				destination.addChild( objectStoreItem );
				updateSet.add( objectStoreItem.getParent() );
			}
		}
		updateSet.add( destination );

		fireObjectStoreItemsChanged(null, objectStoreItems, updateSet.toArray( new IObjectStoreItem[0]) );
		
		return null;
	}

	private void moveChildObject(IObjectStoreItem objectStoreItem, IObjectStoreItem destination) {

		com.filenet.api.core.Folder folder = (com.filenet.api.core.Folder) ((ObjectStoreItem) objectStoreItem).getObjectStoreObject();

		if ( destination instanceof Folder ) {
			folder.set_Parent( (com.filenet.api.core.Folder) ((Folder) destination).getObjectStoreObject() );
		} else if (destination instanceof ObjectStore ) {

			throw new UnsupportedOperationException( "Fix this!");
// TODO: fix this			
//			Folder rootFolder = ((ObjectStore)destination).getRootFolder();
//			folder.set_Parent( (com.filenet.api.core.Folder) rootFolder.getObjectStoreObject() );
		}
		folder.save( RefreshMode.REFRESH );
	}

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
}
