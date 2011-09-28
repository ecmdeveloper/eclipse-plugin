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

import com.ecmdeveloper.plugin.core.model.tasks.IGetReleasedVersionTask;
import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetReleasedVersionTask extends DocumentTask implements IGetReleasedVersionTask {

	private Document releasedVersionDocument;

	public Document getReleasedVersionDocument() {
		return releasedVersionDocument;
	}

	public GetReleasedVersionTask(Document document) {
		super(document);
	}

	@Override
	public Object call() throws Exception {
		
		org.apache.chemistry.opencmis.client.api.Document releasedVersion = getReleasedVersion();
		releasedVersionDocument = ObjectStoreItemFactory.createDocument( releasedVersion, null, getDocument().getObjectStore() );
		
		return releasedVersionDocument;
	}
}
