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
import com.ecmdeveloper.plugin.core.model.tasks.codemodule.IUpdateCodeModuleTask;
import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
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
public class UpdateCodeModuleTask extends CodeModuleTask implements IUpdateCodeModuleTask {

	protected String name;
	protected Collection<File> files;
	protected String codeModuleId;
	protected ObjectStore objectStore;

	public UpdateCodeModuleTask(String codeModuleId, String name, Collection<File> files, IObjectStore objectStore ) {
		super();
		this.codeModuleId = codeModuleId;
		this.name = name;
		this.files = files;
		this.objectStore = (ObjectStore) objectStore;
	}

	@Override
	protected Object execute() throws Exception {

		VersionSeries versionSeries = getCodeModuleVersionSeries(objectStore, codeModuleId );
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

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}
}
