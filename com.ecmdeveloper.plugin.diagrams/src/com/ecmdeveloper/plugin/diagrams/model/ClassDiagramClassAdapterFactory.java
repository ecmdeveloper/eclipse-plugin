/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.model;

import org.eclipse.core.runtime.IAdapterFactory;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;

/**
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramClassAdapterFactory implements IAdapterFactory {

	private static Class<?>[] SUPPORTED_TYPES = new Class[] { ClassDiagramClass.class };	
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		
		if ( ClassDiagramClass.class.equals( adapterType ) ) {

			if ( ClassDescription.class.isInstance( adaptableObject ) ) {
				return new ClassDiagramClass( (ClassDescription) adaptableObject );
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class[] getAdapterList() {
		return SUPPORTED_TYPES;
	}
}
