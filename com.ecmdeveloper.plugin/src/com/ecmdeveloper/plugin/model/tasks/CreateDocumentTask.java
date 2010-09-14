package com.ecmdeveloper.plugin.model.tasks;

import java.util.Map;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.filenet.api.core.Factory;

public class CreateDocumentTask extends CreateTask {

	private Document newDocument;

	public CreateDocumentTask(Folder parent, String className, Map<String,Object> propertiesMap) {
		super(parent, className, propertiesMap);
	}

	public Document getNewDocument() {
		return newDocument;
	}

	@Override
	public ObjectStoreItem getNewObjectStoreItem() {
		return getNewDocument();
	}

	@Override
	public Object call() throws Exception {

		createNewDocument();
		setProperties(newDocument);
		newDocument.save();
		newDocument.refresh();
		fileInParent();
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}


	private void createNewDocument() {
		com.filenet.api.core.Document internalDocument = createInternalDocument();
		Folder parent = getParent();
		newDocument = new Document(internalDocument, parent, parent.getObjectStore(), false );
	}

	private com.filenet.api.core.Document createInternalDocument() {
		com.filenet.api.core.ObjectStore internalObjectStore = getInternalObjectStore();
		com.filenet.api.core.Document internalDocument = Factory.Document.createInstance(internalObjectStore, getClassName() );
		return internalDocument;
	}
}