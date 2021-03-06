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

	private static final String NAME = "This";
	private final IQueryTable queryTable;
	private boolean selected;
	private String alias;

	public ThisQueryField(IQueryTable queryTable) {
		this.queryTable = queryTable;
		this.selected = true;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return NAME;
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
	public boolean isSearchable() {
		return queryTable.isContentEngineTable();
	}

	@Override
	public boolean isCBREnabled() {
		return false;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public boolean isSelectable() {
		return true;
	}
}
