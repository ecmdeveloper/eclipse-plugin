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
package com.ecmdeveloper.plugin.model.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.IFetchPropertiesTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.util.ObjectDumper;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.BatchItemHandle;
import com.filenet.api.core.Domain;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.RetrievingBatch;
import com.filenet.api.property.PropertyFilter;

/**
 * @author Ricardo Belfor
 *
 */
public class FetchPropertiesTask extends BaseTask implements IFetchPropertiesTask {

	private List<IObjectStoreItem> objectStoreItems = new ArrayList<IObjectStoreItem>();
	
	private String propertyNames[];
	
	public FetchPropertiesTask(IObjectStoreItem objectStoreItem, String[] propertyNames) {
		this.objectStoreItems.add( objectStoreItem );
		this.propertyNames = propertyNames;
	}

	public FetchPropertiesTask(Collection<IObjectStoreItem> objectStoreItems, String[] propertyNames) {
		this.objectStoreItems.addAll( objectStoreItems );
		this.propertyNames = propertyNames;
	}
	
	@Override
	protected Object execute() throws Exception {

		PropertyFilter pf = getPropertyFilter();
		RetrievingBatch retrievingBatch = getRetrievingBatch();
		
		for ( IObjectStoreItem objectStoreItem : objectStoreItems ) {
			IndependentlyPersistableObject objectStoreObject = ((ObjectStoreItem) objectStoreItem).getObjectStoreObject();
			retrievingBatch.add(objectStoreObject, pf);
		}

		retrievingBatch.retrieveBatch();

		checkForErrors(retrievingBatch);
		
		ObjectDumper od = new ObjectDumper();

		for ( IObjectStoreItem objectStoreItem : objectStoreItems ) {
			IndependentlyPersistableObject objectStoreObject = ((ObjectStoreItem) objectStoreItem).getObjectStoreObject();
			od.dump(objectStoreObject);
			System.out.println();
		}
		//objectStoreObject.refresh(propertyNames);
		//objectStoreObject.fetchProperties(propertyNames);

		return null;
	}

	private RetrievingBatch getRetrievingBatch() {
		Domain domain = getDomain();
		RetrievingBatch retrievingBatch = RetrievingBatch.createRetrievingBatchInstance(domain);
		return retrievingBatch;
	}

	private Domain getDomain() {
		com.filenet.api.core.ObjectStore internalObjectStore = getObjectStore();
		if ( !internalObjectStore.getProperties().isPropertyPresent( PropertyNames.DOMAIN ) ) {
			internalObjectStore.refresh( new String[] { PropertyNames.DOMAIN } );
		}
		Domain domain = internalObjectStore.get_Domain();
		return domain;
	}

	private com.filenet.api.core.ObjectStore getObjectStore() {
		IObjectStoreItem objectStoreItem = objectStoreItems.get(0);
		ObjectStore objectStore = (ObjectStore) objectStoreItem.getObjectStore();
		return (com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject();
	}

	private PropertyFilter getPropertyFilter() {
		PropertyFilter pf = new PropertyFilter();
		for ( String propertyName : propertyNames) {
			pf.addIncludeProperty(1, null, null, propertyName, null);
		}
		return pf;
	}

	private void checkForErrors(RetrievingBatch retrievingBatch) throws Exception {
		if ( retrievingBatch.hasExceptions() ) {
			for (Object object : retrievingBatch.getBatchItemHandles(null) ) {
				BatchItemHandle handle = (BatchItemHandle) object;
				if (handle.hasException() ) {
					throw new Exception("Fetching properties failed", handle.getException() );
				}
			}
		}
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		ObjectStoreItem objectStoreItem = (ObjectStoreItem) objectStoreItems.get(0);
		return objectStoreItem.getObjectStore().getConnection();
	}
}
