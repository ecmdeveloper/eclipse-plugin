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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import com.ecmdeveloper.plugin.core.model.ICodeModule;
import com.ecmdeveloper.plugin.core.model.IObjectStore;

public class CodeModuleFile {

	private List<CodeModuleFileListener> listeners;

	private String id;
	private String name;
	private String filename;
	
	private ArrayList<File> files;
	private ArrayList<IJavaElement> javaElements;

	private String connectionName;
	private String connectionDisplayName;
	private String objectStoreName;
	private String objectStoreDisplayName;
	
	public CodeModuleFile(ICodeModule codeModule, IObjectStore objectStore) {
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
		javaElements = new ArrayList<IJavaElement>();
		
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

	public Collection<File> getFiles() {
		return files;
	}

	public Collection<IJavaElement> getJavaElements() {
		return javaElements;
	}
	
	public ArrayList<File> getContentElementFiles() throws Exception {
		ArrayList<File> contentElements = new ArrayList<File>();
		
		JavaElementFilesFinder javaElementFilesFinder = new JavaElementFilesFinder();
		for ( IJavaElement javaElement : javaElements) {
			contentElements.addAll( javaElementFilesFinder.findFiles(javaElement) );
		}
		contentElements.addAll(files);
		return contentElements;
	}

	public ArrayList<Object> getContentElements() throws Exception {
		ArrayList<Object> contentElements = new ArrayList<Object>();
		contentElements.addAll( javaElements );
		contentElements.addAll(files);
		return contentElements;
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
	
	public void removeFile( File file ) {
		files.remove( file );
		filesChanged();
	}
	
	public void addJavaElement(IJavaElement javaElement) {
		javaElements.add(javaElement);
		filesChanged();
	}

	public boolean removeJavaElement(IJavaElement javaElement) {
		boolean removed = javaElements.remove(javaElement);
		if (removed) {
			filesChanged();
		} 
		return removed;
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

	public boolean isEmpty() throws Exception {
		if ( ! files.isEmpty() ) {
			return false;
		}

		JavaElementFilesFinder javaElementFilesFinder = new JavaElementFilesFinder();
		for ( IJavaElement javaElement : javaElements) {
			if ( !javaElementFilesFinder.findFiles(javaElement).isEmpty() ) {
				return false;
			}
		}
		return true;
	}

	public Collection<String> getClassNames(String interfaceName ) throws Exception {
		ArrayList<IType> contentElements = new ArrayList<IType>();
		
		JavaElementClassesFinder javaElementFilesFinder = new JavaElementClassesFinder();
		for ( IJavaElement javaElement : javaElements) {
			contentElements.addAll( javaElementFilesFinder.findClasses( javaElement, interfaceName) );
		}

		Set<String> classNames = new HashSet<String>();
		for ( IType type : contentElements ) {
			classNames.add( getTypeName(type)  );
		}
		
		return classNames;
	}
	
	private String getTypeName(IType type ) {
		StringBuffer typeName =  new StringBuffer();
		IPackageFragment pack= type.getPackageFragment();
		if (!pack.isDefaultPackage()) {
			typeName.append( pack.getElementName() );
			typeName.append('.');
		}
		typeName.append( type.getElementName() );
		return typeName.toString();
	}
}
