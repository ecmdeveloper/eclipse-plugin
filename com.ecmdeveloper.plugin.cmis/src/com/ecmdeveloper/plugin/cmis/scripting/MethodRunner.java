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

package com.ecmdeveloper.plugin.cmis.scripting;

import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import com.ecmdeveloper.plugin.scripting.engine.AbstractMethodRunner;
import com.ecmdeveloper.plugin.scripting.engine.ScriptingContext;

/**
 * @author ricardo.belfor
 *
 */
public class MethodRunner extends AbstractMethodRunner {

	private static Session session;

	public MethodRunner(ScriptingContext scriptingContext) {
		super(scriptingContext);
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
		SessionFactoryImpl sessionFactory = SessionFactoryImpl.newInstance();
		
		Map<String, String> parameters = scriptingContext.getParameters();
		parameters.put(SessionParameter.USER, scriptingContext.getUsername() );
		parameters.put(SessionParameter.PASSWORD, scriptingContext.getPassword() );
		parameters.put(SessionParameter.REPOSITORY_ID, scriptingContext.getObjectStoreName() );
		
		session = sessionFactory.createSession(parameters);
	}

	@Override
	protected Object fetchObject(int index, ScriptingContext scriptingContext) {

		CmisObject object = session.getObject(scriptingContext.getIds().get(index) );
		return object;
	}
	
	public static Session getSession() {
		if ( session == null ) {
			throw new IllegalStateException("The session is not yet initialized" );
		}
		return session;
	}
}
