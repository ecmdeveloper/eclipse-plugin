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

package com.ecmdeveloper.plugin.filesystem;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.LoadChildrenTask;
import com.ecmdeveloper.plugin.util.PluginLog;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ObjectStoreFolderItem extends ObjectStoreFileStore {

	protected Folder folder;
	protected Collection<IObjectStoreItem> children;
	
	public ObjectStoreFolderItem(Folder folder) {
		super(folder);
		this.folder = folder;
	}

	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor)
			throws CoreException {
		throw new CoreException(PluginLog.createStatus( IStatus.ERROR, IStatus.ERROR, "This store represents a directory", null ) );
	}

	@Override
	public URI toURI() {
		
		try {
			return ObjectStoreFileSystem.toURI(folder);
		} catch (URISyntaxException e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public String[] childNames(int options, IProgressMonitor monitor)
			throws CoreException {
		
			initializeChildren( monitor );
			
			ArrayList<String> childNames = new ArrayList<String>();
			
			for (IObjectStoreItem objectStoreItem : folder.getChildren() ) {
				if ( objectStoreItem instanceof Folder ) {
					childNames.add( objectStoreItem.getName() );
				} else if ( objectStoreItem instanceof Document ) {
					childNames.add( ((Document) objectStoreItem).getContainmentName() );
				}
			}
			
			return childNames.toArray( new String[0] );
			
	}

	private void initializeChildren(IProgressMonitor monitor) throws CoreException {
		
		if ( children == null ) {

			try {
				// TODO use the monitor
				
				LoadChildrenTask loadChildrenTask = new LoadChildrenTask( objectStoreItem );
				ObjectStoresManager.getManager().executeTaskSync(loadChildrenTask);
			
				children = folder.getChildren();
			} catch (ExecutionException e) {
				throw new CoreException(PluginLog.createStatus( IStatus.ERROR, IStatus.ERROR, "Getting childs failed", e ) );
			}
		}
	}

	@Override
	public IFileStore getChild(String name) {

		try {
			initializeChildren( null );
		} catch (CoreException e) {
			throw new RuntimeException( e );
		}
		
		for (IObjectStoreItem objectStoreItem : children ) {
			if ( objectStoreItem instanceof Folder ) {
				if ( name.equals( objectStoreItem.getName() ) ) {
					return ObjectStoreFileStoreFactory.getFileStore(objectStoreItem);
				}
			} else if ( objectStoreItem instanceof Document ) {
				if ( name.equals( ((Document) objectStoreItem).getContainmentName() ) ) {
					return ObjectStoreFileStoreFactory.getFileStore(objectStoreItem);
				}
			}
		}

		return null;
	}
}
