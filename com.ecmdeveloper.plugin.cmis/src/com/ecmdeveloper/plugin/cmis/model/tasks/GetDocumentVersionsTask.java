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

import java.util.ArrayList;
import java.util.List;

import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetDocumentVersionsTask;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetDocumentVersionsTask extends AbstractTask implements IGetDocumentVersionsTask {

	private Document document;
	private List<IDocument> versionsList;
	
	public GetDocumentVersionsTask(IDocument document) {
		this.document = (Document) document;
	}

	@Override
	public List<IDocument> getVersions() {
		return versionsList;
	}

	@Override
	public Object call() throws Exception {

		ObjectStore objectStore = document.getObjectStore();
		org.apache.chemistry.opencmis.client.api.Document internalDocument = document.getInternalDocument();
		List<org.apache.chemistry.opencmis.client.api.Document> versions = internalDocument.getAllVersions();
		versionsList = new ArrayList<IDocument>();
		
		for ( org.apache.chemistry.opencmis.client.api.Document version : versions ) {
			versionsList.add( ObjectStoreItemFactory.createDocument(version, null, objectStore ) );
		}		
		return null;
	}
}
