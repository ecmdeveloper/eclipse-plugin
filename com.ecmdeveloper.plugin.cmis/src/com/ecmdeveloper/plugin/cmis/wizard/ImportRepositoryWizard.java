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

package com.ecmdeveloper.plugin.cmis.wizard;

import java.util.Collection;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.cmis.model.Connection;
import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.ui.wizard.AbstractConfigureConnectionWizardPage;
import com.ecmdeveloper.plugin.ui.wizard.AbstractImportObjectStoreWizard;
import com.ecmdeveloper.plugin.ui.wizard.SelectConnectionWizardPage;

/**
 * @author ricardo.belfor
 *
 */
public class ImportRepositoryWizard extends AbstractImportObjectStoreWizard {

	private static final String CONNECTION_NAME_FIELD = "CONNECTION_NAME";

	public ImportRepositoryWizard() {
		super();
		setWindowTitle( "Import Repository" );
	}

	@Override
	protected AbstractConfigureConnectionWizardPage createConfigureConnectionWizardPage() {
		return new AbstractConfigureConnectionWizardPage() {

			private StringFieldEditor connectionNameEditor;
			private String connectionName;
			
			@Override
			protected void createExtraControls(Composite container2) {
				createConnectionNameEditor();
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
		};
	}

	@Override
	protected SelectConnectionWizardPage createSelectConnectionWizardPage(Collection<IConnection> connections) {
		return new SelectConnectionWizardPage(connections) {

			@Override
			protected ViewerFilter getConnectionsFilter() {
				return new ViewerFilter() {

					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						return  element instanceof Connection; 
					}
				};
			}
			
		};
	}
}
