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

package com.ecmdeveloper.plugin.core.dialogs;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;

/**
 * @author ricardo.belfor
 *
 */
public class LoginDialog extends TitleAreaDialog {

	private final IObjectStore objectStore;
	private IConnection connection;
	private String password;
	private String username;
	private boolean storePassword;
	
	public LoginDialog(Shell parentShell, IObjectStore objectStore ) {
		super(parentShell);
		this.objectStore = objectStore;
		connection = objectStore.getConnection();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText( MessageFormat.format("Connecting to ''{0}''", objectStore.getDisplayName()) );
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Control control = super.createDialogArea(parent);
		setTitle("Credentials");
		String message = MessageFormat.format(
				"Provide the credentials for the connection to the ''{0}''\nObject Store.", objectStore
						.getDisplayName());
		setMessage( message );
		
		Composite container = createContainer(parent);
		createUsernameText( container );
		createPasswordText( container );
		createStorePasswordButton( container );
		
		return control;
	}

	private Composite createContainer(Composite parent) {
	
	    Composite panel = new Composite(parent, SWT.NONE);
	    GridLayout layout = getContainerLayout();
	    panel.setLayout(layout);
	    panel.setLayoutData(new GridData(GridData.FILL_BOTH));
	    panel.setFont(parent.getFont());
	
	    return panel;
	}

	private GridLayout getContainerLayout() {
		GridLayout layout = new GridLayout(2, false);
	    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
	    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
	    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
	    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		return layout;
	}

	private void createUsernameText(Composite container ) {
	
		Label label = new Label(container, SWT.None );
		label.setText( "Username:" );
		
		final Text usernameText = new Text(container, SWT.BORDER );
		usernameText.setLayoutData( getGridData() );
		usernameText.addListener(SWT.Modify, new Listener() {
	
			@Override
			public void handleEvent(Event event) {
				username = usernameText.getText();
			} } 
		);
	
		if ( connection.getUsername() != null ) {
			usernameText.setText( connection.getUsername() );
		}
	}

	private void createStorePasswordButton(Composite container) {
		final Button button = new Button(container, SWT.CHECK );
		button.setText("Store password");
		GridData gridData = getGridData();
		gridData.horizontalSpan = 2;
		button.setLayoutData(gridData);
		button.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				storePassword = button.getSelection();
			}} 
		);
		
		button.setSelection( connection.isStorePassword() );
	}

	private void createPasswordText(Composite container) {

		Label label = new Label(container, SWT.None );
		label.setText( "Password:" );
		
		final Text passwordText = new Text(container, SWT.BORDER );
		passwordText.setLayoutData( getGridData() );
		passwordText.setEchoChar('*');
		passwordText.addListener(SWT.Modify, new Listener() {

			@Override
			public void handleEvent(Event event) {
				password = passwordText.getText();
			} } 
		);
		
		if ( connection.getPassword() != null ) {
			passwordText.setText( connection.getPassword() );
		}
	}

	private GridData getGridData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false );
		return layoutData;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected void okPressed() {
		
		connection.setPassword( password.isEmpty() ? null : password );
		connection.setUsername( username.isEmpty() ? null : username );
		connection.setStorePassword(storePassword);
		
		super.okPressed();
	}
}
