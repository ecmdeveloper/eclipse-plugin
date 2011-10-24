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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.ecmdeveloper.plugin.core.model.ISearchResultRow;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultComparator extends ViewerComparator {

	private String sortValue;
	private boolean descending;
	
	public String getSortValue() {
		return sortValue;
	}

	public void setSortValue(String sortValue) {
		if (sortValue.equals( this.sortValue ) ) {
			descending = !descending;
		} else {
			this.sortValue= sortValue;
			descending = true;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		ISearchResultRow row1 = (ISearchResultRow) e1;
		ISearchResultRow row2 = (ISearchResultRow) e2;
		int returnCode = row1.compareTo(row2, sortValue );
		if ( descending ) {
			returnCode = -returnCode;
		}
		return returnCode;
	}
}
