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

import com.ecmdeveloper.plugin.core.model.tasks.ICancelCheckoutTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.Document;
import com.filenet.api.constants.RefreshMode;

/**
 * @author Ricardo.Belfor
 *
 */
public class CancelCheckoutTask extends DocumentTask implements ICancelCheckoutTask {

	public CancelCheckoutTask(Document document) {
		super(document);
	}

	protected Object execute() throws Exception {

		cancelCheckout();
		getDocument().refresh();
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
}
