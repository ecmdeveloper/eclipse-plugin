/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;

import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.core.model.tasks.ICheckinTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinTask extends DocumentTask implements ICheckinTask {

	private boolean majorVersion;

	public CheckinTask(Document document, boolean majorVersion, boolean autoClassify ) {
		super(document);
		this.majorVersion = majorVersion;
	}

	public Object call() throws Exception {

		if ( getDocument().isSaved() ) {
			org.apache.chemistry.opencmis.client.api.Document internalDocument = getInternalDocument();
			ObjectId objectId = internalDocument.checkIn(majorVersion, null, null, null);
			Session session = getDocument().getObjectStore().getSession();
			CmisObject newDocument = session.getObject(objectId);
			getDocument().refresh((org.apache.chemistry.opencmis.client.api.Document) newDocument);
		} else {
			Session session = getDocument().getObjectStore().getSession();
			DocumentType typeDefinition = (DocumentType) session.getTypeDefinition( getDocument().getClassName() );
			getDocument().saveNew( getVersioningState(typeDefinition) );
		}
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );

		return null;
	}

	private VersioningState getVersioningState(DocumentType typeDefinition) {
		VersioningState versioningState;
		if ( typeDefinition.isVersionable() == null || !typeDefinition.isVersionable() ) {
			versioningState = VersioningState.NONE;
		} else {
			versioningState = majorVersion ? VersioningState.MAJOR : VersioningState.MINOR;
		}
		return versioningState;
	}
}


