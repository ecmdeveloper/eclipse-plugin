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

package com.ecmdeveloper.plugin.search.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.SearchResultRow;

/**
 * @author ricardo.belfor
 *
 */
public abstract class SearchResultExport {

	private final Collection<SearchResultRow> searchResult;
	private final String filename;
	
	public SearchResultExport(Collection<SearchResultRow> searchResult, String filename ) {
		this.searchResult = searchResult;
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}

	public void export() throws IOException {
		
		if ( searchResult.isEmpty() ) {
			return;
		}
		
		FileWriter writer = new FileWriter( new File(filename) );
		startExport(writer);
		SearchResultRow firstRow = searchResult.iterator().next();
		ArrayList<String> valueNames = firstRow.getValueNames();
		writeHeaderRow( writer, valueNames );
		
		Iterator<SearchResultRow> iterator = searchResult.iterator();
		while (iterator.hasNext() ) {
			SearchResultRow searchResultRow = iterator.next();
			ArrayList<String> values = getValues(searchResultRow, valueNames);
			writeValuesRow( writer, values, valueNames );
		}
		
		finishExport(writer);
		
		writer.close();
	}

	protected abstract void startExport(FileWriter writer) throws IOException;

	protected abstract void writeHeaderRow(FileWriter writer, Collection<String> valueNames) throws IOException;

	protected abstract void writeValuesRow(FileWriter writer, ArrayList<String> values, ArrayList<String> valueNames) throws IOException;

	protected abstract void finishExport(FileWriter writer) throws IOException;

	private ArrayList<String> getValues(SearchResultRow searchResultRow,
			Collection<String> valueNames) {
		ArrayList<String> values = new ArrayList<String>();
		for ( String valueName : valueNames ) {
			if ( valueName.equals("This") && searchResultRow.isHasObjectValue() ) {
				IObjectStoreItem objectStoreItem = searchResultRow.getObjectValue();
				values.add( objectStoreItem.getDisplayName() );
			} else {
				Object value = searchResultRow.getValue(valueName);
				values.add( value != null ? value.toString() : "" );
			}
		}
		return values;
	}
}
