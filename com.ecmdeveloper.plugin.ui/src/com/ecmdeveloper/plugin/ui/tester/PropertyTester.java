/**
 * Copyright 2009,2011, Ricardo Belfor
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
package com.ecmdeveloper.plugin.ui.tester;

import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IObjectStore;

/**
 * @author Ricardo.Belfor
 *
 */
public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		
		if ( receiver instanceof IObjectStore  )
		{
			if ( "isConnected".equals(property) ) {
				
				return ((IObjectStore)receiver).isConnected();
			}
	
			if ( "notConnected".equals(property) ) {
				
				return ! ((IObjectStore)receiver).isConnected();
			}
		} else if ( receiver instanceof IDocument ) {
			if ( "canCheckout".equals(property) ) {
				return ((IDocument)receiver).canCheckOut();
			}
		}
		
		return false;
	}
}
