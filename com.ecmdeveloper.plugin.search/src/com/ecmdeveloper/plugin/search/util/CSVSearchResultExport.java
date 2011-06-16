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

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import au.com.bytecode.opencsv.CSVWriter;

import com.ecmdeveloper.plugin.model.SearchResultRow;

/**
 * @author ricardo.belfor
 *
 */
public class CSVSearchResultExport extends SearchResultExport {

	private CSVWriter csvWriter;
	private final char separator;
	private boolean writeHeader;
	
	public CSVSearchResultExport(Collection<SearchResultRow> searchResult, String filename,
			boolean writeHeader, char separator ) {
		super(searchResult, filename );
		this.separator = separator;
		this.writeHeader = writeHeader;
	}

	@Override
	protected void startExport(FileWriter writer) {
		csvWriter = new CSVWriter(writer, separator );
	}

	@Override
	protected void writeHeaderRow(FileWriter writer, Collection<String> valueNames) {
		if ( writeHeader ) {
			csvWriter.writeNext( valueNames.toArray( new String[valueNames.size()] ) );
		}
	}

	@Override
	protected void writeValuesRow(FileWriter writer, ArrayList<String> values, ArrayList<String> valueNames) {
		csvWriter.writeNext( values.toArray( new String[values.size()] ) );
	}

	@Override
	protected void finishExport(FileWriter writer) {
	}
}
