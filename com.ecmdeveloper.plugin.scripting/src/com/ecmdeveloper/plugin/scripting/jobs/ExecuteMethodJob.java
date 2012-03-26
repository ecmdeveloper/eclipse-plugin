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

package com.ecmdeveloper.plugin.scripting.jobs;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import com.ecmdeveloper.plugin.core.model.IConnection;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.scripting.Activator;
import com.ecmdeveloper.plugin.scripting.engine.ScriptingContext;
import com.ecmdeveloper.plugin.scripting.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class ExecuteMethodJob extends Job {

	private static final String JOB_NAME = "Execute Method";
	
	private final IMethod method;
	private final Collection<IObjectStoreItem> objectStoreItems;
	private final boolean debug;

	private final String username;
	private final String password;
	private final String runnerClassName;

	public ExecuteMethodJob(IMethod method, Collection<IObjectStoreItem> objectStoreItems, String username, String password, String runnerClassName, boolean debug) {
		super(JOB_NAME);
		this.method = method;
		this.objectStoreItems = objectStoreItems;
		this.username = username;
		this.password = password;
		this.runnerClassName = runnerClassName;
		this.debug = debug;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
	    try {
			ScriptingContext scriptingContext = getScriptingContext(objectStoreItems);
			String filename = Activator.getDefault().getScriptingContextSerializer().serialize(scriptingContext);
			executeMethod(filename);
		} catch (Exception e) {
			PluginMessage.openErrorFromThread(getName(), e.getLocalizedMessage(), e);
			return Status.CANCEL_STATUS;
		} 
		return Status.OK_STATUS;
	}

	private ScriptingContext getScriptingContext(Collection<IObjectStoreItem> objectStoreItems) {

		ScriptingContext scriptingContext = null;
		for ( IObjectStoreItem objectStoreItem : objectStoreItems) {
			if ( scriptingContext == null ) {
				IObjectStore objectStore = objectStoreItem.getObjectStore();
				IConnection connection = objectStore.getConnection();
				scriptingContext = new ScriptingContext(username, password, connection.getUrl(), connection.getParameters(),
						objectStore.getName());
			}
			scriptingContext.addObject(objectStoreItem.getId(), objectStoreItem.getClassName() );
		}
		
		scriptingContext.setScriptMethodName(method.getElementName());
		scriptingContext.setScriptClassName(method.getDeclaringType().getFullyQualifiedName());
		
		return scriptingContext;
	}

	private void executeMethod(String filename) throws CoreException {

		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(ID_JAVA_APPLICATION);
		
		ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, getLaunchName() );
		workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, runnerClassName );
		workingCopy.setAttribute(ATTR_PROJECT_NAME, method.getJavaProject().getElementName() );
		workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, "\"" + filename + "\"" );
		ILaunchConfiguration launchConfiguration = workingCopy.doSave();
		DebugUITools.launch(launchConfiguration, debug? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE);
	}

	private String getLaunchName() {
		IType declaringType = method.getDeclaringType();
		return declaringType.getFullyQualifiedName('.') + "." + method.getElementName();
	}
}
