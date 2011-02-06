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

package com.ecmdeveloper.plugin.search.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author ricardo.belfor
 *
 */
public class QueryTable implements IQueryTable {

	private ArrayList<IQueryField> fields;

	@Override
	public Collection<IQueryField> getQueryFields() {
		if ( fields == null) {
			fields = createMockFields();
		}
		return fields;
	}
	
	protected ArrayList<IQueryField> createMockFields() {
		ArrayList<IQueryField> fields = new ArrayList<IQueryField>();
		for (int i = 0; i < 10; i++) {
			QueryField queryField = new QueryField("Field " + i , QueryFieldType.STRING );
			fields.add(queryField);
		}
		return fields;
	}
}

