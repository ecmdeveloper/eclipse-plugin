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
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.VersionSeries;

/**
 * @author Ricardo.Belfor
 *
 */
public class CancelCheckoutTask extends DocumentTask {

	public CancelCheckoutTask(Document document) {
		super(document);
	}

	@Override
	public Object call() throws Exception {

		com.filenet.api.core.Document currentVersion = cancelCheckout();
		getDocument().refresh( currentVersion );
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}

	private com.filenet.api.core.Document cancelCheckout() {

		com.filenet.api.core.Document currentVersion = getCurrentVersion();
		com.filenet.api.core.Document reservation = (com.filenet.api.core.Document) currentVersion
				.cancelCheckout();
		reservation.save( RefreshMode.REFRESH );
		return currentVersion;
	}

	private com.filenet.api.core.Document getCurrentVersion() {
		
		com.filenet.api.core.Document internalDocument = getInternalDocument();
		internalDocument.fetchProperties( new String[] { PropertyNames.VERSION_SERIES } );
		VersionSeries versionSeries = internalDocument.get_VersionSeries();
		versionSeries.fetchProperties( new String[] { PropertyNames.CURRENT_VERSION } );
		com.filenet.api.core.Document currentVersion = (com.filenet.api.core.Document) versionSeries
				.get_CurrentVersion();

		return currentVersion;
	}
}
