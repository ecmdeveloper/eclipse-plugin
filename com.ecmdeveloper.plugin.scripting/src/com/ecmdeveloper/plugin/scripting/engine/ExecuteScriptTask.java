/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.scripting.engine;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class ExecuteScriptTask implements Callable<Object>  {

	private ObjectStoreItem objectStoreItem;
	private ClassLoader classLoader;
	
	public ExecuteScriptTask(ObjectStoreItem objectStoreItem, ClassLoader classLoader) {
		this.objectStoreItem = objectStoreItem;
		this.classLoader = classLoader;
	}

	@Override
	public Object call() throws Exception {

    	ContentEngineConnection connection = objectStoreItem.getObjectStore().getConnection();
//    	System.out.println( methodClass.getClassLoader().getClass().getName() );
//    	System.out.println( MethodRunner.class.getCanonicalName() );
    	Class<?> methodRunnerClass = classLoader.loadClass( "com.ecmdeveloper.scripting.MethodRunner" );
		System.out.println( System.getProperty("java.security.auth.login.config" ) );

    	Object methodRunner = methodRunnerClass.newInstance();
    	String username = connection.getUsername();
		String password = connection.getPassword();
		String url = connection.getUrl();
		
		Method connectMethod = methodRunnerClass.getMethod("connect", new Class[] {String.class, String.class, String.class });
		connectMethod.invoke( methodRunner, username, password, url);
		
		return null;
	}
}
