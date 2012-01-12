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

package com.ecmdeveloper.plugin.ui.model;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;

/**
 * @author ricardo.belfor
 *
 */
public class ObjectStoreItemAdapterFactory implements IAdapterFactory {

	private ObjectStoreItemWorkbenchAdapter objectStoreItemWorkbenchAdapter = new ObjectStoreItemWorkbenchAdapter();
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IWorkbenchAdapter.class) {
            return getWorkbenchElement(adaptableObject);
        }
		return null;
	}

	private Object getWorkbenchElement(Object adaptableObject) {
		return objectStoreItemWorkbenchAdapter;
	}

	@Override
	public Class<?>[] getAdapterList() {
        return new Class[] { IWorkbenchAdapter.class };
	}
}
