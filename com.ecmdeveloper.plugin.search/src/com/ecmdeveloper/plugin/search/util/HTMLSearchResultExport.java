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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.ecmdeveloper.plugin.model.SearchResultRow;

/**
 * @author ricardo.belfor
 *
 */
public class HTMLSearchResultExport extends SearchResultExport {

	private boolean writeHeader;
	
	public HTMLSearchResultExport(Collection<SearchResultRow> searchResult, String filename,
			boolean writeHeader) {
		super(searchResult, filename);
		this.writeHeader = writeHeader;
	}

	@Override
	protected void startExport(FileWriter writer) throws IOException {
		writer.write(
				"<html xmlns='http://www.w3.org/1999/xhtml'>" +
				"<head>\r\n" + 
				"</head>\r\n" + 
				"<body>\r\n" + 
				"<table border='1'>\r\n" );
	}

	@Override
	protected void writeHeaderRow(FileWriter writer, Collection<String> valueNames) throws IOException {
		if ( writeHeader) {
			writer.write("\t<tr>\r\n" );
			for ( String valueName : valueNames ) {
				writer.write("\t\t<th>");
				writer.write( valueName );
				writer.write("</th>\r\n");
			}
			writer.write("<tr>\r\n" );
		}
	}

	@Override
	protected void writeValuesRow(FileWriter writer, ArrayList<String> values, ArrayList<String> valueNames) throws IOException {
		writer.write("\t<tr>\r\n" );
		for ( String value : values ) {
			writer.write("\t\t<td>");
			writer.write( XMLSearchResultExport.getEscaped( value ) );
			writer.write("</td>\r\n");
		}
		writer.write("\t<td>\r\n" );
	}

	@Override
	protected void finishExport(FileWriter writer) throws IOException {
		writer.write( "</table>\r\n</body>\r\n</html>\r\n" );
	}
}
