/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.util;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;

public class ContentCache implements IPartListener2 {

	private static final String CONTENT_CACHE_FOLDER = "content_cache";
	private Set<IPartService> partServicesListeningTo = new HashSet<IPartService>();
	private Map<String,String> cacheFiles = new HashMap<String,String>();
	private IPath parentPath;
	
	public ContentCache(IPath parentPath) {
		this.parentPath = parentPath;
	}

	public IPath getTempFolderPath(IObjectStoreItem objectStoreItem) {
		
		IPath cacheLocation = getRootPath().append(getTempFolderName(objectStoreItem));

		if ( ! cacheLocation.toFile().exists() ) {
			cacheLocation.toFile().mkdir();
		}
		
		return cacheLocation;
	}

	public void clear() {
	    for ( File file : getRootPath().toFile().listFiles() ) {
	    	deleteDirectory( file );
	    }
	}
	
	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	public IPath getRootPath() {
		
		IPath cacheLocation = parentPath.append( CONTENT_CACHE_FOLDER );

		if ( ! cacheLocation.toFile().exists() ) {
			cacheLocation.toFile().mkdir();
		}
		
		return cacheLocation;
	}
	
	public void registerFile( String uriString, String filename ) {
		cacheFiles.put(uriString, filename);
	}
	
	private String getTempFolderName(IObjectStoreItem objectStoreItem) {
		
		StringBuffer path = new StringBuffer();
		
		path.append( objectStoreItem.getObjectStore().getConnection().getName() );
		path.append( "_" );
		path.append( objectStoreItem.getObjectStore().getName() );
		path.append( "_" );
		path.append( objectStoreItem.getId() );
		
		return path.toString();
	}
	
	public void registerAsListener(IWorkbenchWindow window ) {
		partServicesListeningTo.add( window.getPartService() );
		window.getPartService().addPartListener(this);
	}

	public void stop() {
		
		for (IPartService partService : partServicesListeningTo ) {
			partService.removePartListener(this);
		}
		
		partServicesListeningTo.clear();
	}
	
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		
		if ( partRef instanceof IEditorReference ) {
			try {
				deleteEditorInput(partRef);
			} catch (PartInitException e) {
				PluginLog.error(e);
			}
		}	
	}

	private void deleteEditorInput(IWorkbenchPartReference partRef) throws PartInitException {
		IEditorInput editorInput = ((IEditorReference)partRef).getEditorInput();
		if ( editorInput instanceof FileStoreEditorInput ) {
			deleteFileStoreEditorInput(editorInput);
		}
	}

	private void deleteFileStoreEditorInput(IEditorInput editorInput) {
		URI uri = ((FileStoreEditorInput)editorInput).getURI();
		if ( cacheFiles.containsKey( uri.toString() ) ) {
			String filename = cacheFiles.get( uri.toString() );
			deleteDirectory( new File( filename) );
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}

}
