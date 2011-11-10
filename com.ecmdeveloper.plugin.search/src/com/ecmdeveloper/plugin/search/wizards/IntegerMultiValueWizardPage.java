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

package com.ecmdeveloper.plugin.search.wizards;

import com.ecmdeveloper.plugin.search.model.IQueryField;

/**
 * @author ricardo.belfor
 *
 */
public class IntegerMultiValueWizardPage extends SimpleMultiValueWizardPage {

	private static final String INVALID_INTEGER_VALUE = "Invalid integer value";
	
	public IntegerMultiValueWizardPage(IQueryField queryField) {
		super(queryField);
		setDescription("Enter Integer Values" );
		setMultiLine(false);
	}

	@Override
	protected Object getEditorValue() {
		String textValue = getText();
		if ( !textValue.isEmpty() ) {
			try {
				return Integer.parseInt(textValue);
			} catch (NumberFormatException e) {
				setErrorMessage(INVALID_INTEGER_VALUE);
			}
		}
		return null;
	}
}
