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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author ricardo.belfor
 *
 */
public class ScriptingContext implements Serializable {

	private static final long serialVersionUID = 7495704115859965889L;

	private final String username;
	private final String password;
	private final String url;
	private final String objectStore;
	private final ArrayList<String> ids;
	private final ArrayList<String> classNames;
	private final Map<String,String> parameters;
	
	private String scriptClassName;
	private String scriptMethodName;
	
	public ScriptingContext(String username, String password, String url, Map<String, String> parameters, String objectStore) {
		
		this.username = username;
		this.password = password;
		this.url = url;
		this.parameters = parameters;
		this.objectStore = objectStore;
		
		ids = new ArrayList<String>();
		classNames = new ArrayList<String>();
		
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public Map<String,String> getParameters() {
		return parameters;
	}

	public ArrayList<String> getIds() {
		return ids;
	}

	public ArrayList<String> getClassNames() {
		return classNames;
	}
	
	public void addObject(String id, String className ) {
		ids.add(id);
		classNames.add(className);
	}

	public String getScriptClassName() {
		return scriptClassName;
	}

	public void setScriptClassName(String scriptClassName) {
		this.scriptClassName = scriptClassName;
	}

	public String getScriptMethodName() {
		return scriptMethodName;
	}

	public void setScriptMethodName(String scriptMethodName) {
		this.scriptMethodName = scriptMethodName;
	}

	public String getObjectStoreName() {
		return objectStore;
	}
}
