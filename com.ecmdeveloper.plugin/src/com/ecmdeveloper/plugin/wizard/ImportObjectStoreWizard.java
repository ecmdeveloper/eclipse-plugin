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
package com.ecmdeveloper.plugin.wizard;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.ui.wizard.AbstractConfigureConnectionWizardPage;
import com.ecmdeveloper.plugin.ui.wizard.AbstractImportObjectStoreWizard;
import com.ecmdeveloper.plugin.ui.wizard.SelectConnectionWizardPage;

public class ImportObjectStoreWizard extends AbstractImportObjectStoreWizard 
{

	public ImportObjectStoreWizard() {
		super();
		setWindowTitle( "Import Object Store" );
	}

	@Override
	protected SelectConnectionWizardPage createSelectConnectionWizardPage(Collection<IConnection> connections) {
		return new SelectConnectionWizardPage(connections) {
	
			@Override
			protected ViewerFilter getConnectionsFilter() {
				return new ViewerFilter() {
	
					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						return  element instanceof ContentEngineConnection; 
					}
				};
			}
		};
	}

	@Override
	protected AbstractConfigureConnectionWizardPage createConfigureConnectionWizardPage() {
		
		return new AbstractConfigureConnectionWizardPage() {

			
			@Override
			public IConnection getConnection() {
				
				final String url = getURL();
				final String username = getUsername();
				final String password = getPassword();
				final boolean storePassword = isStorePassword();
				final ContentEngineConnection objectStoreConnection = new ContentEngineConnection();
				
				objectStoreConnection.setUrl(url);
				objectStoreConnection.setUsername(username);
				objectStoreConnection.setPassword(password);
				objectStoreConnection.setStorePassword(storePassword);
				
				return objectStoreConnection;
			}

			@Override
			protected boolean isConnectionFieldsSet() {
				return isFieldSet(getURL() ) && isFieldSet(getUsername() );
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

				if ( !isFieldSet( getUsername() ) ) {
					if ( USERNAME_FIELD.equals(fieldname) ) {
						setErrorMessage("The user name cannot be empty");
					}
					return false;
				}
				
				return true;
			}
			
		};
	}
}