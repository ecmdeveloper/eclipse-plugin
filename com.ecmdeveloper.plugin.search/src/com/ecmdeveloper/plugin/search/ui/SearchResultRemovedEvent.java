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
import java.util.List;

import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.SearchResultEvent;

import com.ecmdeveloper.plugin.core.model.ISearchResultRow;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultRemovedEvent extends SearchResultEvent {

	private static final long serialVersionUID = -204924580196767581L;
	private List<ISearchResultRow> searchResultRows;
	
	protected SearchResultRemovedEvent(ISearchResult searchResult, List<ISearchResultRow> searchResultRows ) {
		super(searchResult);
		this.searchResultRows = new ArrayList<ISearchResultRow>();
		this.searchResultRows.addAll(searchResultRows);
	}

	public List<ISearchResultRow> getSearchResultRows() {
		return searchResultRows;
	}
}
