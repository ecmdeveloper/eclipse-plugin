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

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.core.IndependentObject;

/**
 * @author Ricardo Belfor
 *
 */
public class FetchObjectTask extends BaseTask {

	private static final String FOLDER_OBJECT_TYPE = "Folder";
	private static final String DOCUMENT_OBJECT_TYPE = "Document";
	private static final String UNSUPPORTED_OBJECT_TYPE_MESSAGE = "Objects of type {0} are not yet supported";
	
	private ObjectStore objectStore;
	private String id;
	private String className;
	private String objectType;
	
	public FetchObjectTask(ObjectStore objectStore, String id, String className, String objectType ) {
		super();
		this.objectStore = objectStore;
		this.id = id;
		this.className = className;
		this.objectType = objectType;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public IObjectStoreItem call() throws Exception {
		
		com.filenet.api.core.ObjectStore internalObjectStore = (com.filenet.api.core.ObjectStore) objectStore
				.getObjectStoreObject();

//		if ( id.startsWith("/") ) {
//			if ( FOLDER_OBJECT_TYPE.equals( objectType) ) {
//				com.filenet.api.core.Folder internalFolder = Factory.Folder.getInstance(internalObjectStore, className, id );
//				return new Folder(internalFolder, null, objectStore );
//			} else if ( DOCUMENT_OBJECT_TYPE.equals(objectType) ) {
//				com.filenet.api.core.Document internalDocument = Factory.Document.getInstance(internalObjectStore, className, id );
//				return new Document(internalDocument, null, objectStore );
//			} else {
//				throw new UnsupportedOperationException( MessageFormat.format( UNSUPPORTED_OBJECT_TYPE_MESSAGE, objectType ) );
//			}
//		} else {
			IndependentObject object = internalObjectStore.getObject(className, id);
			if ( FOLDER_OBJECT_TYPE.equals( objectType) ) {
				return new Folder(object, null, objectStore );
			} else if ( DOCUMENT_OBJECT_TYPE.equals(objectType) ) {
				return new Document(object, null, objectStore );
			} else {
				throw new UnsupportedOperationException( MessageFormat.format( UNSUPPORTED_OBJECT_TYPE_MESSAGE, objectType ) );
			}
//		}
	}
}
