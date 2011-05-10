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
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author ricardo.belfor
 *
 */
public class Query {

	public static final String TABLE_ADDED = "TableAdded";
	public static final String TABLE_REMOVED = "TableAdded";
	public static final String TOGGLE_INCLUDE_SUBCLASSES = "ToggleIncludeSubclasses";
	
	private ArrayList<IQueryTable> queryTables = new ArrayList<IQueryTable>();
	private QueryDiagram queryDiagram;
	private QueryElement mainQuery;
	private boolean includeSubclasses;
	private boolean distinct;
	private Integer maxCount;
	private String name;
	
	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	public Query() {
		queryDiagram = new QueryDiagram(this);
		queryDiagram.setRootDiagram(true);
		add( new MockQueryTable("Query Table 1") );
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Collection<IQueryTable> getQueryTables() {
		return queryTables;
	}
	
	public void add(IQueryTable queryTable) {
		queryTables.add(queryTable);
		listeners.firePropertyChange(TABLE_ADDED, queryTable, null);
	}
	
	public void remove(IQueryTable queryTable) {
		queryTables.remove(queryTable);
		listeners.firePropertyChange(TABLE_REMOVED, queryTable, null);
	}
	
	public void setIncludeSubclasses(boolean includeSubclasses) {
		this.includeSubclasses = includeSubclasses;
		listeners.firePropertyChange(TOGGLE_INCLUDE_SUBCLASSES, includeSubclasses,  null);
	}
	public boolean isIncludeSubclasses() {
		return includeSubclasses;
	}
	
	public boolean isDistinct() {
		return distinct;
	}
	
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}
	public Integer getMaxCount() {
		return maxCount;
	}
	public QueryDiagram getQueryDiagram() {
		return queryDiagram;
	}
	
	public QueryElement getMainQuery() {
		return mainQuery;
	}
	
	public void setMainQuery(QueryElement mainQuery) {
		this.mainQuery = mainQuery;
		queryDiagram.refresh();
	}

	public Collection<IQueryField> getQueryFields() {
		ArrayList<IQueryField> queryFields = new ArrayList<IQueryField>();
		if ( !queryTables.isEmpty() ) {
			for ( IQueryTable queryTable : queryTables ) {
				queryFields.addAll( queryTable.getQueryFields() );
			}
		}
		return queryFields;
	}

	public Collection<IQueryField> getSelectedQueryFields() {
		ArrayList<IQueryField> queryFields = new ArrayList<IQueryField>();
		if ( !queryTables.isEmpty() ) {
			for ( IQueryTable queryTable : queryTables ) {
				queryFields.addAll( queryTable.getSelectedQueryFields() );
			}
		}
		return queryFields;
	}
	
	public void setQueryFieldsSelection(boolean selected ) {
		for ( IQueryField queryField : getQueryFields() ) {
			queryField.setSelected(selected);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener l){
		listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l){
		listeners.removePropertyChangeListener(l);
	}
	
	public String toSQL() {
		
		StringBuffer sql = new StringBuffer();
		
		appendSelectPart(sql);
		appendFromPart(sql);
		appendWherePart(sql);
		
		return sql.toString();
	}

	private void appendSelectPart(StringBuffer sql) {
		sql.append( "\nSELECT " );
		if ( isDistinct() ) {
			sql.append("DISTINCT ");
		}
		
		if ( maxCount != null) {
			sql.append("TOP ");
			sql.append( maxCount );
			sql.append(" ");
		}

		String concat = "";
		Collection<IQueryField> selectedQueryFields = getSelectedQueryFields();
		if ( !selectedQueryFields.isEmpty() ) {
			for (IQueryField queryField : selectedQueryFields ) {
				sql.append(concat);
				sql.append( queryField.getName() );
				concat = ", ";
			}
			sql.append(" ");
		} else {
			sql.append("This ");
		}
	}

	private void appendFromPart(StringBuffer sql) {
		sql.append("\nFROM ");
		if ( getQueryTables().size() == 1) {
			sql.append("[");
			sql.append( getQueryTables().iterator().next() );
			sql.append( "] ");
		} else {
			// TODO something with joins
		}
	}
	private void appendWherePart(StringBuffer sql) {

		String whereClause = getQueryDiagram().toSQL();
		if ( whereClause != null && !whereClause.isEmpty() ) {
			sql.append( "\nWHERE " );
			sql.append( whereClause );
		}
	}

	@Override
	public String toString() {
		return toSQL();
	}

}
