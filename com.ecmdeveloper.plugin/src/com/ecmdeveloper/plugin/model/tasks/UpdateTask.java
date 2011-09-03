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

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * @author Ricardo.Belfor
 *
 */
public class UpdateTask extends BaseTask {

	protected IObjectStoreItem[] objectStoreItems;
	
	public UpdateTask( IObjectStoreItem objectStoreItem ) {
		this.objectStoreItems = new IObjectStoreItem[] { objectStoreItem }; 
	}
	
	public UpdateTask( IObjectStoreItem[] objectStoreItems ) {
		this.objectStoreItems = objectStoreItems;
	}

	public IObjectStoreItem[] getObjectStoreItems() {
		return objectStoreItems;
	}

	@Override
	protected Object execute() throws Exception {

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			((ObjectStoreItem)objectStoreItem).save();
		}

		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		if ( objectStoreItems.length == 0) {
			throw new IllegalArgumentException();
		}
		return (ContentEngineConnection) objectStoreItems[0].getObjectStore().getConnection();
	}
}
