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

package com.ecmdeveloper.plugin.scripting.engine;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;

import javax.security.auth.Subject;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.EntireNetwork;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

/**
 * @author ricardo.belfor
 *
 */
public class MethodRunner {

	private final ScriptingContext scriptingContext;
	private ObjectStore objectStore;
	
	public MethodRunner(ScriptingContext scriptingContext) {
		this.scriptingContext = scriptingContext;
	}

	public static void main(String[] args) {
		
		String jaasConfigFile = "C:\\Data\\My Eclipse\\ecm-developer\\com.ecmdeveloper.plugin.lib\\config\\jaas.conf.WSI";
		System.setProperty("java.security.auth.login.config", jaasConfigFile );
		
		try {
			ScriptingContext scriptingContext = getScriptingContext(args[0]);
			MethodRunner methodRunner = new MethodRunner(scriptingContext);
			methodRunner.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ScriptingContext getScriptingContext(String filename) throws Exception {
		
		ObjectInputStream objectInputStream = null;
		
		try {
			FileInputStream fos = new FileInputStream( filename );
			objectInputStream = new ObjectInputStream(fos);
			ScriptingContext scriptingContext = (ScriptingContext) objectInputStream.readObject();
			return scriptingContext;
		} finally {
			if ( objectInputStream != null ) {
				objectInputStream.close();
			}
		}
	}

	public void run() {
			try {
				connect(scriptingContext.getUsername(), scriptingContext.getPassword(), scriptingContext.getUrl() );
				ClassLoader classLoader = this.getClass().getClassLoader();
				Class<?> class1 = classLoader.loadClass(scriptingContext.getScriptClassName());
				Object newInstance = class1.newInstance();
				Method classMethod = getClassMethod(class1, scriptingContext.getScriptMethodName() );
				
				for (int i = 0; i < scriptingContext.getIds().size(); ++i ) {
					IndependentObject object = fetchObject(i);
					classMethod.invoke(newInstance, object );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	private IndependentObject fetchObject(int i) {
		String classIdent = scriptingContext.getClassNames().get(i);
		String objectId = scriptingContext.getIds().get(i);
		IndependentObject object = objectStore.fetchObject(classIdent, objectId, null);
		return object;
	}

	public void connect(String username, String password, String url ) {
		Connection connection = Factory.Connection.getConnection(url);
		Subject subject = UserContext.createSubject(connection, username, password, "FileNetP8WSI" );
		UserContext uc = UserContext.get();
		uc.pushSubject(subject);
		
		EntireNetwork entireNetwork = Factory.EntireNetwork.fetchInstance(connection, null);
		Domain domain = entireNetwork.get_LocalDomain();
		objectStore = Factory.ObjectStore.fetchInstance(domain, scriptingContext.getObjectStoreName(), null);
	}
	
	private Method getClassMethod(Class<?> methodClass, String methodName) {
		Method classMethod = null;
		try {
			classMethod = methodClass.getMethod(methodName, new Class[] {Object.class} );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classMethod;
	}
}
