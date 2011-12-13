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

package com.ecmdeveloper.plugin.properties;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.properties.AbstractConnectionPropertyPage;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;

/**
 * @author ricardo.belfor
 *
 */
public class ConnectionPropertyPage extends AbstractConnectionPropertyPage {

	public IConnection getTestConnection() {
	
		final ContentEngineConnection connection = new ContentEngineConnection();
		
		connection.setUrl(getUrl());
		connection.setUsername(getUserName());
		connection.setPassword(getPassword());
		return connection;
	}
	
	public IConnection getConnection() {
		ObjectStore objectStore = (ObjectStore) getElement();
		IConnection connection = objectStore.getConnection();
		return connection;
	}
}
