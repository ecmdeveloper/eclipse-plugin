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

package com.ecmdeveloper.plugin.folderview.views;

import java.util.Date;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.folderview.model.Category;

/**
 * @author ricardo.belfor
 *
 */
public class FolderViewComparator extends ViewerComparator {

	private String sortValue;
	private boolean descending;
	
	public String getSortValue() {
		return sortValue;
	}

	public void setSortValue(String sortValue) {
		if (sortValue.equals( this.sortValue ) ) {
			descending = !descending;
		} else {
			this.sortValue= sortValue;
			descending = true;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		Object value1 = null;
		Object value2 = null;

		if (e1 instanceof IObjectStoreItem && e2 instanceof IObjectStoreItem ) {
			value1 = getObjectStoreItemValue(e1);
			value2 = getObjectStoreItemValue(e2);
		} else if (e1 instanceof Category && e2 instanceof Category ) {
			value1 = ((Category) e1).getValue();
			value2 = ((Category) e1).getValue();
		} else if ( e1 instanceof Category && e2 instanceof IObjectStoreItem ) { 
			value2 = getObjectStoreItemValue(e2);
		} else if ( e1 instanceof IObjectStoreItem && e2 instanceof Category ) { 
			value1 = getObjectStoreItemValue(e1);
		}
		
		int returnCode = compareValues(value1, value2);
		
		if ( descending ) {
			returnCode = -returnCode;
		}
		return returnCode;
	}

	private Object getObjectStoreItemValue(Object objectStoreItem) {

		if ( sortValue == null) {
			return objectStoreItem.toString();
		}

		if ( FolderView.NAME_COLUMN.equals(sortValue ) ) {
			return ((IObjectStoreItem) objectStoreItem).getDisplayName();
		}
		return ((IObjectStoreItem) objectStoreItem).getSafeValue(sortValue);
	}

	private int compareValues(Object value1, Object value2) {
		int returnCode; 
		if ( value1 == null && value2 == null ) {
			returnCode = 0;
		} else if ( value1 == null ) {
			returnCode = -1;
		} else if ( value2 == null ) {
			returnCode = 1;
		} else {
			
			if ( value1 instanceof Date ) {
				returnCode = ((Date) value1).compareTo((Date) value2);
			} else if ( value1 instanceof Boolean )  {
				returnCode = ((Boolean) value1).compareTo((Boolean) value2);
			} else if ( value1 instanceof Double) {
				returnCode = ((Double) value1).compareTo((Double) value2);
			} else if ( value1 instanceof Long ) {
				returnCode = ((Long) value1).compareTo((Long) value2);
			} else if ( value1 instanceof Integer ) {
				returnCode = ((Integer) value1).compareTo((Integer) value2);
			} else {
				returnCode = value1.toString().compareTo( value2.toString() );
			}
		}

		return returnCode;
	}
}
