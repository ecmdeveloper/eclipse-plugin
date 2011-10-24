/**
 * Copyright 2011, Ricardo Belfor
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

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractMethodRunner {

	private final ScriptingContext scriptingContext;

	public AbstractMethodRunner(ScriptingContext scriptingContext) {
		this.scriptingContext = scriptingContext;
	}
	
	protected ScriptingContext getScriptingContext() {
		return scriptingContext;
	}

	protected static ScriptingContext getScriptingContext(String filename) throws Exception {
		
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
			ScriptingContext scriptingContext = getScriptingContext();
			connect(scriptingContext);
			ClassLoader classLoader = this.getClass().getClassLoader();
			Class<?> class1 = classLoader.loadClass(scriptingContext.getScriptClassName());
			Object newInstance = class1.newInstance();
			Method classMethod = getClassMethod(class1, scriptingContext.getScriptMethodName() );
			
			for (int i = 0; i < scriptingContext.getIds().size(); ++i ) {
				Object object = fetchObject(i, scriptingContext );
				classMethod.invoke(newInstance, object );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void connect(ScriptingContext scriptingContext);

	protected abstract Object fetchObject(int index, ScriptingContext scriptingContext);

	private Method getClassMethod(Class<?> methodClass, String methodName) {
		Method classMethod = null;
		try {
			classMethod = methodClass.getMethod(methodName, new Class[] {Object.class} );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classMethod;
	}
	
//	public static abstract AbstractMethodRunner getMethodRunner(ScriptingContext scriptingContext);
}
