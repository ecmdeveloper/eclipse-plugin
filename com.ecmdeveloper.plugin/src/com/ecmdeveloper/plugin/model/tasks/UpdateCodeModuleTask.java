/**
 * 
 */
package com.ecmdeveloper.plugin.model.tasks;

import java.io.File;
import java.util.Collection;

import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.util.Id;

/**
 * @author Ricardo.Belfor
 *
 */
public class UpdateCodeModuleTask extends CodeModuleTask {

	protected String name;
	protected Collection<File> files;
	protected String codeModuleId;
	protected ObjectStore objectStore;

	public UpdateCodeModuleTask(String codeModuleId, String name, Collection<File> files, ObjectStore objectStore ) {
		super();
		this.codeModuleId = codeModuleId;
		this.name = name;
		this.files = files;
		this.objectStore = objectStore;
	}

	@Override
	public Object call() throws Exception {

		com.filenet.api.core.ObjectStore objectStoreObject = (com.filenet.api.core.ObjectStore) objectStore.getObjectStoreObject();
		
		VersionSeries versionSeries = Factory.VersionSeries.getInstance(objectStoreObject, new Id( codeModuleId ) ); 
		versionSeries.fetchProperties( new String[]  { PropertyNames.CURRENT_VERSION } );
		Document document = (Document) versionSeries.get_CurrentVersion();
		
		document.checkout(ReservationType.EXCLUSIVE, null, document.getClassName(), null);
		document.save(RefreshMode.REFRESH);
		com.filenet.api.core.Document reservation = (com.filenet.api.core.Document) document.get_Reservation();
		
		ContentElementList contentElementList = createContent(files);
		
		reservation.set_ContentElements(contentElementList);
		reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION );
		reservation.getProperties().putValue( "DocumentTitle", name );
		reservation.save(RefreshMode.REFRESH);

		return new CodeModule( versionSeries, objectStore, objectStore );
	}
}
