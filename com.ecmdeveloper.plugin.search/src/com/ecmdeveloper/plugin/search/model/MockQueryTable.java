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
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author ricardo.belfor
 *
 */
public class MockQueryTable implements IQueryTable {

	private ArrayList<IQueryField> fields;
	private final String name;
	private Collection<IQueryTable> childQueryTables;
	private String alias;
	
	public MockQueryTable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public Collection<IQueryField> getQueryFields() {
		if ( fields == null) {
			fields = createMockFields();
		}
		return fields;
	}
	
	protected ArrayList<IQueryField> createMockFields() {
		ArrayList<IQueryField> fields = new ArrayList<IQueryField>();
		QueryFieldType[] values = QueryFieldType.values();
		fields.add(new ThisQueryField(this) );
		for (int i = 0; i < 10; i++) {
			MockQueryField queryField = new MockQueryField("Field " + i , values[i % (values.length-1)], this );
			fields.add(queryField);
		}
		return fields;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Collection<IQueryTable> getChildQueryTables() {

		if (childQueryTables == null ) { 
			childQueryTables = new ArrayList<IQueryTable>();
			if ( getName().equals("Query Table 1") ) {
				childQueryTables.add( new MockQueryTable("Query Sub Table 1") );
				childQueryTables.add( new MockQueryTable("Query Sub Table 2") );
			}
		}
		return childQueryTables;
	}

	@Override
	public void addChildQueryTable(IQueryTable childTable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergeQueryFields(Collection<IQueryField> queryFields) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<IQueryField> getSelectedQueryFields() {
		Collection<IQueryField> selectedQueryFields = new ArrayList<IQueryField>();
		for (IQueryField queryField : fields ) {
			if ( queryField.isSelected() ) {
				selectedQueryFields.add(queryField);
			}
		}
		return selectedQueryFields;
	}

	@Override
	public String getConnectionName() {
		return null;
	}

	@Override
	public String getObjectStoreName() {
		return null;
	}

	@Override
	public String getConnectionDisplayName() {
		return getConnectionName();
	}

	@Override
	public String getObjectStoreDisplayName() {
		return getObjectStoreName();
	}

	@Override
	public void addQueryField(IQueryField queryField) {
		fields.add(queryField);
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public IQueryField getQueryField(String fieldName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCBREnabled() {
		return true;
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
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyQueryFieldChanged(String propertyName, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		
	}
}

