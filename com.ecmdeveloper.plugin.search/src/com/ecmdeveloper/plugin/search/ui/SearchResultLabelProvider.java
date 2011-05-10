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

import java.util.HashMap;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.model.SearchResultRow;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultLabelProvider extends LabelProvider implements ITableLabelProvider {

	private HashMap<Integer,String> indexToNameMap = new HashMap<Integer, String>();
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public void connectIndexToName(Integer index, String name) {
		indexToNameMap.put(index, name);
	}
	
	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof SearchResultRow) {
			SearchResultRow searchResultRow = (SearchResultRow)element;
			String name = indexToNameMap.get(Integer.valueOf(columnIndex) );
			if ( name != null ) {
				Object value = searchResultRow.getValue(name);
				if ( value != null ) {
					return value.toString();
				}
			} 
		}
		return "";
	}
}
