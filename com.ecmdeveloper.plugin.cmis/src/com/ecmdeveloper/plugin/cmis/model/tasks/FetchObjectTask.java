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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.text.MessageFormat;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;

import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IFetchObjectTask;

/**
 * @author Ricardo Belfor
 *
 */
public class FetchObjectTask extends AbstractTask implements IFetchObjectTask {
	
	private static final String UNSUPPORTED_OBJECT_TYPE_MESSAGE = "Objects of type {0} are not yet supported";
	
	private ObjectStore objectStore;
	private String id;
	@SuppressWarnings("unused")
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
	public Object call() throws Exception {
		
//		try {
			Session session = objectStore.getSession();

			CmisObject cmisObject = session.getObject(id);
			
			if ( FOLDER_OBJECT_TYPE.equals( objectType) ) {
				return ObjectStoreItemFactory.createFolder(cmisObject, null, objectStore );
			} else if ( DOCUMENT_OBJECT_TYPE.equals(objectType) ) {
				return ObjectStoreItemFactory.createDocument(cmisObject, null, objectStore );
			} else {
				throw new UnsupportedOperationException( MessageFormat.format( UNSUPPORTED_OBJECT_TYPE_MESSAGE, objectType ) );
			}
//		} catch (EngineRuntimeException e) {
//			if ( e.getExceptionCode().equals( ExceptionCode.E_OBJECT_NOT_FOUND ) ) {
//				throw new ObjectNotFoundException(e);
//			} else {
//				throw e;
//			}
//		}
	}

}
