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

import java.util.ArrayList;

/**
 * @author ricardo.belfor
 *
 */
public enum SortType {
	NONE {	
		@Override
		public String toString() {
			return "";
		}
	},
	ASC,
	DESC;


	public static String[] getNames() {

		ArrayList<String> sortTypes = new ArrayList<String>();
		for ( SortType sortType2 : SortType.values() ) {
			sortTypes.add( sortType2.toString()  );
		}
		
		return sortTypes.toArray( new String[0] );
	}
	
	public static SortType valueOf( Integer ordinal ) {
		
		if ( ordinal != null ) {
			for ( SortType sortType : values() ) {
				if ( ordinal.intValue() == sortType.ordinal() ) {
					return sortType;
				}
			}
		}
		return NONE;
	}
}
