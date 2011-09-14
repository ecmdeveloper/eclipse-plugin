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

import com.ecmdeveloper.plugin.core.model.tasks.ICheckoutTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoreItemFactory;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;

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

	protected Object execute() throws Exception {
		
		com.filenet.api.core.Document currentVersion = getCurrentVersion();
		currentVersion.checkout( ReservationType.EXCLUSIVE, null, null, null);
		currentVersion.save( RefreshMode.REFRESH );
		getDocument().refresh();
		checkoutDocument = ObjectStoreItemFactory.createDocument( currentVersion, null, getDocument().getObjectStore() );
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		
		return null;
	}
}
