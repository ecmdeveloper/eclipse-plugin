package com.ecmdeveloper.plugin.cmis.model;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.ObjectStoreItemsModel;
import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.cmis.model.Folder;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;

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

	private static void addToModel(ObjectStoreItem objectStoreItem) {
		objectStoreItemsModel.add(objectStoreItem);
	}
}
