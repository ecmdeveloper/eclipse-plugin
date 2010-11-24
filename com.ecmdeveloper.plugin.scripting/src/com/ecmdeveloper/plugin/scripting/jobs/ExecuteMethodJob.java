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

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.ExecuteMethodTask;
import com.ecmdeveloper.plugin.scripting.engine.ScriptEngine;
import com.ecmdeveloper.plugin.scripting.util.PluginMessage;
import com.ecmdeveloper.scripting.MethodRunner;

/**
 * @author ricardo.belfor
 *
 */
public class ExecuteMethodJob extends Job {

	private static final String JOB_NAME = "Execute Method";
	
	private final Method method;
	private final ObjectStoreItem[] objectStoreItems;
	private final Class<?> methodClass;
	private final Writer writer;

	public ExecuteMethodJob(Class<?>  methodClass, Method method, ObjectStoreItem[] objectStoreItems, Writer writer) {
		super(JOB_NAME);
		this.methodClass = methodClass;
		this.method = method;
		this.objectStoreItems = objectStoreItems;
		this.writer = writer;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
	    try
		{
	    	ObjectStoreItem objectStoreItem =  objectStoreItems[0];
	    	ScriptEngine.getScriptEngine().execute(objectStoreItem, methodClass.getClassLoader() );
//	    	ContentEngineConnection connection = objectStoreItem.getObjectStore().getConnection();
//	    	System.out.println( methodClass.getClassLoader().getClass().getName() );
////	    	System.out.println( MethodRunner.class.getCanonicalName() );
//	    	Class<?> methodRunnerClass = methodClass.getClassLoader().loadClass( "com.ecmdeveloper.scripting.MethodRunner" );
//
//			System.out.println( System.getProperty("java.security.auth.login.config" ) );
//
//	    	Object methodRunner = methodRunnerClass.newInstance();
//	    	String username = connection.getUsername();
//			String password = connection.getPassword();
//			String url = connection.getUrl();
//			
//			Method connectMethod = methodRunnerClass.getMethod("connect", new Class[] {String.class, String.class, String.class });
//			connectMethod.invoke( methodRunner, username, password, url);
			
//			methodRunner.connect(username, password, url);
	    	
//	    	for ( ObjectStoreItem objectStoreItem : objectStoreItems) {
//	    		executeMethod(objectStoreItem);
//	    	}
		} catch (Exception e) {
			e.printStackTrace();
			PluginMessage.openErrorFromThread(getName(), e.getLocalizedMessage(), e);
		} 
		return Status.OK_STATUS;
	}

	private void executeMethod(ObjectStoreItem objectStoreItem) throws ExecutionException,
			InstantiationException, IllegalAccessException, IOException {
		Object target = methodClass.newInstance();
		ExecuteMethodTask task = new ExecuteMethodTask(objectStoreItem, method, target, writer);
		ObjectStoresManager.getManager().executeTaskSync(task);
		writer.flush();
	}
}
