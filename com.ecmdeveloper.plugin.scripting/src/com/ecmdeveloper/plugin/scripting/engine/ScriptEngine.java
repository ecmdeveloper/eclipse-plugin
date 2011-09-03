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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public class ScriptEngine {

	private ExecutorService executorService;
	private static ScriptEngine scriptEngine;
	
	private ScriptEngine() {
		executorService = Executors.newSingleThreadExecutor();
	}
	
	public static ScriptEngine getScriptEngine()
	{
		if ( scriptEngine == null )
		{
			synchronized(ScriptEngine.class) {
				scriptEngine = new ScriptEngine();
			}
		}
		return scriptEngine;
	}
	
	public void execute(ObjectStoreItem objectStoreItem, ClassLoader classLoader) throws Exception {
		
		ExecuteScriptTask task = new ExecuteScriptTask(objectStoreItem, classLoader);
		executorService.submit(task).get();
	}
}
