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

package com.ecmdeveloper.plugin.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;

/**
 * @author Ricardo.Belfor
 *
 */
public class ObjectStoresViewSorter extends ViewerSorter {

	private static int CONTAINERS_CATEGORY = 0;
	private static int CONTAINEES_CATEGORY = 1;
	
	@Override
	public int category(Object objectStoreItem) {

		if (objectStoreItem instanceof Folder ) {
			if ( ((Folder) objectStoreItem).isContained() ) {
				return CONTAINEES_CATEGORY;
			} else {
				return CONTAINERS_CATEGORY;
			}
		} else { 
			return CONTAINEES_CATEGORY;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		String sortName1 = getSortName(e1);
		String sortName2 = getSortName(e2);
		
		return sortName1.compareToIgnoreCase( sortName2 );
	}

	private String getSortName(Object item) {
		
		if ( ! (item instanceof ObjectStoreItem ) ) {
			return item.toString();
		}
		
		ObjectStoreItem objectStoreItem = (ObjectStoreItem) item;
		if ( objectStoreItem.getDisplayName() == null ) {
			return objectStoreItem.getId();
		}
		return objectStoreItem.getDisplayName();
	}
}
