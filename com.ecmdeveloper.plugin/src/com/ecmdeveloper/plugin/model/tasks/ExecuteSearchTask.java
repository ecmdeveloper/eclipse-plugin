/**
 * Copyright 2011, Ricardo Belfor
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

import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.ISearchResultRow;
import com.ecmdeveloper.plugin.core.model.tasks.IExecuteSearchTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.SearchResultRow;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;

/**
 * @author ricardo.belfor
 *
 */
public class ExecuteSearchTask extends BaseTask implements IExecuteSearchTask {

	private ObjectStore objectStore;
	private String query;
	private ArrayList<ISearchResultRow> searchResultList;
	
	public ExecuteSearchTask(String query, IObjectStore objectStore2) {
		this.query = query;
		this.objectStore = (ObjectStore) objectStore2;
		searchResultList = new ArrayList<ISearchResultRow>(); 
	}
	
	@Override
	public ArrayList<ISearchResultRow> getSearchResult() {
		return searchResultList;
	}

	@Override
	protected Object execute() throws Exception {
		
		RepositoryRowSet searchResult = getSearchResult(query);
		Iterator<?> iterator = searchResult.iterator();
		while (iterator.hasNext()) {
			RepositoryRow object = (RepositoryRow) iterator.next();
			SearchResultRow s = new SearchResultRow(object, objectStore);
			searchResultList.add(s);
		}
		
		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}

	private RepositoryRowSet getSearchResult(String query) {
		com.filenet.api.core.ObjectStore internalObjectStore = (com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject();
		PropertyFilter pf = new PropertyFilter();
		pf.addIncludeProperty(0, null, null, PropertyNames.ID, null );
		SearchScope scope = new SearchScope( internalObjectStore );
		RepositoryRowSet fetchRows = scope.fetchRows(new SearchSQL( query ), null, pf, null);
		return fetchRows;
	}
}
