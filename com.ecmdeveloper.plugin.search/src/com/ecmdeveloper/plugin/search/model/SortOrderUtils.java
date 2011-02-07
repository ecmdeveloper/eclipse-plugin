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

package com.ecmdeveloper.plugin.search.model;

import java.util.Collection;

/**
 * @author ricardo.belfor
 *
 */
public class SortOrderUtils {

	public static int getMax(Collection<IQueryField> fields) {

		int max = 0;
		
		for ( IQueryField field : fields) {
			if ( !SortType.NONE.equals( field.getSortType() ) ) {
				max = Math.max(max, field.getSortOrder() );
			}
		}
		
		return max;
	}
	
	public static void swap(Collection<IQueryField> fields, int value1, int value2) {
		if ( value1 == value2) {
			return;
		}
		
		for ( IQueryField field : fields) {
			if ( !SortType.NONE.equals( field.getSortType() ) ) {
				if ( field.getSortOrder() == value1 ) {
					field.setSortOrder(value2);
				} else if ( field.getSortOrder() == value2 ) {
					field.setSortOrder(value1);
				}
			}
		}
	}
	
	public static void remove(Collection<IQueryField> fields, int value) {

		for ( IQueryField field : fields) {
			if ( !SortType.NONE.equals( field.getSortType() ) ) {
				if ( field.getSortOrder() > value ) {
					field.setSortOrder( field.getSortOrder()-1 );
				}
			}
		}
	}
}
