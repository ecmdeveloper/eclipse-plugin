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

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.ui.wizard.AbstractConfigureConnectionWizardPage;
import com.ecmdeveloper.plugin.ui.wizard.AbstractImportObjectStoreWizard;

public class ImportObjectStoreWizard extends AbstractImportObjectStoreWizard 
{

	@Override
	protected AbstractConfigureConnectionWizardPage createConfigureConnectionWizardPage() {
		
		return new AbstractConfigureConnectionWizardPage() {

			@Override
			public IConnection getConnection() {
				
				String url = getURL();
				String username = getUsername();
				String password = getPassword();

				final ContentEngineConnection objectStoreConnection = new ContentEngineConnection();
				
				objectStoreConnection.setUrl(url);
				objectStoreConnection.setUsername(username);
				objectStoreConnection.setPassword(password);
				
				return objectStoreConnection;
			}

			@Override
			protected boolean isConnectionFieldsSet() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			protected boolean validateInput() {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
	}
}