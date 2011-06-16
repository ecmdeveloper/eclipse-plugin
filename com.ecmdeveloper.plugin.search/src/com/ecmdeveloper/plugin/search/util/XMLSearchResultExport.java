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
public class XMLSearchResultExport extends SearchResultExport {

	private final String rowsTag;
	private final String rowTag;

	public XMLSearchResultExport(Collection<SearchResultRow> searchResult, String filename, String rowsTag, String rowTag) {
		super(searchResult, filename);
		this.rowsTag = rowsTag;
		this.rowTag = rowTag;
	}

	@Override
	protected void startExport(FileWriter writer) throws IOException {
		StringBuffer header = new StringBuffer();
		header.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" );
		appendRowsTag(header, true );
		writer.write( header.toString() );
	}

	private void appendRowsTag(StringBuffer header, boolean opening) {
		header.append( opening ? "<" : "</" );
		header.append(rowsTag);
		header.append( ">\r\n");
	}

	@Override
	protected void writeHeaderRow(FileWriter writer, Collection<String> valueNames)
			throws IOException {
	}

	@Override
	protected void writeValuesRow(FileWriter writer, ArrayList<String> values, ArrayList<String> valueNames) throws IOException {
		StringBuffer row = new StringBuffer();
		appendRowTag(row,true);
		
		for ( int i = 0; i < values.size(); ++i) {
			appendValueNameTag(row, valueNames.get(i), true);
			row.append( getEscaped( values.get(i)) );
			appendValueNameTag(row, valueNames.get(i), false);
		}
		appendRowTag(row,false);
		
		writer.write( row.toString() );
	}

	private void appendRowTag(StringBuffer row, boolean opening) {
		row.append( opening ? "\t<" : "\t</" );
		row.append( rowTag );
		row.append( ">\r\n");
	}

	private void appendValueNameTag(StringBuffer row, String valueName, boolean opening) {
		row.append( opening ? "\t\t<" : "</" );
		row.append( valueName );
		row.append( opening? ">" : ">\r\n" );
	}

	@Override
	protected void finishExport(FileWriter writer) throws IOException {
		writer.write("</" + rowsTag + ">");
	}

	public static String getEscaped(String value) {
		StringBuffer result = new StringBuffer(value.length() + 10);
		for (int i = 0; i < value.length(); ++i) {
			appendEscapedChar(result, value.charAt(i));
		}
		return result.toString();
	}

	private static void appendEscapedChar(StringBuffer buffer, char c) {
		String replacement = getReplacement(c);
		if (replacement != null) {
			buffer.append('&');
			buffer.append(replacement);
			buffer.append(';');
		} else {
			buffer.append(c);
		}
	}

	private static String getReplacement(char c) {
		switch (c) {
			case '<' :
				return "lt"; //$NON-NLS-1$
			case '>' :
				return "gt"; //$NON-NLS-1$
			case '"' :
				return "quot"; //$NON-NLS-1$
			case '\'' :
				return "apos"; //$NON-NLS-1$
			case '&' :
				return "amp"; //$NON-NLS-1$
			case '\r':
				return "#x0D"; //$NON-NLS-1$
			case '\n':
				return "#x0A"; //$NON-NLS-1$
			case '\u0009':
				return "#x09"; //$NON-NLS-1$
		}
		return null;
	}
}
