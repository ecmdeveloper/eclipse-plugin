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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.ecmdeveloper.plugin.classes.model.PropertyDescription;

/**
 * @author ricardo.belfor
 *
 */
public class QueryField2 implements IQueryField {

	public static final String PROPERTY_VALUE = "property_value";
	
	private final String name;
	private final String displayName;
	private final QueryFieldType queryFieldType;
	private final IQueryTable queryTable;
	private final boolean orderable;
	private final boolean containable;
	private final boolean cbrEnabled;
	
	private SortType sortType = SortType.NONE;
	private int sortOrder = 0;
	private boolean selected;
	private String alias;

	transient private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	public QueryField2(PropertyDescription propertyDescription, IQueryTable queryTable) {
		this.queryTable = queryTable;
		this.name = propertyDescription.getName();
		this.displayName = propertyDescription.getDisplayName();
		this.queryFieldType = getQueryFieldType(propertyDescription);
		this.orderable = propertyDescription.isOrderable();
		this.containable = propertyDescription.isContainable();
		this.cbrEnabled = propertyDescription.isCBREnabled();
		
	}
	
	public QueryField2(String name, String displayName, QueryFieldType queryFieldType,
			boolean orderable, boolean containable, boolean cbrEnabled, IQueryTable queryTable) {
		this.name = name;
		this.displayName = displayName;
		this.queryFieldType = queryFieldType;
		this.orderable = orderable;
		this.containable = containable;
		this.cbrEnabled = cbrEnabled;
		this.queryTable = queryTable;
	}

	private QueryFieldType getQueryFieldType(PropertyDescription propertyDescription) {
		switch ( propertyDescription.getPropertyType() ) {
		case BINARY: return QueryFieldType.BINARY;
		case BOOLEAN: return QueryFieldType.BOOLEAN;
		case DATE: return QueryFieldType.DATE;
		case DOUBLE: return QueryFieldType.DOUBLE;
		case GUID: return QueryFieldType.GUID;
		case LONG: return QueryFieldType.LONG;
		case OBJECT: return QueryFieldType.OBJECT;
		case STRING: return QueryFieldType.STRING;
		case UNKNOWN: return QueryFieldType.NONE;
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
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
		SortType oldValue = this.sortType;
		this.sortType = sortType;
		listeners.firePropertyChange(PROPERTY_VALUE, oldValue,  sortType);
	}

	@Override
	public int getSortOrder() {
		return sortOrder;
	}

	@Override
	public void setSortOrder(int sortOrder) {
		int oldValue = this.sortOrder;
		this.sortOrder = sortOrder;
		listeners.firePropertyChange(PROPERTY_VALUE, oldValue,  sortOrder);
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		boolean oldValue = this.selected;
		this.selected = selected;
		listeners.firePropertyChange(PROPERTY_VALUE, oldValue,  selected);
	}

	public IQueryTable getQueryTable() {
		return queryTable;
	}

	@Override
	public boolean isOrderable() {
		return orderable;
	}

	@Override
	public boolean isSupportsWildcards() {
		return QueryFieldType.STRING.equals( queryFieldType );
	}

	@Override
	public boolean isContainable() {
		return containable;
	}

	@Override
	public boolean isQueryField() {
		if ( queryFieldType.equals(QueryFieldType.BINARY ) ) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isCBREnabled() {
		return cbrEnabled;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		String oldValue = this.alias;
		this.alias = alias;
		listeners.firePropertyChange(PROPERTY_VALUE, oldValue,  alias);
	}

	public void addPropertyChangeListener(PropertyChangeListener l){
		listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l){
		listeners.removePropertyChangeListener(l);
	}
}
