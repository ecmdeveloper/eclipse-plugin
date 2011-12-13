/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.search.model;

import com.ecmdeveloper.plugin.search.editor.QueryIcons;
import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;

/**
 * 
 * @author ricardo.belfor
 * 
 */
public class MultiValueInTest extends QueryComponent {

	static final long serialVersionUID = 1;
	
	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			MultiValueInTest.class, "Multi Value In Test", "Query Field Multi Value In Test", QueryIcons.MULTI_VALUE_IN_TEST_ICON,
			QueryIcons.MULTI_VALUE_IN_TEST_ICON_LARGE ) {

				@Override
				public boolean isValidFor(IQueryField queryField) {
					if ( !queryField.isSearchable() ) {
						return false;
					}
					
					return QueryFieldType.isMultiValued( queryField.getType() );
				}};	
	
	private Object value;

	public MultiValueInTest(Query query) {
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
			result.append( QueryFieldValueFormatter.format(getField(), value) );
			result.append( getQuery().isContentEngineQuery() ? " IN " : " = ANY ");
			appendField(result, strict);
			result.append( " " );
			return result.toString();
		} else {
			return "";
		}
	}

	public void setValue(Object value) {
		Object oldField = this.value;
		this.value = value;
		firePropertyChange(PROPERTY_CHANGED, oldField, value);
	}

	public Object getValue() {
		return value;
	}

	@Override
	public QueryComponentType getType() {
		return QueryComponentType.MULTI_VALUE_IN_TEST;
	}
}
