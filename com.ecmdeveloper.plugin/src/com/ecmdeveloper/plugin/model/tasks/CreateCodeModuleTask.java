package com.ecmdeveloper.plugin.model.tasks;

import java.io.File;
import java.util.Collection;

import com.ecmdeveloper.plugin.model.CodeModule;
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
	
	public CreateCodeModuleTask( String name, Collection<File> files, ObjectStore objectStore ) {
		this.name = name;
		this.files = files;
		this.objectStore = objectStore;
	}

	@Override
	public Object call() throws Exception {

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
}
