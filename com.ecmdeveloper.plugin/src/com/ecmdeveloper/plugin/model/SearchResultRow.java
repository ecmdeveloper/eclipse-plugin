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

package com.ecmdeveloper.plugin.model;

import java.util.HashMap;
import java.util.Iterator;

import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.query.RepositoryRow;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultRow {

	private HashMap<String, Object> values = new HashMap<String, Object>();
	
	public SearchResultRow(RepositoryRow row) {
		Properties properties = row.getProperties();
		Iterator<?> iterator = properties.iterator();
		while ( iterator.hasNext() ) {
			Property property = (Property) iterator.next();
			values.put(property.getPropertyName(), property.getObjectValue() );
		}
	}
	
	public Object getValue(String name) {
		return values.get(name);
	}
}
