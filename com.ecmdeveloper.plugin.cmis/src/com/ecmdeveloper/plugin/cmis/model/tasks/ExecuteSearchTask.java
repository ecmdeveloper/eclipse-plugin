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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.ArrayList;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;

import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.model.SearchResultRow;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.ISearchResultRow;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IExecuteSearchTask;

/**
 * @author ricardo.belfor
 *
 */
public class ExecuteSearchTask extends AbstractTask implements IExecuteSearchTask {

	private ObjectStore objectStore;
	private String query;
	private ArrayList<ISearchResultRow> searchResultList;
	private final Integer maxHits;
	
	public ExecuteSearchTask(String query, IObjectStore objectStore, Integer maxHits) {
		this.query = query;
		this.maxHits = maxHits;
		this.objectStore = (ObjectStore) objectStore;
		searchResultList = new ArrayList<ISearchResultRow>(); 
	}
	
	@Override
	public ArrayList<ISearchResultRow> getSearchResult() {
		return searchResultList;
	}

	@Override
	public Object call() throws Exception {
		
		Session session = objectStore.getSession();
		ItemIterable<QueryResult> queryResults = getQueryResult(session);
		
		if ( queryResults != null ) {
			for ( QueryResult queryResult : queryResults ) {
				SearchResultRow s = new SearchResultRow(queryResult, objectStore);
				searchResultList.add(s);
			}
		}
		
		return null;
	}

	private ItemIterable<QueryResult> getQueryResult(Session session) {
		ItemIterable<QueryResult> queryResults;
        if ( maxHits != null) {
        	OperationContext queryContext = new OperationContextImpl(null, false, false, false,
					IncludeRelationships.NONE, null, false, null, false, maxHits);
    		queryResults = session.query(query, true, queryContext );
        } else {
    		queryResults = session.query(query, true );
        }
		return queryResults;
	}
}
