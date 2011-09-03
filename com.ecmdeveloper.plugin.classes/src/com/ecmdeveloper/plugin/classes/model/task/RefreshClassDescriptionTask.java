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

package com.ecmdeveloper.plugin.classes.model.task;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.util.PluginLog;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.tasks.BaseTask;

/**
 * @author Ricardo.Belfor
 *
 */
public class RefreshClassDescriptionTask extends BaseTask {

	private ClassDescription[] classDescriptions;

	public RefreshClassDescriptionTask(ClassDescription[] classDescriptions) {
		this.classDescriptions = classDescriptions;
	}
	
	public ClassDescription[] getClassDescriptions() {
		return classDescriptions;
	}

	@Override
	protected Object execute() throws Exception {

		for (ClassDescription classDescription: classDescriptions) {
			try {
				classDescription.refresh();
			} catch (Exception e) {
				PluginLog.error(e);
			}
		}

		fireTaskCompleteEvent( TaskResult.COMPLETED );

		return null;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		if ( classDescriptions.length == 0) {
			throw new IllegalArgumentException();
		}
		return classDescriptions[0].getObjectStore().getConnection();
	}
}
