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

import java.util.List;

import com.ecmdeveloper.plugin.search.editor.QueryIcons;
import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;

/**
 * @author ricardo.belfor
 *
 */
public class InTest extends QueryComponent {

	static final long serialVersionUID = 1;
	
	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			InTest.class, "In Test", "Value In Test", QueryIcons.IN_TEST_ICON,
			QueryIcons.IN_TEST_ICON_LARGE) {

				@Override
				public boolean isValidFor(IQueryField queryField) {
					return queryField.isSearchable() &&  !QueryFieldType.isMultiValued( queryField.getType() );
				}};	
	
	private List<?> values;

	public InTest(Query query) {
		super(query);
	}

	public String toString() {
		return toString(false);
	}

	@Override
	public String toSQL() {
		return toString(true);
	}

	private String toString(boolean strict) {
		if (getField() != null ) {
			StringBuffer result = new StringBuffer();
			appendField(result, strict);
			result.append( " IN (");
			
			String concat = "";
			for ( Object value : values) {
				result.append( concat );
				result.append( QueryFieldValueFormatter.format(getField(), value) );
				concat = ",";
			}
			
			result.append( ")" );
			return result.toString();
		} else {
			return "";
		}
	}

	public void setValue(Object value) {
		Object oldField = this.values;
		this.values = (List<?>) value;
		firePropertyChange(PROPERTY_CHANGED, oldField, value);
	}

	public List<?> getValue() {
		return values;
	}

	@Override
	public QueryComponentType getType() {
		return QueryComponentType.IN_TEST;
	}
}
