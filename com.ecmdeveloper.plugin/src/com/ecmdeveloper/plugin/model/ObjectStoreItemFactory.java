/**
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
