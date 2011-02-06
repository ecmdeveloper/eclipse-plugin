/**
 * Copyright 2010, Ricardo Belfor
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

import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.util.Id;

/**
 * @author ricardo.belfor
 *
 */
public class ExecuteMethodTask extends BaseTask {

	private ObjectStoreItem objectStoreItem;
	private final Method method;
	private final Object targetClass;
	private final Writer writer;

	public ExecuteMethodTask(ObjectStoreItem objectStoreItem, Method method, Object targetClass, Writer writer) {
		this.objectStoreItem = objectStoreItem;
		this.method = method;
		this.targetClass = targetClass;
		this.writer = writer;
	}

	@Override
	protected Object execute() throws Exception {
		
		ObjectStore objectStore = objectStoreItem.getObjectStore();
		com.filenet.api.core.ObjectStore internalObjectStore = (com.filenet.api.core.ObjectStore) objectStore
				.getObjectStoreObject();
		
		IndependentObject objectStoreObject = internalObjectStore.fetchObject(objectStoreItem.getClassName(), new Id( objectStoreItem.getId() ), null);
		
		try {
			method.invoke(targetClass, new Object[] {objectStoreObject, writer});
		} catch (Exception e) {
			PrintWriter printWriter = new PrintWriter(writer); 
			e.printStackTrace(printWriter);
		}
		
		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStoreItem.getObjectStore().getConnection();
	}
}
