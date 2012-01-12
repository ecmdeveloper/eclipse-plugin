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

import com.ecmdeveloper.plugin.core.model.IPropertyDescription;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class MultiplicityFormatter {

	private static final String MULTIPLICITY_SEPARATOR = "..";
	private static final String MULTIPLICITY_MULTI_REQUIRED = "1" + MULTIPLICITY_SEPARATOR + "n";
	private static final String MULTIPLICITY_MULTI_NOT_REQUIRED = "0" + MULTIPLICITY_SEPARATOR + "n";
	private static final String MULTIPLICITY_SINGLE_REQUIRED = "";
	private static final String MULTIPLICITY_SINGLE_NOT_REQUIRED = "0" + MULTIPLICITY_SEPARATOR + "1";

	public static String getMultiplicity( IPropertyDescription propertyDescription ) {
		
		if ( !propertyDescription.isMultivalue() ) {
			
			if ( ! propertyDescription.isRequired() ) {
				return MULTIPLICITY_SINGLE_NOT_REQUIRED;
			} else {
				return MULTIPLICITY_SINGLE_REQUIRED;
			}
		} else {
			if ( ! propertyDescription.isRequired() ) {
				return MULTIPLICITY_MULTI_NOT_REQUIRED;
			} else {
				return MULTIPLICITY_MULTI_REQUIRED;
			}
		}
	}
}
