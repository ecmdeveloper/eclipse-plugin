/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.model.tasks;

import java.util.ArrayList;
import java.util.Iterator;

import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.collection.CodeModuleSet;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Document;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;

/**
 * This task gets the code modules from the Object Store.
 * 
 * @author Ricardo.Belfor
 *
 */
public class GetCodeModulesTask extends BaseTask {

	protected ObjectStore objectStore;

	public GetCodeModulesTask(ObjectStore objectStore) {
		super();
		this.objectStore = objectStore;
	}

	/**
	 * Gets the code modules from the Object Store.
	 * 
	 * @return the object
	 * 
	 * @throws Exception the exception
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	protected Object execute() throws Exception {

		SearchScope scope = new SearchScope((com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject() );
		String query = "Select This From CodeModule WHERE IsCurrentVersion = TRUE"; //$NON-NLS-1$
		CodeModuleSet codeModuleSet = (CodeModuleSet) scope.fetchObjects(new SearchSQL( query  ), null, null, null);
		Iterator<?> iterator = codeModuleSet.iterator();
		
		ArrayList<CodeModule> codeModules = new ArrayList<CodeModule>();

		while (iterator.hasNext()) {
			com.filenet.api.core.Document document = (Document) iterator.next(); 
			document.fetchProperties( new String[] { PropertyNames.VERSION_SERIES } );
			codeModules.add( new CodeModule( document.get_VersionSeries(), objectStore, objectStore ) );
		}
		
		return codeModules;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}
}
