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

package com.ecmdeveloper.plugin.cmis.properties;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.cmis.model.Connection;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.properties.AbstractConnectionPropertyPage;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;

/**
 * @author ricardo.belfor
 *
 */
public class ConnectionPropertyPage extends AbstractConnectionPropertyPage {

	private StringFieldEditor connectionNameEditor;
	private String connectionName;

	@Override
	protected void performDefaults() {
		IConnection connection = getConnection();
		connectionNameEditor.setStringValue( connection.getDisplayName() );
		super.performDefaults();
	}

	@Override
	protected void createConnectionContents(Composite container, IConnection connection) {
		connectionName = connection.getDisplayName();
		createConnectionNameEditor(container);		
		super.createConnectionContents(container, connection);
	}

	private void createConnectionNameEditor(Composite container) {
		
		connectionNameEditor = new StringFieldEditor("", "Connection name:",container );

		connectionNameEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					connectionName = (String) event.getNewValue();
					updateControls();
				}
			}
		});
		
		connectionNameEditor.setStringValue( connectionName );
	}
	
	
	@Override
	protected void updateControls() {
		
		if ( connectionName == null || connectionName.isEmpty() ) {
			setValid(false);
			setErrorMessage("The value of the connection name cannot be empty");
			return;
		}
		
		super.updateControls();
	}

	@Override
	protected IConnection getConnection() {
		ObjectStore objectStore = (ObjectStore) getElement();
		IConnection connection = objectStore.getConnection();
		return connection;
	}

	@Override
	public IConnection getTestConnection() {
		
		String url = getUrl();
		String username = getUserName();
		String password = getPassword();

		final Connection connection = new Connection();
		
		connection.setUrl(url);
		connection.setUsername(username);
		connection.setPassword(password);
		connection.setName(url);
		connection.setDisplayName(connectionName);
		return connection;
	}
}
