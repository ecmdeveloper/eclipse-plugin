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
import com.ecmdeveloper.plugin.search.model.constants.FullTextQueryType;
import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;

/**
 * @author ricardo.belfor
 *
 */
public class FullTextQuery extends QueryComponent {

	static final long serialVersionUID = 1;

	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			FullTextQuery.class, "Full Text Query", "Full Text Query", QueryIcons.FULL_TEXT_ICON,
			QueryIcons.FULL_TEXT_ICON_LARGE) {

				@Override
				public boolean isValidFor(IQueryField queryField) {
					return queryField.isCBREnabled();
				}};
	
	private String text;
	private FullTextQueryType fullTextQueryType;
	private boolean allFields;
	
	public FullTextQuery(Query query) {
		super(query);
	}
				
	@Override
	public QueryComponentType getType() {
		return QueryComponentType.FULL_TEXT;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		String oldField = this.text;
		this.text = text;
		firePropertyChange(FIELD_CHANGED, oldField, text);
	}

	public FullTextQueryType getFullTextQueryType() {
		return fullTextQueryType;
	}

	public void setFullTextQueryType(FullTextQueryType fullTextQueryType) {
		FullTextQueryType oldField = this.fullTextQueryType;
		this.fullTextQueryType = fullTextQueryType;
		firePropertyChange(FIELD_CHANGED, oldField, fullTextQueryType );
	}

	public boolean isAllFields() {
		return allFields;
	}

	public void setAllFields(boolean allFields) {
		this.allFields = allFields;
		firePropertyChange(FIELD_CHANGED, null, allFields );
	}

	@Override
	public String toSQL() {
		return toSQL(true);
	}

	public String toSQL(boolean strict) {
		if ( fullTextQueryType != null ) {
			
			StringBuffer sql = new StringBuffer();
			sql.append( fullTextQueryType.name() );
			sql.append( "{" );

			if ( fullTextQueryType.equals( FullTextQueryType.CONTAINS ) && (getField() != null || allFields ) ) {
				if ( allFields ) {
					sql.append("*");
				} else {
					if (strict) sql.append("[");
					sql.append( getField().getName() );
					if (strict) sql.append("]");
				}
			}  else if ( fullTextQueryType.equals( FullTextQueryType.FREETEXT ) ) {
				sql.append("*");
			}

			sql.append(",'");
			sql.append( text );
			sql.append( "')" );
			return sql.toString();
		}
		return "";
	}

	@Override
	public String toString() {
		return toSQL(false);
	}
}
