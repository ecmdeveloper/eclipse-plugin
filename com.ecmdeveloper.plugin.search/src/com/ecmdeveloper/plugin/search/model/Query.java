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
import java.util.Collections;
import java.util.Comparator;

/**
 * @author ricardo.belfor
 *
 */
public class Query {

	public static final String TABLE_ADDED = "TableAdded";
	public static final String TABLE_REMOVED = "TableAdded";
	public static final String TOGGLE_INCLUDE_SUBCLASSES = "ToggleIncludeSubclasses";
	private static final String TOGGLE_DISTINCT = "ToggleDistinct";
	private static final String MAX_COUNT = "MaxCount";
	
	private ArrayList<IQueryTable> queryTables = new ArrayList<IQueryTable>();
	private QueryDiagram queryDiagram;
	private QueryElement mainQuery;
	private boolean includeSubclasses = true;
	private boolean distinct;
	private Integer maxCount;
	private String name;
	
	transient protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	public Query() {
		queryDiagram = new QueryDiagram(this);
		queryDiagram.setRootDiagram(true);
//		add( new MockQueryTable("Query Table 1") );
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
		listeners.firePropertyChange(TOGGLE_DISTINCT, distinct,  null);
	}
	
	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
		listeners.firePropertyChange(MAX_COUNT, maxCount,  null);
	}
	public Integer getMaxCount() {
		return maxCount;
	}
	
	public boolean isCRBEnabled() {
		for (IQueryTable queryTable : getQueryTables() ) {
			if ( queryTable.isCBREnabled() ) {
				return true;
			}
		}
		return false;
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
		appendOrderByPart(sql);
		
		return sql.toString();
	}

	private void appendSelectPart(StringBuffer sql) {
		sql.append( "SELECT " );
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
				appendFieldPart(sql, queryField);
				concat = "\r\n\t, ";
			}
			sql.append(" ");
		} else {
			sql.append("This ");
		}
	}

	private void appendFieldPart(StringBuffer sql, IQueryField queryField) {
		sql.append( queryField.getName() );
		if ( queryField.getAlias() != null ) {
			sql.append(' ');
			sql.append("AS ");
			sql.append( queryField.getAlias() );
		}
	}

	private void appendFromPart(StringBuffer sql) {
		sql.append("\nFROM ");
		if ( getQueryTables().size() == 1) {
			IQueryTable queryTable = getQueryTables().iterator().next();
			appendTablePart(sql, queryTable);
			if ( !includeSubclasses ) {
				sql.append( "WITH EXCLUDESUBCLASSES ");
			}
		} else {
			// TODO something with joins
		}
	}

	private void appendTablePart(StringBuffer sql, IQueryTable queryTable) {
		sql.append("[");
		sql.append( queryTable );
		sql.append( "] ");
		if ( queryTable.getAlias() != null ) {
			sql.append("AS ");
			sql.append(queryTable.getAlias() );
			sql.append(' ');
		}
	}
	private void appendWherePart(StringBuffer sql) {

		String whereClause = getQueryDiagram().toSQL();
		if ( whereClause != null && !whereClause.isEmpty() ) {
			sql.append( "\nWHERE " );
			sql.append( whereClause );
		}
	}

	private void appendOrderByPart(StringBuffer sql) {
		ArrayList<IQueryField> orderByFields = getOrderByFields();

		String concat = "\nORDER BY ";
		for ( IQueryField queryField : orderByFields ) {
			sql.append(concat);
			sql.append( "[" );
			sql.append( queryField.getName() );
			sql.append( "] " );
			sql.append( queryField.getSortType().name() );
			concat = ",";
		}
	}

	private ArrayList<IQueryField> getOrderByFields() {
		ArrayList<IQueryField> orderByFields = new ArrayList<IQueryField>();
		
		for ( IQueryField queryField : getSelectedQueryFields() ) {
			if ( !SortType.NONE.equals( queryField.getSortType() ) ) {
				orderByFields.add(queryField);
			}
		}

		Collections.sort(orderByFields, new Comparator<IQueryField>() {

			@Override
			public int compare(IQueryField arg0, IQueryField arg1) {
				return Integer.valueOf(arg0.getSortOrder()).compareTo(
						Integer.valueOf(arg1.getSortOrder()));
			}
		} );
		return orderByFields;
	}

	@Override
	public String toString() {
		return toSQL();
	}

	public IQueryField getField(String fieldName, String tableName) {
		Collection<IQueryTable> queryTables2 = getQueryTables();
		return getQueryField(fieldName, tableName, queryTables2);
	}

	private IQueryField getQueryField(String fieldName, String tableName, Collection<IQueryTable> queryTables2) {
		
		for ( IQueryTable queryTable : queryTables2 ) {
			if ( queryTable.getName().equals(tableName) ) {
				IQueryField queryField = queryTable.getQueryField( fieldName) ;
				if ( queryField != null ) {
					return queryField;
				}
			}
			
			IQueryField queryField = getQueryField(fieldName, tableName, queryTable.getChildQueryTables() );
			if ( queryField != null ) {
				return queryField;
			}
		}
		return null;
	}

	public IQueryTable getTable(String tableName) {
		return getTable(tableName, getQueryTables() );
	}
	
	private IQueryTable getTable(String tableName, Collection<IQueryTable> queryTables2 ) {
		for ( IQueryTable childQueryTable : queryTables2 ) {
			if ( childQueryTable.getName().equals(tableName) ) {
				return childQueryTable;
			}
			
			IQueryTable queryTable = getTable(tableName, childQueryTable.getChildQueryTables() );
			if ( queryTable != null ) {
				return queryTable;
			}
		}
		return null;
		
	}

}
