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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.enums.Action;

import com.ecmdeveloper.plugin.cmis.model.Folder;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IFolder;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IMoveTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;

/**
 * This task moves object store items to the specified destination.
 * 
 * @author Ricardo Belfor
 *
 */
public class MoveTask extends AbstractTask implements IMoveTask {

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
	public Object call() throws Exception {

		updateSet = new HashSet<IObjectStoreItem>();

		org.apache.chemistry.opencmis.client.api.Folder destinationFolder = null;
		
		destinationFolder = getDestinationFolder(destinationFolder);
		if ( destinationFolder == null ) return null;
		
		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
		
			if ( !(objectStoreItem instanceof IFolder) ) {
				moveContainedObject((ObjectStoreItem) objectStoreItem, destinationFolder);
			} else {
				if ( ! ((IFolder)objectStoreItem).isContained() ) {
					moveChildObject( (ObjectStoreItem) objectStoreItem, destination );
				} else {
					moveContainedObject((ObjectStoreItem) objectStoreItem, destinationFolder);
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

	private org.apache.chemistry.opencmis.client.api.Folder getDestinationFolder(
			org.apache.chemistry.opencmis.client.api.Folder destinationFolder) {
		if ( destination instanceof Folder ) {
			destinationFolder = ((Folder) destination).getInternalFolder();
		} else if (destination instanceof ObjectStore ) {
			Session session = ((ObjectStore) destination).getSession();
			destinationFolder = session.getRootFolder();
		}
		return destinationFolder;
	}

	@Override
	public IObjectStoreItem[] getObjectStoreItems() {
		return objectStoreItems;
	}

	@Override
	public IObjectStoreItem[] getUpdatedObjectStoreItems() {
		return updateSet.toArray( new IObjectStoreItem[0]);
	}

	/**
	 * Move an object with a parent-child relationship.
	 * 
	 * @param objectStoreItem the object store item
	 * @param destination the destination
	 */
	private void moveChildObject(ObjectStoreItem objectStoreItem, IObjectStoreItem destination) {

		CmisObject cmisObject = objectStoreItem.getCmisObject();
		Map<String, Object> properties = new HashMap<String, Object>();

		if ( destination instanceof Folder ) {
			properties.put(PropertyIds.PARENT_ID, destination.getId() );
		} else if (destination instanceof ObjectStore ) {
			Session session = ((ObjectStore) destination).getSession();
			org.apache.chemistry.opencmis.client.api.Folder rootFolder = session.getRootFolder();
			properties.put(PropertyIds.PARENT_ID, rootFolder.getId() );
		}
		cmisObject.updateProperties( properties );
	}

	/**
	 * Move a contained object.
	 * 
	 * @param objectStoreItem the object store item
	 * @param destinationFolder the destination
	 */
	private void moveContainedObject(ObjectStoreItem objectStoreItem,
			org.apache.chemistry.opencmis.client.api.Folder destinationFolder) {

		if ( isMoveAllowed(objectStoreItem.getCmisObject()) ) {
			
			ObjectId destinationId = new ObjectIdImpl(destinationFolder.getId() );
			FileableCmisObject fileableCmisObject = (FileableCmisObject) objectStoreItem.getCmisObject();

			List<org.apache.chemistry.opencmis.client.api.Folder> parents = fileableCmisObject.getParents();

			for ( org.apache.chemistry.opencmis.client.api.Folder parent : parents ) {
				if ( parent.getId().equalsIgnoreCase( objectStoreItem.getParent().getId() ) ) {
					fileableCmisObject.move(parent, destinationId );
					return;
				}
			}
		}
	}
	
	private boolean isMoveAllowed(CmisObject cmisObject) {
		
		if ( !(cmisObject instanceof FileableCmisObject)) {
			return false;
		}

		AllowableActions allowableActions = cmisObject.getAllowableActions();
		if ((allowableActions == null) || (allowableActions.getAllowableActions() == null)) {
			return true;
		}

		return allowableActions.getAllowableActions().contains(Action.CAN_MOVE_OBJECT);
	}
}
