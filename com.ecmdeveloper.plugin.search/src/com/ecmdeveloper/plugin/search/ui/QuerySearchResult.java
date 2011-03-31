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

/**
 * @author ricardo.belfor
 *
 */
public class QuerySearchResult implements ISearchResult {

	private final SearchQuery searchQuery;
	private Collection<String> result;
	private Collection<String> columnNames;
	private HashSet<ISearchResultListener> searchListeners = new HashSet<ISearchResultListener>();
	
	public QuerySearchResult(SearchQuery searchQuery) {
		this.searchQuery = searchQuery;
		result = new ArrayList<String>();
		result.add("Hello");
		result.add("World");
		
		columnNames = new ArrayList<String>();
		for ( int i = 0; i < 4; ++i) {
			columnNames.add("Column " + i );
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
		return "My Query";
	}

	@Override
	public ISearchQuery getQuery() {
		return searchQuery;
	}

	@Override
	public String getTooltip() {
		return "My Query Tooltip";
	}

	@Override
	public void removeListener(ISearchResultListener listeners) {
		searchListeners.remove(listeners);
	}
	
	@SuppressWarnings("serial")
	public void doit() {
		for (ISearchResultListener listener: searchListeners) {
            listener.searchResultChanged(new SearchResultEvent(this) { });
		}
	}
	
	public Collection<String> getResult() {
		return result;
	}
	
	public Collection<String> getColumnNames() {
		return columnNames;
	}
}
