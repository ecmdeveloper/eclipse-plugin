/**
 * Copyright 2009,2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.model.tasks;

import javax.security.auth.Subject;

import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.filenet.api.util.UserContext;

public abstract class BaseTask extends AbstractTask {

	@Override
	public final Object call() throws Exception {
		UserContext userContext = UserContext.get();
		try {
			ContentEngineConnection contentEngineConnection = getContentEngineConnection();
			if ( contentEngineConnection == null ) {
				throw new IllegalArgumentException("The content engine connection cannot be null");
			}
			Subject subject = contentEngineConnection.getSubject();
			userContext.pushSubject(subject);
			return execute();
		} finally {
			userContext.popSubject();
		}
	}
	
	protected abstract Object execute() throws Exception;
	
	protected abstract ContentEngineConnection getContentEngineConnection();
}
