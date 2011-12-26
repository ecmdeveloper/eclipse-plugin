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

package com.ecmdeveloper.plugin.core.properties;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.impl.ObjectStoresManager;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractConnectionPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private static final String CONNECTION_SUCCESFUL_MESSAGE = "Testing the connection was succesful";
	private static final String CONNECTING_FAILED_MESSAGE = "Connecting failed";
	private static final String CONNECT_TITLE = "Connect";
	private StringFieldEditor userNameEditor;
	private StringFieldEditor passwordEditor;
	private StringFieldEditor urlEditor;
	private Button connectButton;

	private String userName;
	private String password;
	private String url;

	public AbstractConnectionPropertyPage() {
	}

	@Override
	final protected Control createContents(Composite parent) {
		
		Composite container = createContainer(parent);
		
		IConnection connection = getConnection();
		userName = connection.getUsername();
		password = connection.getPassword();
		url = connection.getUrl();

		createConnectionContents(container, connection);

		if ( connection.isConnected() ) {
			Label label = new Label(container, SWT.None);
			label.setText("\n\n\nNOTE: Changes to the connection will take only effect after a reconnect.");
			GridData gd = new GridData(GridData.BEGINNING);
			gd.horizontalSpan = 2;
			label.setLayoutData(gd);
		}
		
		return container;
	}

	protected void createConnectionContents(Composite container, IConnection connection) {
		
		createUserNameEditor(container);
		createPasswordEditor(container);
		createUrlEditor(container);
		createConnectButton(container);
	}
	
	protected abstract IConnection getConnection();

	private void createConnectButton(Composite container) {
		connectButton = new Button(container, SWT.NONE);
		connectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performConnect();
			}
		});
		connectButton.setText("Test");
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		connectButton.setLayoutData(gd);
	}

	protected void performConnect() {

		final IConnection connection = getTestConnection();
		initializeConnection(connection);
		
		IRunnableContext context = new ProgressMonitorDialog( getShell() );
		try {
			context.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					monitor.beginTask("Connection to " + url, IProgressMonitor.UNKNOWN );
					connection.connect();
					monitor.done();
				}
			} );
			
			MessageDialog.openInformation(getShell(), CONNECT_TITLE, CONNECTION_SUCCESFUL_MESSAGE);
		} catch (Exception e) {
			PluginMessage.openError(getShell(), CONNECT_TITLE, CONNECTING_FAILED_MESSAGE, e );
		}		
	}

	protected abstract void initializeConnection(IConnection connection);

	protected abstract IConnection getTestConnection();
		
	private Composite createContainer(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);
		return panel;
	}

	protected void createUserNameEditor(Composite container) {
		
		userNameEditor = new StringFieldEditor("", "User name:",container );
		userNameEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					userName = (String) event.getNewValue();
					updateControls();
				}
			}
		});
		userNameEditor.setStringValue(userName);
	}

	protected void createPasswordEditor(Composite container) {
		passwordEditor = new StringFieldEditor("", "Password:",container );
		passwordEditor.getTextControl(container).setEchoChar('*');
		passwordEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					password = (String) event.getNewValue();
				}
			}
		});
		passwordEditor.setStringValue(password);
	}

	protected void createUrlEditor(Composite container) {
		
		urlEditor = new StringFieldEditor("", "URL:",container );
		urlEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					url = (String) event.getNewValue();
					updateControls();
				}
			}
		});
		urlEditor.setStringValue(url);
	}
	
	protected void updateControls() {
		
		if ( url == null || url.isEmpty() ) {
			setValid(false);
			setErrorMessage("The value of the URL cannot be empty");
			return;
		}

		if ( userName == null || userName.isEmpty() ) {
			setValid(false);
			setErrorMessage("The value of the user name cannot be empty");
			return;
		}
		
		setErrorMessage(null);
		setValid(true);
	}

	@Override
	public boolean performOk() {
		initializeConnection( getConnection() );
		ObjectStoresManager.getManager().saveObjectStores();
		return true;
	}

	@Override
	protected void performDefaults() {

		IConnection connection = getConnection();

		userNameEditor.setStringValue( connection.getUsername() );
		passwordEditor.setStringValue( connection.getPassword() );
		urlEditor.setStringValue( connection.getUrl() );
		
		super.performDefaults();
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
