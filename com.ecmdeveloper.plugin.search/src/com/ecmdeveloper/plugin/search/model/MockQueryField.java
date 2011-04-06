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

/**
 * @author ricardo.belfor
 *
 */
public class MockQueryField implements IQueryField {

	private final String name;
	private final QueryFieldType queryFieldType;
	private final IQueryTable queryTable;

	private SortType sortType = SortType.NONE;
	private int sortOrder = 0;
	private boolean selected;
	
	public MockQueryField(String name, QueryFieldType queryFieldType, IQueryTable queryTable) {
		this.name = name;
		this.queryFieldType = queryFieldType;
		this.queryTable = queryTable;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public QueryFieldType getType() {
		return queryFieldType;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public SortType getSortType() {
		return sortType;
	}

	@Override
	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	@Override
	public int getSortOrder() {
		return sortOrder;
	}

	@Override
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public IQueryTable getQueryTable() {
		return queryTable;
	}

	@Override
	public boolean isOrderable() {
		if ( queryFieldType.equals(QueryFieldType.BINARY ) ||
				queryFieldType.equals(QueryFieldType.BOOLEAN ) ||
				queryFieldType.equals(QueryFieldType.BINARY ) ||
				queryFieldType.equals(QueryFieldType.STRING ) ) {
			// TODO check for long strings
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isSupportsWildcards() {
		return QueryFieldType.STRING.equals( queryFieldType );
	}

	@Override
	public boolean isContainable() {
		// TODO check if field is folder, document or custom object 
		return QueryFieldType.OBJECT.equals(queryFieldType);
	}

	@Override
	public boolean isQueryField() {
		if ( queryFieldType.equals(QueryFieldType.BINARY ) ) {
			return false;
		}
		return true;
	}
}
