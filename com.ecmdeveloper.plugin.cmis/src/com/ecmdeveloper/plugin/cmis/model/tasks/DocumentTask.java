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

import java.util.List;

import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class DocumentTask extends AbstractTask {

	private Document document;

	public DocumentTask(Document document) {
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}

	protected org.apache.chemistry.opencmis.client.api.Document getInternalDocument() {
		return getDocument().getInternalDocument();
	}

	protected org.apache.chemistry.opencmis.client.api.Document getReservation() {

		List<org.apache.chemistry.opencmis.client.api.Document> versions = getInternalDocument().getAllVersions();
//		org.apache.chemistry.opencmis.client.api.Document version = versions.get( versions.size() - 1);
		org.apache.chemistry.opencmis.client.api.Document version = versions.get(0);
		return version;
	}

	protected org.apache.chemistry.opencmis.client.api.Document getCurrentVersion() {
		
		org.apache.chemistry.opencmis.client.api.Document internalDocument = getInternalDocument();
		org.apache.chemistry.opencmis.client.api.Document currentVersion = internalDocument.getObjectOfLatestVersion(false);
	
		return currentVersion;
	}

	protected org.apache.chemistry.opencmis.client.api.Document getReleasedVersion() {
		
		org.apache.chemistry.opencmis.client.api.Document internalDocument = getInternalDocument();
		org.apache.chemistry.opencmis.client.api.Document currentVersion = internalDocument.getObjectOfLatestVersion(true);
	
		return currentVersion;
	}

//	private VersionSeries getVersionSeries() {
//		com.filenet.api.core.Document internalDocument = getInternalDocument();
//		internalDocument.fetchProperties( new String[] { PropertyNames.VERSION_SERIES } );
//		VersionSeries versionSeries = internalDocument.get_VersionSeries();
//		return versionSeries;
//	}

}
