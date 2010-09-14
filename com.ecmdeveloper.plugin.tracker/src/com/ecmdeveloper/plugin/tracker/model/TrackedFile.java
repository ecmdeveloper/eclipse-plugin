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

package com.ecmdeveloper.plugin.tracker.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;


/**
 * @author Ricardo.Belfor
 *
 */
public class TrackedFile implements IAdaptable {

	private String id;
	private String name;
	private String className;
	private String filename;
	private String versionSeriesId;
	private String connectionName;
	private String connectionDisplayName;
	private String objectStoreName;
	private String objectStoreDisplayName;
	private IFile file;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
		Path path = new Path( getFilename() );
		file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}
	
	public String getVersionSeriesId() {
		return versionSeriesId;
	}
	
	public void setVersionSeriesId(String versionSeriesId) {
		this.versionSeriesId = versionSeriesId;
	}
	
	public String getConnectionName() {
		return connectionName;
	}
	
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}
	
	public String getConnectionDisplayName() {
		return connectionDisplayName;
	}
	
	public void setConnectionDisplayName(String connectionDisplayName) {
		this.connectionDisplayName = connectionDisplayName;
	}
	
	public String getObjectStoreName() {
		return objectStoreName;
	}
	
	public void setObjectStoreName(String objectStoreName) {
		this.objectStoreName = objectStoreName;
	}
	
	public String getObjectStoreDisplayName() {
		return objectStoreDisplayName;
	}
	
	public void setObjectStoreDisplayName(String objectStoreDisplayName) {
		this.objectStoreDisplayName = objectStoreDisplayName;
	}

	public IFile getFile() {
		return file;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}
}
