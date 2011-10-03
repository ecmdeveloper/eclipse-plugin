/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.ui.wizard;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.core.model.IConnection;

public abstract class AbstractConfigureConnectionWizardPage extends WizardPage {

	protected static final String USERNAME_FIELD = "USERNAME";
	protected static final String URL_FIELD = "URL";

	private Button connectButton;

	protected StringFieldEditor usernameEditor;
	protected StringFieldEditor passwordEditor;
	protected StringFieldEditor urlEditor;
	
	protected String username;
	protected String password;
	protected String url;

	protected Composite container;
	
	public AbstractConfigureConnectionWizardPage() {
		super("configureConnection");
		setTitle("Configure Connection");
		setDescription("Configure the new connection to the server.");
	}

	@Override
	public void createControl(Composite parent) {

		container = createContainer(parent);

		createUsernameEditor();
		createPasswordEditor();
		createUrlEditor();

		createExtraControls(container);
		
		createConnectButton(container);
	}

	protected void createExtraControls(Composite container2) {
		
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	protected abstract boolean isConnectionFieldsSet();

	private void updateConnectButton() {
		connectButton.setEnabled( isConnectionFieldsSet() );
	}
	
	private void createConnectButton(Composite container) {
		connectButton = new Button(container, SWT.NONE);
		connectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performConnect();
			}
		});
		connectButton.setText("Connect");
		updateConnectButton();
	}

	protected void performConnect() {
		((AbstractImportObjectStoreWizard) getWizard()).connect();
		setPageComplete( ((AbstractImportObjectStoreWizard) getWizard()).isConnected() );
	}

//	protected void updatePageComplete(ModifyEvent event) {
//
//		setPageComplete(false);
//		
//		if ( event.widget.equals( urlField ) && getURL() == null) {
//			setErrorMessage("The connection url cannot be empty");
//			return;
//		}
//
//		if ( event.widget.equals( usernameField ) && getUsername() == null) {
//			setErrorMessage("The user name cannot be empty" );
//			return;
//		}
//
//		setErrorMessage(null);
//	}

	@Override
	public boolean canFlipToNextPage() {
		return ((AbstractImportObjectStoreWizard) getWizard()).isConnected();
	}

	public String getURL() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public abstract IConnection getConnection();

	protected void createUsernameEditor() {
		
		usernameEditor = new StringFieldEditor("", "Username:",container ) {

			@Override
			public int getNumberOfControls() {
				return 2; //ConfigureCredentialsWizardPage.this.getNumberOfControls();
			}

		};
		usernameEditor.setEmptyStringAllowed(true);
		usernameEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					username = (String) event.getNewValue();
					updateControls(USERNAME_FIELD);
				}
			}
		});

	}
	
	protected void createPasswordEditor() {
		passwordEditor = new StringFieldEditor("", "Password:",container ) {

			@Override
			public int getNumberOfControls() {
				return 2; //ConfigureCredentialsWizardPage.this.getNumberOfControls();
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

	protected void createUrlEditor() {
		urlEditor = new StringFieldEditor("", "URL:",container ) {

			@Override
			public int getNumberOfControls() {
				return 2; //ConfigureCredentialsWizardPage.this.getNumberOfControls();
			}

		};
		urlEditor.setEmptyStringAllowed(false);
		urlEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					url = (String) event.getNewValue();
					updateControls(URL_FIELD);
				}
			}
		});
	}

	protected abstract boolean validateInput(String fieldname);
	
	public void updateControls(String fieldname) {

		updateConnectButton();
		boolean validInput = validateInput(fieldname);
		setPageComplete( validInput );
		if ( validInput ) {
			setErrorMessage(null);
		}
	}
}
