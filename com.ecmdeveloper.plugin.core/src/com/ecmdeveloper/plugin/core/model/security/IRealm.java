/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.core.model.security;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;

/**
 * @author ricardo.belfor
 *
 */
public interface IRealm {

	String getName();
	Collection<IPrincipal> findPrincipals(String pattern, PrincipalType type) throws ExecutionException;
	Collection<IPrincipal> findPrincipals(String pattern, PrincipalType type, IProgressMonitor progressMonitor) throws ExecutionException;
	IObjectStore getObjectStore();
	void store(IPrincipal item, IMemento memento);
	IPrincipal restore(IMemento memento);
	Collection<IPrincipal> getMembers(IPrincipal principal) throws Exception;
	Collection<IPrincipal> getMemberships(IPrincipal principal) throws Exception;
}
