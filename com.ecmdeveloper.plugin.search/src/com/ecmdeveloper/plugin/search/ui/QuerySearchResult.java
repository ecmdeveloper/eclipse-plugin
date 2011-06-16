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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.SearchResultEvent;

import com.ecmdeveloper.plugin.model.SearchResultRow;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class QuerySearchResult implements ISearchResult {

	private final SearchQuery searchQuery;
	private Collection<String> columnNames;
	private HashSet<ISearchResultListener> searchListeners = new HashSet<ISearchResultListener>();
	private final Query query;
	private ArrayList<SearchResultRow> searchResult;
	
	public QuerySearchResult(Query query, SearchQuery searchQuery) {
		this.query = query;
		this.searchQuery = searchQuery;
		columnNames = new ArrayList<String>();
		Collection<IQueryField> selectedQueryFields = query.getSelectedQueryFields();
		for (IQueryField queryField : selectedQueryFields ) {
			columnNames.add( queryField.getName() );
		}
	}

	@Override
	public void addListener(ISearchResultListener listener) {
		searchListeners.add(listener);		
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getLabel() {
		return searchQuery.getLabel();
	}

	@Override
	public ISearchQuery getQuery() {
		return searchQuery;
	}

	@Override
	public String getTooltip() {
		return query.getName();
	}

	@Override
	public void removeListener(ISearchResultListener listeners) {
		searchListeners.remove(listeners);
	}
	
	public Collection<SearchResultRow> getResult() {
		if ( searchResult != null ) {
			return searchResult;
		}
		return new ArrayList<SearchResultRow>();
	}
	
	public Collection<String> getColumnNames() {
		return columnNames;
	}

	public void setSearchResult(ArrayList<SearchResultRow> searchResult) {
		this.searchResult = searchResult;
		fireSearchResultChanged();
	}

	@SuppressWarnings("serial")
	private void fireSearchResultChanged() {
		for (ISearchResultListener listener: searchListeners) {
            listener.searchResultChanged(new SearchResultEvent(this) { });
		}
	}

	private void fireSearchResultAdded(SearchResultRow searchResultRow) {
		for (ISearchResultListener listener: searchListeners) {
            listener.searchResultChanged(new SearchResultAddEvent(this, searchResultRow) );
		}
	}

	public void addRow(SearchResultRow searchResultRow) {
		if (searchResult == null ) {
			searchResult = new ArrayList<SearchResultRow>();
		}
		searchResult.add(searchResultRow);
		fireSearchResultAdded(searchResultRow);
	}

	public void clear() {
		if ( searchResult != null ) {
			searchResult.clear();
			fireSearchResultChanged();
		}
	}

	public int size() {
		if ( searchResult != null ) {
			return searchResult.size();
		}
		return 0;
	}
}
