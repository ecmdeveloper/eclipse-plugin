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
package com.ecmdeveloper.plugin.codemodule.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.ObjectStore;

public class CodeModuleFile {

	private List<CodeModuleFileListener> listeners;

	protected String id;
	protected String name;
	protected String filename;
	
	protected ArrayList<File> files;

	private String connectionName;
	private String connectionDisplayName;
	private String objectStoreName;
	private String objectStoreDisplayName;
	
	public CodeModuleFile(CodeModule codeModule, ObjectStore objectStore) {
		this(codeModule.getName(), codeModule.getId(), objectStore.getConnection().getName(), objectStore.getConnection().getDisplayName(), objectStore.getName(), objectStore.getDisplayName() );
	}
	
	public CodeModuleFile(String name, String id, String connectionName, String connectionDisplayName, String objectStoreName, String objectStoreDisplayName ) {

		this.name = name;
		this.id = id;
		this.connectionName = connectionName;
		this.connectionDisplayName = connectionDisplayName;
		this.objectStoreName = objectStoreName;
		this.objectStoreDisplayName = objectStoreDisplayName;

		files = new ArrayList<File>();
	    listeners = new ArrayList<CodeModuleFileListener>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		
		if ( ( this.name == null && name != null ) || ! this.name.equals(name) ) {
			this.name = name;
			nameChanged();
		}
	}

	public String getConnectionDisplayName() {
		return connectionDisplayName;
	}

	public String getObjectStoreDisplayName() {
		return objectStoreDisplayName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<File> getFiles() {
		return files;
	}
	
	public String getObjectStoreName() {
		return objectStoreName;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void addFile(File file) {
		files.add( file );
		filesChanged();
	}
	
	public void removeFile( File file )
	{
		files.remove( file );
		filesChanged();
	}
	
	public void addPropertyFileListener(CodeModuleFileListener listener) {
		if ( ! listeners.contains(listener) ) {
			listeners.add(listener);
		}
	}

	public void removePropertyFileListener( CodeModuleFileListener listener) {
		listeners.remove(listener);
	}

	private void nameChanged() {
		Iterator<CodeModuleFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().nameChanged();
		}
	}

	private void filesChanged() {
		Iterator<CodeModuleFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().filesChanged();
		}
	}
}
