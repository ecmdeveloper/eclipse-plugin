/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.model;

import com.filenet.api.constants.Cardinality;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class MultiplicityFormatter {

// TODO the unicode value is not correctly saved to file
//	private static final String HORIZONTAL_ELIPSIS = "\u2026";
	private static final String HORIZONTAL_ELIPSIS = "...";
	private static final String MULTIPLICITY_MULTI_REQUIRED = "[1" + HORIZONTAL_ELIPSIS + "n]";
	private static final String MULTIPLICITY_MULTI_NOT_REQUIRED = "[0" + HORIZONTAL_ELIPSIS + "n]";
	private static final String MULTIPLICITY_SINGLE_REQUIRED = "";
	private static final String MULTIPLICITY_SINGLE_NOT_REQUIRED = "[0" + HORIZONTAL_ELIPSIS + "1]";

	public static String getMultiplicity( com.filenet.api.meta.PropertyDescription internalPropertyDescription ) {
		
		if ( Cardinality.SINGLE.equals( internalPropertyDescription.get_Cardinality() ) ) {
			
			if ( ! internalPropertyDescription.get_IsValueRequired() ) {
				return MULTIPLICITY_SINGLE_NOT_REQUIRED;
			} else {
				return MULTIPLICITY_SINGLE_REQUIRED;
			}
		} else {
			if ( ! internalPropertyDescription.get_IsValueRequired() ) {
				return MULTIPLICITY_MULTI_NOT_REQUIRED;
			} else {
				return MULTIPLICITY_MULTI_REQUIRED;
			}
		}
	}
}
