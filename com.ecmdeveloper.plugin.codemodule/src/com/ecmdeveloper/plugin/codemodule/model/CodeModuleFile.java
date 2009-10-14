package com.ecmdeveloper.plugin.codemodule.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeModuleFile {

	private List<CodeModuleFileListener> listeners;

	protected String id;
	protected String name;
	protected String objectStore;
	protected String connection;
	protected String filename;
	
	protected ArrayList<File> files;

	private String connectionName;

	private String objectStoreName;
	
	public CodeModuleFile(String name, String id, String connectionName, String objectStoreName) {

		this.files = new ArrayList<File>();
		this.name = name;
		this.id = id;
		this.connectionName = connectionName;
		this.objectStoreName = objectStoreName;

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

	public String getId() {
		return id;
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
