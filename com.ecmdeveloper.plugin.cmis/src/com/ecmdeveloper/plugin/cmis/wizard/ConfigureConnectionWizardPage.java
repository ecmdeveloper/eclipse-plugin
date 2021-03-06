/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.cmis.wizard;

import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.cmis.model.Authentication;
import com.ecmdeveloper.plugin.cmis.model.Connection;
import com.ecmdeveloper.plugin.cmis.ui.AuthenticationEditor;
import com.ecmdeveloper.plugin.cmis.ui.BindingEditor;
import com.ecmdeveloper.plugin.cmis.ui.ConnectionNameEditor;
import com.ecmdeveloper.plugin.cmis.ui.UseClientCompressionEditor;
import com.ecmdeveloper.plugin.cmis.ui.UseCompressionEditor;
import com.ecmdeveloper.plugin.cmis.ui.UseCookiesEditor;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.ui.wizard.AbstractConfigureConnectionWizardPage;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureConnectionWizardPage extends AbstractConfigureConnectionWizardPage {

	private static final String CONNECTION_NAME_FIELD = "CONNECTION_NAME";
	
	private ConnectionNameEditor connectionNameEditor;
	private AuthenticationEditor authenticationEditor;
	private BindingEditor bindingEditor;
	private UseCompressionEditor useCompressionEditor;
	private UseClientCompressionEditor useClientCompressionEditor;
	private UseCookiesEditor useCookiesEditor;
	
	@Override
	protected void createExtraControls(Composite container2) {
		createBindingEditor(container2);
		createConnectionNameEditor();
		createAuthenticationEditor( container2 );
		createCompressEditor(container2);
		createClientCompressEditor(container2);
		createUseCookiesEditor(container2);
	}

	protected void createConnectionNameEditor() {

		connectionNameEditor = new ConnectionNameEditor(container) {

			@Override
			protected void updateControls() {
				ConfigureConnectionWizardPage.this.updateControls(CONNECTION_NAME_FIELD);
			}
		};
	}

	private void createAuthenticationEditor(Composite container) {
		authenticationEditor = new AuthenticationEditor(container);
		authenticationEditor.setValue(Authentication.STANDARD);
	}

	private void createBindingEditor(Composite container) {
		bindingEditor = new BindingEditor(container);
		bindingEditor.setValue(BindingType.ATOMPUB);
	}
	
	private void createCompressEditor(Composite container) {
		useCompressionEditor = new UseCompressionEditor(container);
		useCompressionEditor.setValue(true);
	}

	private void createUseCookiesEditor(Composite container) {
		useCookiesEditor = new UseCookiesEditor(container);
		useCookiesEditor.setValue(false);
	}
	
	private void createClientCompressEditor(Composite container) {
		useClientCompressionEditor = new UseClientCompressionEditor(container);
		useClientCompressionEditor.setValue(false);
	}
	
	@Override
	public IConnection getConnection() {
		
		final String url = getURL();
		final String username = getUsername();
		final String password = getPassword();
		final boolean storePassword = isStorePassword();
		final Connection connection = new Connection();
		
		connection.setUrl(url);
		connection.setUsername(username);
		connection.setPassword(password);
		connection.setStorePassword(storePassword);
		connection.setName(url);
		connection.setDisplayName( connectionNameEditor.getValue() );
		connection.setAuthentication( authenticationEditor.getValue() );
		connection.setBindingType( bindingEditor.getValue() );
		connection.setUseCompression( useCompressionEditor.getValue() );
		connection.setUseClientCompression( useClientCompressionEditor.getValue() );
		connection.setUseCookies( useCookiesEditor.getValue() );
		
		return connection;
	}

	@Override
	protected boolean isConnectionFieldsSet() {
		return isFieldSet(getURL() ) && isFieldSet( connectionNameEditor.getValue() );
	}

	private boolean isFieldSet(String value) {
		return value != null && !value.isEmpty();
	}

	@Override
	protected boolean validateInput(String fieldname) {
		
		setErrorMessage(null);
		
		if ( !isFieldSet(getURL() ) ) {
			if ( URL_FIELD.equals(fieldname) ) { 
				setErrorMessage("The connection url cannot be empty");
			}
			return false;
		}

		if ( !isFieldSet( connectionNameEditor.getValue() ) ) {
			if ( CONNECTION_NAME_FIELD.equals(fieldname) ) {
				setErrorMessage("The connection name cannot be empty");
			}
			return false;
		}
		
		return true;
	}

}
