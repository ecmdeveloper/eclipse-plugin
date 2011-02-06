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

package com.ecmdeveloper.plugin.search.editor;

import java.lang.reflect.Constructor;

import org.eclipse.gef.requests.CreationFactory;

import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryElement;

/**
 * @author ricardo.belfor
 *
 */
public class QueryCreationFactory implements CreationFactory {

	private Class<? extends QueryElement> type;
	private final Query query;

	public QueryCreationFactory(Query query, Class<? extends QueryElement> type) {
		this.query = query;
		this.type = type;
	}
	
	@Override
	public Object getNewObject() {
		try {
			Constructor<? extends QueryElement> constructor = type.getConstructor( Query.class );
			return constructor.newInstance(query);
		} catch (Exception exc) {
			return null;
		}
	}

	@Override
	public Object getObjectType() {
		return type;
	}
}
