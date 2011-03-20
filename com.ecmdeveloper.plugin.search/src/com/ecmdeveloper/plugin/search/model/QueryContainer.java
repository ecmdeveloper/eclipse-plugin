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

/**
 * 
 * @author ricardo.belfor
 *
 */
public abstract class QueryContainer extends QueryDiagram {

	public QueryContainer(Query query) {
		super(query);
	}

	private static final long serialVersionUID = 1L;

	public String toSQL() {
		
		if ( getChildren().size() == 0 ) {
			return "";
		}
		
		StringBuffer sql = new StringBuffer();
		String prefix = getOperationPrefix();
		if ( prefix != null ) {
			sql.append(prefix);
			sql.append(" ");
		}

		String concat = "(";
		for ( QueryElement queryElement : getChildren() ) {
			String childSql = queryElement.toSQL();
			if ( !childSql.isEmpty() ) {
				sql.append(concat);
				sql.append(childSql );
				concat = " " + getConcatOperation() + " ";
			}
		}
		
		if ( sql.length() != 0) {
			sql.append( ")" );
		}
		
		return sql.toString();
	}

	protected abstract String getOperationPrefix();
	protected abstract String getConcatOperation();
}
