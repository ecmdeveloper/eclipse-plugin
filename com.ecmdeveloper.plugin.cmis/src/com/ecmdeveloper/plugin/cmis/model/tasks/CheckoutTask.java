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
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;

import com.ecmdeveloper.plugin.core.model.tasks.ICheckoutTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckoutTask extends DocumentTask implements ICheckoutTask {
	
	private Document checkoutDocument;

	public CheckoutTask(Document document) {
		super(document);
	}

	public Document getCheckoutDocument() {
		return checkoutDocument;
	}

	@Override
	public Object call() throws Exception {
		
		org.apache.chemistry.opencmis.client.api.Document currentVersion = getCurrentVersion();
		
		ObjectId objectId = currentVersion.checkOut();
		Session session = getDocument().getObjectStore().getSession();
		CmisObject privateWorkingCopy = session.getObject(objectId);

		getDocument().refresh();
		checkoutDocument = ObjectStoreItemFactory.createDocument( privateWorkingCopy, null, getDocument().getObjectStore() );
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}
}
