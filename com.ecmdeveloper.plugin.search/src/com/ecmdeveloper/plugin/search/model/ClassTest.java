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

import com.ecmdeveloper.plugin.search.editor.QueryIcons;
import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;

/**
 * @author ricardo.belfor
 *
 */
public class ClassTest extends QueryComponent  {

	private static final long serialVersionUID = 1L;
	private String className;
	
	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			ClassTest.class, "Is Class Test", "Query Field Is Class Test",
			QueryIcons.CLASS_TEST_ICON, QueryIcons.CLASS_TEST_ICON_LARGE){

				@Override
				public boolean isValidFor(IQueryField queryField) {
					return queryField instanceof ThisQueryField;
				}};	
	
	public ClassTest(Query query) {
		super(query);
	}

	@Override
	public QueryComponentType getType() {
		return QueryComponentType.CLASS_TEST;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		String oldValue = this.className;
		this.className = className;
		firePropertyChange(FIELD_CHANGED, oldValue, className );
	}

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toSQL() {
		return toString(true);
	}
	
	private String toString(boolean strict) {
		if ( getField() != null && className != null ) {
			
			StringBuffer result = new StringBuffer();
			result.append( " ISCLASS(");

			if ( strict ) result.append('[');
			if ( getField() instanceof ThisQueryField ) {
				result.append( getField().getQueryTable().getName() );
			} else {
				result.append( getField().getName() );
			}
			if ( strict ) result.append(']');

			result.append(',');
			if ( strict ) result.append('[');
			result.append(className);
			if ( strict ) result.append(']');
			result.append( ")");
			return result.toString();
		} else {
			return "";
		}
	}

}
