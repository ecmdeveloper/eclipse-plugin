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

package com.ecmdeveloper.plugin.properties.jobs;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.core.model.tasks.TaskManager;
import com.ecmdeveloper.plugin.model.tasks.FetchPropertiesTask;
import com.ecmdeveloper.plugin.properties.editors.ObjectStoreItemEditor;

/**
 * @author Ricardo.Belfor
 *
 */
public class RefreshEditorPropertiesJob extends AbstractEditorJob {

	private static final String JOB_NAME = "Refresh Editor Properties";
	private static final String MONITOR_MESSAGE = "Refreshing Editor Properties";
	private static final String FAILED_MESSAGE = "Refreshing Editor Properties for \"{0}\" failed";
	
	private ClassDescription classDescription;
	
	public RefreshEditorPropertiesJob(ObjectStoreItemEditor editor,IWorkbenchWindow window) {
		super( editor, window, JOB_NAME );
		classDescription = (ClassDescription) getEditorInput().getAdapter( ClassDescription.class );
	}

	protected void runEditorJob() throws Exception {
		fetchProperties();
		editor.refreshProperties();
	}

	private void fetchProperties() throws Exception {
		String[] propertyNames = getPropertyNames();
		
		if ( propertyNames.length != 0 ) {
			FetchPropertiesTask task = new FetchPropertiesTask(objectStoreItem, propertyNames);
			TaskManager.getInstance().executeTaskSync(task);
		}
	}
	
	private String[] getPropertyNames() {

		Set<String> propertyNames = new HashSet<String>();
		for (PropertyDescription propertyDescription : classDescription.getPropertyDescriptions()) {
			propertyNames.add(propertyDescription.getName());
		}

		return propertyNames.toArray(new String[0]);
	}

	@Override
	protected String getFailedMessage() {
		return MessageFormat.format( FAILED_MESSAGE, objectStoreItem.getName() );		
	}

	@Override
	protected String getMonitorMessage() {
		return MONITOR_MESSAGE;
	}
}
