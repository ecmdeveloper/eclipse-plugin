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

import java.text.MessageFormat;

import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.tasks.IFetchObjectTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectNotFoundException;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItemFactory;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;

/**
 * @author Ricardo Belfor
 *
 */
public class FetchObjectTask extends BaseTask implements IFetchObjectTask {
	
	private static final String UNSUPPORTED_OBJECT_TYPE_MESSAGE = "Objects of type {0} are not yet supported";
	
	private ObjectStore objectStore;
	private String id;
	private String className;
	private String objectType;
	
	public FetchObjectTask(IObjectStore objectStore2, String id, String className, String objectType ) {
		super();
		this.objectStore = (ObjectStore) objectStore2;
		this.id = id;
		this.className = className;
		this.objectType = objectType;
	}

	@Override
	protected Object execute() throws Exception {
		
		try {
			com.filenet.api.core.ObjectStore internalObjectStore = (com.filenet.api.core.ObjectStore) objectStore
					.getObjectStoreObject();
	
			IndependentObject object = internalObjectStore.getObject(className, id);
			if ( FOLDER_OBJECT_TYPE.equals( objectType) ) {
				return ObjectStoreItemFactory.createFolder(object, null, objectStore );
			} else if ( DOCUMENT_OBJECT_TYPE.equals(objectType) ) {
				return ObjectStoreItemFactory.createDocument(object, null, objectStore );
			} else if ( CUSTOM_OBJECT_TYPE.equals(objectType) ) {
				return ObjectStoreItemFactory.createCustomObject(object,null, objectStore);
			} else {
				throw new UnsupportedOperationException( MessageFormat.format( UNSUPPORTED_OBJECT_TYPE_MESSAGE, objectType ) );
			}
		} catch (EngineRuntimeException e) {
			if ( e.getExceptionCode().equals( ExceptionCode.E_OBJECT_NOT_FOUND ) ) {
				throw new ObjectNotFoundException(e);
			} else {
				throw e;
			}
		}
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}
}
