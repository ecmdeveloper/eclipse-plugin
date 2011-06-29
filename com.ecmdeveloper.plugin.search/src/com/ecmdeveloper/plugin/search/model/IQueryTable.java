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
import java.util.Collection;

/**
 * @author ricardo.belfor
 *
 */
public interface IQueryTable {
	public String getName();
	public String getDisplayName();
	public Collection<IQueryField> getQueryFields();
	public Collection<IQueryTable> getChildQueryTables();
	public String getConnectionName();
	public String getConnectionDisplayName();
	public String getObjectStoreName();
	public String getObjectStoreDisplayName();
	public boolean isCBREnabled();
	public void setAlias(String alias);
	public String getAlias();
	
	// TODO: add these to a different interface?
	public void addChildQueryTable(IQueryTable childTable);
	public void mergeQueryFields(Collection<IQueryField> queryFields);
	public Collection<IQueryField> getSelectedQueryFields();
	public void addQueryField(IQueryField queryField );
	public IQueryField getQueryField(String fieldName);
	public void addPropertyChangeListener(PropertyChangeListener listener);
	public void removePropertyChangeListener(PropertyChangeListener listener);
	void notifyQueryFieldChanged(String propertyName, Object oldValue, Object newValue);
}
