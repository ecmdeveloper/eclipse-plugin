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

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IUpdateTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;

/**
 * @author Ricardo.Belfor
 *
 */
public class UpdateTask extends AbstractTask implements IUpdateTask {

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
	public Object call() throws Exception {
		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			((ObjectStoreItem)objectStoreItem).save();
		}

		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}
}
