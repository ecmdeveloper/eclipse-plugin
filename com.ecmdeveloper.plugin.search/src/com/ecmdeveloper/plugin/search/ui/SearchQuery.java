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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.SearchResultRow;
import com.ecmdeveloper.plugin.model.tasks.ExecuteSearchTask;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class SearchQuery implements ISearchQuery {

	private static final String LABEL_FORMAT_EMPTY = "{0} - {1}";
	private static final String LABEL_FORMAT = "{0} - {1} - {2} matches";
	private static final String TASK_NAME = "Executing Search";
	QuerySearchResult searchResult;
	private final Query query;
	private String label;
	private String queryDate;
	
	public SearchQuery(Query query) {
		this.query = query;
		searchResult = new QuerySearchResult(query,this);
		queryDate = (new Date()).toString();
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
		if (searchResult.size() == 0 ) {
			return label = MessageFormat.format(LABEL_FORMAT_EMPTY, query.getName(), queryDate );
		} else {
			return label = MessageFormat.format(LABEL_FORMAT, query.getName(), queryDate, searchResult.size());
		}
	}

	@Override
	public ISearchResult getSearchResult() {
		return searchResult;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
				
		try
		{
			searchResult.clear();
			
			monitor.beginTask(TASK_NAME, IProgressMonitor.UNKNOWN );
			monitor.subTask( getLabel() );
			ArrayList<SearchResultRow> searchResultRows = executeSearch();
			monitor.done();

			IStatus status = addRowsToResult(searchResultRows, monitor);
			monitor.done();
			return status;
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(TASK_NAME, e.getLocalizedMessage(), e );
			return Status.CANCEL_STATUS;
		}
	}

	private IStatus addRowsToResult(ArrayList<SearchResultRow> searchResultRows,
			IProgressMonitor monitor) {

		monitor.beginTask("Fetching Result", searchResultRows.size() );

		for (SearchResultRow searchResultRow : searchResultRows ) {
			try {
				addRowToResult(searchResultRow, monitor);
				monitor.worked(1);
			} catch (ExecutionException e) {
				PluginMessage.openErrorFromThread(TASK_NAME, e.getLocalizedMessage(), e );
				return Status.CANCEL_STATUS;
			}
			
			if ( monitor.isCanceled() ) {
				return Status.CANCEL_STATUS;
			}
		}
		return Status.OK_STATUS;
	}

	private void addRowToResult(SearchResultRow searchResultRow, IProgressMonitor monitor) throws ExecutionException {
		if ( searchResultRow.isHasObjectValue() ) {
			IObjectStoreItem objectStoreItem = searchResultRow.loadObjectValue();
			monitor.subTask( objectStoreItem.getDisplayName() );
		}
		searchResult.addRow(searchResultRow);
	}

	private ArrayList<SearchResultRow> executeSearch() throws Exception {
		ObjectStoresManager objectStoresManager = ObjectStoresManager.getManager();
		ObjectStore objectStore = getObjectStore(objectStoresManager);
		if ( !objectStore.isConnected() ) {
			throw new Exception( "Object Store '" + objectStore.getDisplayName() + "' is not connected." );
		}
		ExecuteSearchTask task = new ExecuteSearchTask(query.toSQL(), objectStore);
		objectStoresManager.executeTaskSync(task);
		
		ArrayList<SearchResultRow> searchResultRows = task.getSearchResult();
		return searchResultRows;
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
