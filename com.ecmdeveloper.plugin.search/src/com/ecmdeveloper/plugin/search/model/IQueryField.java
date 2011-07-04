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
public interface IQueryField {
	public String getName();
	public String getDisplayName();
	public QueryFieldType getType();
	public void setSortType(SortType sortType);
	public SortType getSortType();
	public void setSortOrder(int sortOrder);
	public int getSortOrder();
	public boolean isSelectable();
	public boolean isSelected();
	public void setSelected(boolean selected);
	public void setAlias(String alias);
	public String getAlias();
	public IQueryTable getQueryTable();
	public boolean isOrderable();
	public boolean isSupportsWildcards();
	public boolean isContainable();
	public boolean isSearchable();
	public boolean isCBREnabled();
}
