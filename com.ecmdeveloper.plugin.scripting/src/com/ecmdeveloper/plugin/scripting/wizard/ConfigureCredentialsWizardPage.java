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

package com.ecmdeveloper.plugin.scripting.wizard;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureCredentialsWizardPage extends AbstractFieldEditorWizardPage {

	private static final String TITLE = "Configure Credentials";
	private static final String USE_EXISTING_CREDENTIALS_PREF_KEY = "Use Existing Credentials";
	private static final String LAUNCH_USERNAME_KEY = "Launch Username";
	private static final String LAUNCH_PASSWORD_KEY = "Launch Password";

	private RadioGroupFieldEditor credentialsEditor;
	private StringFieldEditor usernameEditor;
	private StringFieldEditor passwordEditor;
	
	private Boolean useExistingCredentials;
	private String username;
	private String password;
	
	protected ConfigureCredentialsWizardPage(IPreferenceStore preferenceStore) {
		super(TITLE, preferenceStore);
		setTitle(TITLE);
		setDescription("Configure the launching credentials");
	}

	@Override
	protected void createFieldEditors(Composite container) {
 		createCredentialsEditor(container);
		createUsernameEditor(container);
		createPasswordEditor(container);

		initializeFields();
		updateControls();
		setErrorMessage(null);
	}

	private void createCredentialsEditor(final Composite container) {
		String[][] labelsAndValues = { {"Use existing credentials", "true", },
				{"Use other credentials:", "false", }
		};
		
		credentialsEditor = new RadioGroupFieldEditor(USE_EXISTING_CREDENTIALS_PREF_KEY, "", 1,
				labelsAndValues, container) {

			@Override
			public int getNumberOfControls() {
				return ConfigureCredentialsWizardPage.this.getNumberOfControls();
			}

		};
				
		credentialsEditor.setPropertyChangeListener( new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				useExistingCredentials = Boolean.valueOf( (String) event.getNewValue() );
				updateControls();
			}
		} );
				
		addFieldEditor(credentialsEditor);
	}

	private void initializeFields() {
		
		IPreferenceStore preferenceStore = getPreferenceStore();
		String value = preferenceStore.getString( USE_EXISTING_CREDENTIALS_PREF_KEY );
		if ( value != null ) {
			useExistingCredentials = Boolean.valueOf( value ) || value.isEmpty();
		}
		
		username = preferenceStore.getString( LAUNCH_USERNAME_KEY );
		password = preferenceStore.getString( LAUNCH_PASSWORD_KEY );
	}

	private void createUsernameEditor(Composite container) {
		usernameEditor = new StringFieldEditor(LAUNCH_USERNAME_KEY, "Username:",container ) {

			@Override
			public int getNumberOfControls() {
				return ConfigureCredentialsWizardPage.this.getNumberOfControls();
			}

		};
		usernameEditor.setEmptyStringAllowed(false);
		usernameEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					username = (String) event.getNewValue();
					updateControls();
				}
			}
		});

		addFieldEditor(usernameEditor);
	}

	private void createPasswordEditor(Composite container) {
		passwordEditor = new StringFieldEditor(LAUNCH_PASSWORD_KEY, "Password:",container ) {

			@Override
			public int getNumberOfControls() {
				return ConfigureCredentialsWizardPage.this.getNumberOfControls();
			}

		};
		passwordEditor.getTextControl(container).setEchoChar('*');
		passwordEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					password = (String) event.getNewValue();
				}
			}
		});
	}

	private void updateControls() {
		
		if ( useExistingCredentials == null ) {
			setPageComplete(false);
			setErrorMessage("No credentials selected" );
			return;
		}
		
		if ( useExistingCredentials != null ) {
			
			passwordEditor.setEnabled(!useExistingCredentials, getFieldEditorsContainer() );
			usernameEditor.setEnabled(!useExistingCredentials, getFieldEditorsContainer() );
			
			if (  !useExistingCredentials.booleanValue() && (username == null || username.isEmpty() ) ) {
				setPageComplete(false);
				setErrorMessage("No username configured" );
				return;
			}
		}
		
		setPageComplete(true);
		setErrorMessage(null);
	}
	
	public boolean isUseExistingCredentials() {
		if ( useExistingCredentials != null ) {
			return useExistingCredentials;
		} else {
			throw new UnsupportedOperationException("Credentials must be selected!");
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}
}
