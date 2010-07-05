/**
 * Copyright 2009,2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.handlers;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.jobs.ViewDocumentJob;
import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class ViewDocumentHandler extends AbstractDocumentHandler {

	public static final String ID = "com.ecmdeveloper.plugin.viewDocument";
	
	@Override
	protected void handleDocument(Document document) {
		ViewDocumentJob job = new ViewDocumentJob(document, null, getWorkbenchWindow() );
		job.setUser(true);
		job.schedule();
	}

	@Override
	protected void executeFinished() {
		Activator.getDefault().getContentCache().registerAsListener( getWorkbenchWindow() );
	}
}
