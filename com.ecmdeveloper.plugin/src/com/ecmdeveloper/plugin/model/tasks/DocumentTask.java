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

package com.ecmdeveloper.plugin.model.tasks;

import com.ecmdeveloper.plugin.model.Document;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.VersionSeries;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class DocumentTask extends BaseTask {

	private Document document;

	public DocumentTask(Document document) {
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}

	protected com.filenet.api.core.Document getInternalDocument() {
		return (com.filenet.api.core.Document) getDocument().getObjectStoreObject();
	}

	protected com.filenet.api.core.Document getReservation() {
		
		VersionSeries versionSeries = getVersionSeries();
		versionSeries.fetchProperties( new String[] { PropertyNames.RESERVATION} );
		com.filenet.api.core.Document currentVersion = (com.filenet.api.core.Document) versionSeries
				.get_Reservation();
	
		return currentVersion;
	}

	protected com.filenet.api.core.Document getCurrentVersion() {
		
		VersionSeries versionSeries = getVersionSeries();
		versionSeries.fetchProperties( new String[] { PropertyNames.CURRENT_VERSION } );
		com.filenet.api.core.Document currentVersion = (com.filenet.api.core.Document) versionSeries
				.get_CurrentVersion();
	
		return currentVersion;
	}

	private VersionSeries getVersionSeries() {
		com.filenet.api.core.Document internalDocument = getInternalDocument();
		internalDocument.fetchProperties( new String[] { PropertyNames.VERSION_SERIES } );
		VersionSeries versionSeries = internalDocument.get_VersionSeries();
		return versionSeries;
	}
}
