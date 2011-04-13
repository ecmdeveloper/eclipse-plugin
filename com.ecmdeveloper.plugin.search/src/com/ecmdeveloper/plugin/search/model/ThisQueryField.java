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
public class ThisQueryField implements IQueryField {

	private final IQueryTable queryTable;
	private boolean selected;

	public ThisQueryField(IQueryTable queryTable) {
		this.queryTable = queryTable;
		this.selected = true;
	}

	@Override
	public String getName() {
		return "This";
	}

	@Override
	public int getSortOrder() {
		return 0;
	}

	@Override
	public SortType getSortType() {
		return SortType.NONE;
	}

	@Override
	public QueryFieldType getType() {
		return QueryFieldType.OBJECT;
	}

	@Override
	public void setSortOrder(int sortOrder) {
	}

	@Override
	public void setSortType(SortType sortType) {
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public IQueryTable getQueryTable() {
		return queryTable;
	}

	@Override
	public boolean isOrderable() {
		return false;
	}

	@Override
	public boolean isSupportsWildcards() {
		return false;
	}

	@Override
	public boolean isContainable() {
		return true;
	}

	@Override
	public boolean isQueryField() {
		return true;
	}
}
