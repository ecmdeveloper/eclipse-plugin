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

import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStores;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IAddObjectStoreTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;

/**
 * @author ricardo.belfor
 *
 */
public class AddObjectStoreTask extends AbstractTask implements IAddObjectStoreTask {

	private final IObjectStore objectStore;
	private final IObjectStores objectStores;
	
	public AddObjectStoreTask(IObjectStore objectStore, IObjectStores objectStores) {
		this.objectStore = objectStore;
		this.objectStores = objectStores;
	}

	@Override
	public Object call() throws Exception {
		objectStore.connect();
		objectStores.add(objectStore);
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}

	@Override
	public IObjectStore getObjectStore() {
		return objectStore;
	}
}
