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
package com.ecmdeveloper.plugin.model;


/**
 * @author Ricardo.Belfor
 * 
 */
public class ObjectStoreItemFactory {

	private static ObjectStoreItemsModel objectStoreItemsModel = ObjectStoreItemsModel.getInstance();
	
	public static Folder createFolder(Object folder, IObjectStoreItem parent, ObjectStore objectStore ) {
		Folder newFolder = new Folder(folder,parent,objectStore);
		addToModel(newFolder);
		return newFolder;
	}

	public static Folder createFolder(Object folder, IObjectStoreItem parent, ObjectStore objectStore, boolean saved ) {
		Folder newFolder = new Folder(folder, parent, objectStore, saved );
		// TODO do something for unsaved documents
		addToModel(newFolder);
		return newFolder;
	}

	public static Document createDocument(Object document, IObjectStoreItem parent, ObjectStore objectStore) {
		Document newDocument = new Document(document, parent,objectStore );
		addToModel(newDocument);
		return newDocument;
	}
	
	public static Document createDocument(Object document, IObjectStoreItem parent, ObjectStore objectStore, boolean saved ) {
		Document newDocument = new Document(document, parent,objectStore, saved );
		// TODO do something for unsaved documents
		addToModel(newDocument);
		return newDocument;
	}

	public static CustomObject createCustomObject(Object customObject, IObjectStoreItem parent, ObjectStore objectStore) {
		CustomObject newCustomObject = new CustomObject(customObject, parent, objectStore);
		addToModel(newCustomObject);
		return newCustomObject;
	}

	public static CustomObject createCustomObject( Object customObject, IObjectStoreItem parent, ObjectStore objectStore, boolean saved ) {
		CustomObject newCustomObject = new CustomObject(customObject, parent, objectStore, saved);
		// TODO do something for unsaved documents
		addToModel(newCustomObject);
		return newCustomObject;
	}
	
	private static void addToModel(ObjectStoreItem objectStoreItem) {
		objectStoreItemsModel.add(objectStoreItem);
	}

//	public static ObjectStore createObjectStore(String name, String displayName, IObjectStoreItem parent) {
//		ObjectStore newObjectStore = new ObjectStore(name, displayName, parent );
//		addToModel(newObjectStore);
//		return newObjectStore;
//	}
}
