/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.folderview.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author ricardo.belfor
 *
 */
public class MockItem {

	private Map<String,Object> values = new HashMap<String, Object>();

	public void putValue(String name, Object value) {
		values.put(name, value);
	}
	
	public Object getValue(String name) {
		return values.get(name);
	}
	
	public Set<String> getColumnNames() {
		return values.keySet();
	}

	@Override
	public String toString() {
		return getValue("First Name") + " " + getValue("Last Name");
	}
}
