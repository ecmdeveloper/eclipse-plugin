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

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class ObjectStoreFileStore extends FileStore {

	protected ObjectStoreItem objectStoreItem;
	
	public ObjectStoreFileStore(ObjectStoreItem objectStoreItem) {
		this.objectStoreItem = objectStoreItem;
	}

	@Override
	public String getName() {
		return objectStoreItem.getName();
	}
	
	@Override
	public IFileInfo fetchInfo(int options, IProgressMonitor monitor)
			throws CoreException {

		FileInfo fileInfo = new FileInfo(getName());
		fileInfo.setDirectory( this instanceof ObjectStoreFolderItem );
		fileInfo.setExists(true);
		fileInfo.setAttribute(EFS.ATTRIBUTE_READ_ONLY, true);
		
		return fileInfo;
	}

	@Override
	public IFileStore getParent() {
		return ObjectStoreFileStoreFactory.getFileStore( objectStoreItem.getParent() );
	}
}
