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
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.cmis.model.Authentication;
import com.ecmdeveloper.plugin.cmis.model.Connection;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.ui.wizard.AbstractConfigureConnectionWizardPage;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureConnectionWizardPage extends AbstractConfigureConnectionWizardPage {

	private static final String CONNECTION_NAME_FIELD = "CONNECTION_NAME";
	private static final String BINDING_NAME_FIELD = "BINDING";
	private static final String AUTHENTICATION_NAME_FIELD = "AUTHENTICATION";
	
	private StringFieldEditor connectionNameEditor;
	private String connectionName;
	private BindingType bindingType = BindingType.ATOMPUB;
	private Authentication authentication = Authentication.STANDARD;
	private boolean useCompression = true;
	private boolean useClientCompression = false;
	private boolean useCookies = false;
	private PreferenceStore preferenceStore = new PreferenceStore();
	
	@Override
	protected void createExtraControls(Composite container2) {
		createBindingEditor(container2);
		createConnectionNameEditor();
		createAuthenticationEditor( container2 );
		createCompressEditor(container2);
		createClientCompressEditor(container2);
		createCookiesEditor(container2);
	}

	protected void createConnectionNameEditor() {
		
		connectionNameEditor = new StringFieldEditor("", "Connection name:",container ) {

			@Override
			public int getNumberOfControls() {
				return 2; //ConfigureCredentialsWizardPage.this.getNumberOfControls();
			}

		};

		connectionNameEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					connectionName = (String) event.getNewValue();
					updateControls(CONNECTION_NAME_FIELD);
				}
			}
		});

	}

	private void createAuthenticationEditor(Composite container) {
		
		final String preferenceName = "Authentication";
		
		RadioGroupFieldEditor authenticationEditor = new RadioGroupFieldEditor(preferenceName, "&Authentication:", 3,
				getAuthenticationLabelsAndValues(), container, false) {
			@Override
			public int getNumberOfControls() {
				return 1;
			}
		};

		setFieldEditorValue(preferenceName, authentication.name(), authenticationEditor);
		
		authenticationEditor.setPropertyChangeListener( new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				authentication = Authentication.valueOf((String) event.getNewValue());
				updateControls(AUTHENTICATION_NAME_FIELD);
			}
		});
	}

	private void setFieldEditorValue(final String preferenceName, String value, FieldEditor editor) {
		preferenceStore.setValue(preferenceName, value );
		editor.setPreferenceStore( preferenceStore );
		editor.load();
	}

	private String[][] getAuthenticationLabelsAndValues() {
		String labelsAndValues[][] = new String[Authentication.values().length][2];
		
		int i = 0;
		for (Authentication format : Authentication.values()) {
			labelsAndValues[i++] = new String[] { format.toString(), format.name() };
		}
		return labelsAndValues;
	}
	
	private void createBindingEditor(Composite container) {
		
		final String preferenceName = "Binding";

		String[][] labelsAndValues = { { "AtomPub", BindingType.ATOMPUB.name() },
									   { "Web Services", BindingType.WEBSERVICES.name() } };
	
		RadioGroupFieldEditor bindingEditor = new RadioGroupFieldEditor("", "B&inding:", 3,
				labelsAndValues, container, false) {
			@Override
			public int getNumberOfControls() {
				return 1;
			}
		};

		setFieldEditorValue(preferenceName, bindingType.name(), bindingEditor);
		
		bindingEditor.setPropertyChangeListener( new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				bindingType = BindingType.valueOf((String) event.getNewValue());
				updateControls(BINDING_NAME_FIELD);
			}
		});
	}
	
	private void createCompressEditor(Composite container) {
		
		final String preferenceName = "Compression";
		
		BooleanFieldEditor editor = getBooleanFieldEditor(preferenceName, container, "Use &Compression" );

		setFieldEditorValue(preferenceName, useCompression, editor );
		editor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				useCompression = (Boolean) event.getNewValue();
			}
		});
	}

	private void setFieldEditorValue(String preferenceName, boolean value, FieldEditor editor) {
		preferenceStore.setValue(preferenceName, value );
		editor.setPreferenceStore( preferenceStore );
		editor.load();
	}

	private void createClientCompressEditor(Composite container) {
		
		final String preferenceName = "Client Compression";

		BooleanFieldEditor editor = getBooleanFieldEditor(preferenceName, container, "Use Client C&ompression" );
		
		setFieldEditorValue(preferenceName, useClientCompression, editor );
		editor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				useClientCompression = (Boolean) event.getNewValue();
			}
		});
	}

	private void createCookiesEditor(Composite container) {
		
		final String preferenceName = "Use Cookies";

		BooleanFieldEditor editor = getBooleanFieldEditor(preferenceName, container, "Use Coo&kies" );
		
		setFieldEditorValue(preferenceName, useCookies, editor );
		editor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				useCookies = (Boolean) event.getNewValue();
			}
		});
	}
	
	private BooleanFieldEditor getBooleanFieldEditor(String preferenceName, Composite container, String labelText) {
		return new BooleanFieldEditor(preferenceName, labelText, container ) {
			@Override
			public int getNumberOfControls() {
				return 2;
			}
		};
	}
	
	@Override
	public IConnection getConnection() {
		
		String url = getURL();
		String username = getUsername();
		String password = getPassword();

		final Connection connection = new Connection();
		
		connection.setUrl(url);
		connection.setUsername(username);
		connection.setPassword(password);
		connection.setName(url);
		connection.setDisplayName(connectionName);
		connection.setAuthentication(authentication);
		connection.setBindingType(bindingType);
		connection.setUseCompression(useCompression);
		connection.setUseClientCompression(useClientCompression);
		connection.setUseCookies(useCookies);
		
		return connection;
	}

	@Override
	protected boolean isConnectionFieldsSet() {
		return isFieldSet(getURL() ) && isFieldSet(connectionName);
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

		if ( !isFieldSet( connectionName ) ) {
			if ( CONNECTION_NAME_FIELD.equals(fieldname) ) {
				setErrorMessage("The connection name cannot be empty");
			}
			return false;
		}
		
		return true;
	}

}
