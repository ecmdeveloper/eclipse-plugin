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

package com.ecmdeveloper.scripting;

import javax.security.auth.Subject;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.EntireNetwork;
import com.filenet.api.core.Factory;
import com.filenet.api.util.UserContext;

/**
 * @author ricardo.belfor
 *
 */
public class MethodRunner {

	private Connection connection;
	private Domain domain;
	
	public void connect(String username, String password, String url ) {
		System.out.println( "trying to connect!");
		connection = Factory.Connection.getConnection(url);
		
		Subject subject = UserContext.createSubject(connection, username, password, "FileNetP8WSI" );
		UserContext uc = UserContext.get();
		uc.pushSubject(subject);
		
		EntireNetwork entireNetwork = Factory.EntireNetwork.fetchInstance(connection, null);
		domain = entireNetwork.get_LocalDomain();
		System.out.println( "connected to " + domain.get_Name() );
	}
	
	public void run() {
		
	}
}
