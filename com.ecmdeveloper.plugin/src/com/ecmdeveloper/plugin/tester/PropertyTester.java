/**
 * 
 */
package com.ecmdeveloper.plugin.tester;

import com.ecmdeveloper.plugin.model.ObjectStore;

/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {

	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		
		if ( ! ( receiver instanceof ObjectStore ) )
		{
			return false;
		}
		
		if ( "isConnected".equals(property) ) {
			
			return ((ObjectStore)receiver).isConnected();
		}

		if ( "notConnected".equals(property) ) {
			
			return ! ((ObjectStore)receiver).isConnected();
		}
		
		return false;
	}
}
