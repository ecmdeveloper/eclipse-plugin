/**
 * Copyright 2010, Ricardo Belfor
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

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.util.PluginLog;

/**
 * @author Ricardo.Belfor
 *
 */
public class RefreshTask extends BaseTask {

	private IObjectStoreItem[] objectStoreItems;

	public RefreshTask( IObjectStoreItem[] objectStoreItems ) {
		this.objectStoreItems = objectStoreItems;
	}

	public RefreshTask( IObjectStoreItem objectStoreItem ) {
		this.objectStoreItems = new IObjectStoreItem[] { objectStoreItem };
	}
	
	@Override
	public Object call() throws Exception {

		for (IObjectStoreItem objectStoreItem : objectStoreItems) {
			try {
				objectStoreItem.refresh();
			} catch (Exception e) {
				PluginLog.error(e);
			}
		}

		fireTaskCompleteEvent( TaskResult.COMPLETED );

		return null;
	}

	public IObjectStoreItem[] getObjectStoreItems() {
		return objectStoreItems;
	}
}
