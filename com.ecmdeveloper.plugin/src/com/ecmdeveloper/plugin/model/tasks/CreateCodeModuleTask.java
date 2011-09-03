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
package com.ecmdeveloper.plugin.model.tasks;

import java.io.File;
import java.util.Collection;

import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.DynamicReferentialContainmentRelationship;
import com.filenet.api.core.Factory;

public class CreateCodeModuleTask extends CodeModuleTask {

	protected String name;
	protected Collection<File> files;
	protected ObjectStore objectStore;
	
	public CreateCodeModuleTask( String name, Collection<File> files, IObjectStore objectStore2 ) {
		this.name = name;
		this.files = files;
		this.objectStore = (ObjectStore) objectStore2;
	}

	@Override
	protected Object execute() throws Exception {

		com.filenet.api.core.Folder folder = Factory.Folder.fetchInstance(
				(com.filenet.api.core.ObjectStore) objectStore
						.getObjectStoreObject(), "/CodeModules", null);

		com.filenet.api.admin.CodeModule codeModule = Factory.CodeModule
				.createInstance((com.filenet.api.core.ObjectStore) objectStore
						.getObjectStoreObject(), "CodeModule"); 

		codeModule.getProperties().putValue("DocumentTitle", name);
		codeModule.set_ContentElements( createContent(files) );
		codeModule.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		codeModule.save(RefreshMode.REFRESH);

		DynamicReferentialContainmentRelationship relation = (DynamicReferentialContainmentRelationship) folder
				.file(  codeModule,
						AutoUniqueName.AUTO_UNIQUE,
						name,
						DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE) ;
		relation.save(RefreshMode.NO_REFRESH);

		return new CodeModule( codeModule.get_VersionSeries(), objectStore, objectStore );
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}
}
