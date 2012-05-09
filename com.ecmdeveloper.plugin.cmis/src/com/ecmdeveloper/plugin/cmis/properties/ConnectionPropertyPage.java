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

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.cmis.model.Connection;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.ui.AuthenticationEditor;
import com.ecmdeveloper.plugin.cmis.ui.BindingEditor;
import com.ecmdeveloper.plugin.cmis.ui.UseClientCompressionEditor;
import com.ecmdeveloper.plugin.cmis.ui.UseCompressionEditor;
import com.ecmdeveloper.plugin.cmis.ui.ConnectionNameEditor;
import com.ecmdeveloper.plugin.cmis.ui.UseCookiesEditor;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.properties.AbstractConnectionPropertyPage;

/**
 * @author ricardo.belfor
 *
 */
public class ConnectionPropertyPage extends AbstractConnectionPropertyPage {

	private StringFieldEditor connectionNameEditor;
	private AuthenticationEditor authenticationEditor;
	private BindingEditor bindingEditor;
	private UseCompressionEditor useCompressionEditor;
	private UseClientCompressionEditor useClientCompressionEditor;
	private UseCookiesEditor useCookiesEditor;
	
	@Override
	protected void performDefaults() {
		Connection connection = (Connection) getConnection();
		connectionNameEditor.setStringValue( connection.getDisplayName() );
		bindingEditor.setValue( connection.getBindingType() );
		authenticationEditor.setValue( connection.getAuthentication() );
		useCompressionEditor.setValue( connection.isUseCompression() );
		useClientCompressionEditor.setValue( connection.isUseClientCompression() );
		useCookiesEditor.setValue( connection.isUseCookies() );
		
		super.performDefaults();
	}

	@Override
	protected void createConnectionContents(Composite container, IConnection connection) {
		createConnectionNameEditor(container, connection );		
		super.createConnectionContents(container, connection);
		createBindingEditor(container, connection);
		createAuthenticationEditor(container, connection );
		createUseCompressionEditor(container,connection );
		createUseClientCompressionEditor(container, connection );
		createUseCookiesEditor(container, connection );
	}

	private void createAuthenticationEditor(Composite container, IConnection connection) {
		authenticationEditor = new AuthenticationEditor(container);
		authenticationEditor.setValue( ((Connection)connection).getAuthentication() );
	}

	private void createBindingEditor(Composite container, IConnection connection) {
		bindingEditor = new BindingEditor(container);
		bindingEditor.setValue( ((Connection)connection).getBindingType() );
	}
	
	private void createUseCompressionEditor(Composite container, IConnection connection) {
		useCompressionEditor = new UseCompressionEditor(container);
		useCompressionEditor.setValue(((Connection)connection).isUseCompression() );
	}

	private void createUseClientCompressionEditor(Composite container, IConnection connection) {
		useClientCompressionEditor = new UseClientCompressionEditor(container);
		useClientCompressionEditor.setValue( ((Connection)connection).isUseClientCompression() );
	}

	private void createUseCookiesEditor(Composite container, IConnection connection) {
		useCookiesEditor = new UseCookiesEditor(container);
		useCookiesEditor.setValue(((Connection)connection).isUseCookies() );
	}
	
	protected void createConnectionNameEditor(Composite container, IConnection connection) {

		connectionNameEditor = new ConnectionNameEditor(container) {

			@Override
			protected void updateControls() {
				ConnectionPropertyPage.this.updateControls();
			}
		};
		
		connectionNameEditor.setStringValue( connection.getDisplayName() );
	}	
	
	@Override
	protected void updateControls() {
		
		String connectionName = connectionNameEditor.getStringValue();
		
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
		return new Connection();
	}

	protected void initializeConnection(IConnection connection) {
		
		Connection connection2 = (Connection) connection;

		connection2.setDisplayName( connectionNameEditor.getStringValue() );
		connection2.setName( getUrl() );
		connection2.setUrl( getUrl() );
		connection2.setUsername( getUserName() );
		connection2.setPassword( getPassword() );
		connection2.setStorePassword( isStorePassword() );
		connection2.setAuthentication( authenticationEditor.getValue() );
		connection2.setBindingType( bindingEditor.getValue() );
		connection2.setUseCompression( useCompressionEditor.getValue() );
		connection2.setUseClientCompression( useClientCompressionEditor.getValue() );
		connection2.setUseCookies( useCookiesEditor.getValue() );
	}
}