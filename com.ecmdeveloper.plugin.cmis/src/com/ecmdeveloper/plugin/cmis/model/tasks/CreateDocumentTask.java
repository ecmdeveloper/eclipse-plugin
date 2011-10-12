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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.Map;

import org.apache.chemistry.opencmis.commons.PropertyIds;

import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.ICreateDocumentTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;

/**
 * @author ricardo.belfor
 *
 */
public class CreateDocumentTask extends AbstractTask implements ICreateDocumentTask {

	private Document newDocument;
	private final ObjectStoreItem parent;
	private final String className;
	private final Map<String, Object> propertiesMap;

	public CreateDocumentTask(ObjectStoreItem parent, String className, Map<String,Object> propertiesMap) {
		this.parent = parent;
		this.className = className;
		this.propertiesMap = propertiesMap;
	}


	@Override
	public IObjectStoreItem getNewObjectStoreItem() {
		return getNewDocument();
	}

	private IObjectStoreItem getNewDocument() {
		return newDocument;
	}


	@Override
	public Object call() throws Exception {

		// TODO fix parent
		newDocument = ObjectStoreItemFactory.createDocument(null, getParent(), (ObjectStore) getParent().getObjectStore(), false );
		for ( String propertyName : propertiesMap.keySet() ) {
			newDocument.setValue(propertyName, propertiesMap.get(propertyName) );
		}
		newDocument.setValue(PropertyIds.OBJECT_TYPE_ID, className );
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}

	@Override
	public IObjectStoreItem getParent() {
		return parent;
	}

	@Override
	public boolean isAddedToParent() {
		// TODO fix this
		return true;
	}
}
