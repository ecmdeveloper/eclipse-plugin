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

package com.ecmdeveloper.plugin.filesystem;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;

import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.TaskManager;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.FetchObjectTask;

/**
 * @author Ricardo.Belfor
 *
 * Use it this way:
 *	IFileSystem fileSystem = EFS.getFileSystem( ObjectStoreFileSystem.SCHEME );
 *	URI uri = ObjectStoreFileSystem.toURI( objectStoreItem );
 *	IFileStore fileStore = fileSystem.getStore(uri);
 *	IDE.openEditorOnFileStore(window.getActivePage(), fileStore);
 */
public class ObjectStoreFileSystem extends FileSystem {

	public static final String OBJECT_TYPE_PARAMETER = "objectType";
	public static final String CLASS_NAME_PARAMETER = "className";
	public static final String SCHEME = "objectstorefile";
	
	private static final String FOLDER_OBJECT_TYPE = "Folder";
	private static final String DOCUMENT_OBJECT_TYPE = "Document";
	private static final String UNSUPPORTED_OBJECT_TYPE_MESSAGE = "Objects of type \"{0 are not supported";

	private Map<String,IFileStore> fileStoreCache = new HashMap<String, IFileStore>();
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileSystem#getStore(java.net.URI)
	 */
	@Override
	public IFileStore getStore(URI uri) {
		
		if ( fileStoreCache.containsKey( uri.toString() ) ) {
			return fileStoreCache.get( uri.toString() );
		}
		
		String objectStoreName = uri.getUserInfo();
		String connectionName = uri.getHost();
		String path = uri.getPath();
		Map<String,String> queryMap = getQueryMap( uri.getQuery() );
		
		ObjectStoresManager objectStoresManager = ObjectStoresManager.getManager();
		IObjectStore objectStore = objectStoresManager.getObjectStore(connectionName, objectStoreName);
		String className = queryMap.get(CLASS_NAME_PARAMETER);
		String objectType = queryMap.get(OBJECT_TYPE_PARAMETER);
		FetchObjectTask task = new FetchObjectTask(objectStore,path,className, objectType );
		
		try {
			IObjectStoreItem object = (IObjectStoreItem) TaskManager.getInstance().executeTaskSync(task);
			if ( object instanceof Document ) {
				int lastPathSeparatorIndex = path.lastIndexOf("/");
				((Document)object).setParentPath( path.substring(0, lastPathSeparatorIndex ) );
				((Document)object).setContainmentName( path.substring( lastPathSeparatorIndex + 1 ) );
			}
			
			IFileStore fileStore = ObjectStoreFileStoreFactory.getFileStore( object);
			fileStoreCache.put( uri.toString(), fileStore );
			return fileStore;
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> getQueryMap(String query) {
	    String[] params = query.split("&");
	    Map<String, String> map = new HashMap<String, String>();
	    for (String param : params)
	    {
	        String name = param.split("=")[0];
	        String value = param.split("=")[1];
	        map.put(name, value);
	    }
	    return map;
	}
	
	public static URI toURI(IObjectStoreItem objectStoreItem ) throws URISyntaxException {
	
		if ( objectStoreItem instanceof Folder ) {
			Folder folder = (Folder) objectStoreItem;
			
			return new URI(ObjectStoreFileSystem.SCHEME, folder
					.getObjectStore().getName(), folder.getObjectStore()
					.getConnection().getName(), -1, folder.getPathName(),
					ObjectStoreFileSystem.getQuery(folder), null);
			
		} else if ( objectStoreItem instanceof Document ) {
			Document document = (Document) objectStoreItem;

			return new URI(ObjectStoreFileSystem.SCHEME, document
					.getObjectStore().getName(), document.getObjectStore()
					.getConnection().getName(), -1, document.getPathName(),
					ObjectStoreFileSystem.getQuery(document), null);
		} else {
			
			throw new UnsupportedOperationException(MessageFormat.format(
					UNSUPPORTED_OBJECT_TYPE_MESSAGE, 
					objectStoreItem.getClass().getName()));
		}
	}
	public static String getQuery(IObjectStoreItem objectStoreItem ) {
		StringBuffer query = new StringBuffer();
		
		query.append( CLASS_NAME_PARAMETER );
		query.append( "=" );
		query.append( objectStoreItem.getClassName() );
		
		query.append( "&" );
		
		query.append( OBJECT_TYPE_PARAMETER );
		query.append( "=" );
		if ( objectStoreItem instanceof Folder ) {
			query.append( FOLDER_OBJECT_TYPE );
		} else if ( objectStoreItem instanceof Document ) {
			query.append( DOCUMENT_OBJECT_TYPE );
		}
		
		return query.toString();
	}

	@Override
	public int attributes() {
		return super.attributes();
	}

	@Override
	public IFileStore fromLocalFile(File file) {
		// TODO Auto-generated method stub
		return super.fromLocalFile(file);
	}
	
	
}
