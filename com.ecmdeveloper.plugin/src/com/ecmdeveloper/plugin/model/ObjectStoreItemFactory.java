/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.filenet.api.core.IndependentObject;

/**
 * @author Ricardo.Belfor
 *
 */
public class ObjectStoreItemFactory {

	@SuppressWarnings("unchecked")
	private static Map<String,Class> nameToClassMap = new HashMap<String, Class>();
	
	static
	{
		nameToClassMap.put( "CodeModule", CodeModule.class );
	}
	
	@SuppressWarnings("unchecked")
	public static IObjectStoreItem getObject( IndependentObject object, IObjectStoreItem parent, ObjectStore objectStore ) 
	{
		String className = object.getClassName();
		
		if ( nameToClassMap.containsKey( className) ) {
			
			Class clazz = nameToClassMap.get( className );

			IObjectStoreItem objectStoreItem;
			try {
				Constructor constructor = clazz.getConstructor(new Class[] {Object.class, IObjectStoreItem.class, ObjectStore.class } );
				objectStoreItem = (IObjectStoreItem) constructor.newInstance( new Object[] { object, parent, objectStore } );
				return objectStoreItem;
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		} else {
			throw new UnsupportedOperationException();
		}
	}
}
