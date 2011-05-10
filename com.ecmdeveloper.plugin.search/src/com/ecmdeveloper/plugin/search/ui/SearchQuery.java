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

package com.ecmdeveloper.plugin.search.ui;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.ExecuteSearchTask;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class SearchQuery implements ISearchQuery {

	QuerySearchResult searchResult;
	private final Query query;

	public SearchQuery(Query query) {
		this.query = query;
		searchResult = new QuerySearchResult(query,this);
	}
	
	@Override
	public boolean canRerun() {
		return true;
	}

	@Override
	public boolean canRunInBackground() {
		return true;
	}

	@Override
	public String getLabel() {
		return query.toString();
	}

	@Override
	public ISearchResult getSearchResult() {
		return searchResult;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		monitor.beginTask("Executing Search", IProgressMonitor.UNKNOWN );
				
		ObjectStoresManager objectStoresManager = ObjectStoresManager.getManager();
		ObjectStore objectStore = getObjectStore(objectStoresManager);
		ExecuteSearchTask task = new ExecuteSearchTask(query.toSQL(), objectStore);
		try {
			objectStoresManager.executeTaskSync(task);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		searchResult.setSearchResult(task.getSearchResult());
		monitor.done();
		return Status.OK_STATUS;
	}

	private ObjectStore getObjectStore(ObjectStoresManager objectStoresManager) {
		Collection<IQueryTable> queryTables = query.getQueryTables();
		IQueryTable queryTable = queryTables.iterator().next();
		String connectionName = queryTable.getConnectionName();
		String objectStoreName = queryTable.getObjectStoreName();
		ObjectStore objectStore = objectStoresManager.getObjectStore(connectionName, objectStoreName);
		return objectStore;
	}
}
