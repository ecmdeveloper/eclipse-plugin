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

import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IDeleteTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;

/**
 * This class is used to perform deleting of objects.
 * 
 * @author Ricardo Belfor
 *
 */
public class DeleteTask extends AbstractTask implements IDeleteTask {

	private IObjectStoreItem[] objectStoreItems;
	private boolean deleteAllVersions;
	
	public boolean isDeleteAllVersions() {
		return deleteAllVersions;
	}

	/**
	 * The constructor of this task is used to pass all the relevant input
	 * to perform the task.
	 * 
	 * @param objectStoreItems the object store items
	 * @param deleteAllVersions the delete all verions flag
	 */
	public DeleteTask( IObjectStoreItem[] objectStoreItems, boolean deleteAllVersions) {
		this.objectStoreItems = objectStoreItems;
		this.deleteAllVersions = deleteAllVersions;
	}

	public IObjectStoreItem[] getObjectStoreItems() {
		return objectStoreItems;
	}

	/**
	 * Performs the delete task. If the input item is an document there is a
	 * choice if all versions or just this version of the document should be
	 * deleted. The parent of the input item is updated to reflect the changes
	 * in the model. Also the ObjectStoresManager class is used to notify all
	 * listeners.
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Object call() throws Exception {

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			deleteObjectStoreItem((ObjectStoreItem) objectStoreItem);
		}
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}

	private void deleteObjectStoreItem(ObjectStoreItem objectStoreItem) {
		objectStoreItem.getCmisObject().delete(deleteAllVersions);
	}
}
