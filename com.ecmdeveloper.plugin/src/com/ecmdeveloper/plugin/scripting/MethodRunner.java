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

package com.ecmdeveloper.plugin.scripting;

import javax.security.auth.Subject;

import com.ecmdeveloper.plugin.scripting.engine.AbstractMethodRunner;
import com.ecmdeveloper.plugin.scripting.engine.ScriptingContext;
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
public class MethodRunner extends AbstractMethodRunner {

	private ObjectStore objectStore;
	
	public MethodRunner(ScriptingContext scriptingContext) {
		super(scriptingContext);

		String jaasConfigFile = "C:\\Data\\My Eclipse\\ecm-developer\\com.ecmdeveloper.plugin.lib\\config\\jaas.conf.WSI";
		System.setProperty("java.security.auth.login.config", jaasConfigFile );
	}

	public static void main(String[] args) {
		
		try {
			ScriptingContext scriptingContext = getScriptingContext(args[0]);
			MethodRunner methodRunner = new MethodRunner(scriptingContext);
			methodRunner.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void connect(ScriptingContext scriptingContext) {
		
		Connection connection = Factory.Connection.getConnection( scriptingContext.getUrl() );
		Subject subject = UserContext.createSubject(connection, scriptingContext.getUsername(), scriptingContext.getPassword(), "FileNetP8WSI" );
		UserContext uc = UserContext.get();
		uc.pushSubject(subject);
		
		EntireNetwork entireNetwork = Factory.EntireNetwork.fetchInstance(connection, null);
		Domain domain = entireNetwork.get_LocalDomain();
		objectStore = Factory.ObjectStore.fetchInstance(domain, getScriptingContext().getObjectStoreName(), null);
	}

	@Override
	protected Object fetchObject(int index, ScriptingContext scriptingContext) {
		String classIdent = scriptingContext.getClassNames().get(index);
		String objectId = scriptingContext.getIds().get(index);
		IndependentObject object = objectStore.fetchObject(classIdent, objectId, null);
		return object;
	}
}
