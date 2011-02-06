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

package com.ecmdeveloper.plugin.model.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoreItemFactory;
import com.filenet.api.collection.VersionableSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Factory;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetDocumentVersionsTask extends BaseTask {

	private Document document;
	private List<Document> versionsList;
	
	public GetDocumentVersionsTask(Document document) {
		this.document = document;
	}

	public List<Document> getVersions() {
		return versionsList;
	}

	@Override
	protected Object execute() throws Exception {

		ObjectStore objectStore = document.getObjectStore();
		com.filenet.api.core.ObjectStore internalObjectStore = (com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject();
		
		String versionSeriesId = document.getVersionSeriesId();
		VersionSeries versionSeries = Factory.VersionSeries.getInstance(internalObjectStore, new Id( versionSeriesId ) ); 
		
		fetchVersionProperties(versionSeries);
		
		VersionableSet versions = versionSeries.get_Versions();
		Iterator<?> versionsIterator = versions.iterator();

		versionsList = new ArrayList<Document>();
		
		while ( versionsIterator.hasNext() ) {
			
			com.filenet.api.core.Document version = (com.filenet.api.core.Document) versionsIterator.next();
			versionsList.add( ObjectStoreItemFactory.createDocument(version, null, objectStore ) );
		}		

		return null;
	}

	private void fetchVersionProperties(VersionSeries versionSeries) {

		PropertyFilter propertyFilter = new PropertyFilter();
		propertyFilter.addIncludeProperty( new FilterElement(null, null, null, PropertyNames.VERSIONS, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.ID, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.MAJOR_VERSION_NUMBER, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.MINOR_VERSION_NUMBER, null ) );

		versionSeries.fetchProperties(propertyFilter);
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return document.getObjectStore().getConnection();
	}
}
