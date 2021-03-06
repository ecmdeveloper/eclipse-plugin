/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.wizards;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class DoubleValueWizardPage extends SimpleValueWizardPage {

	private static final String INVALID_DOUBLE_VALUE = "Invalid double value";
	private static final String TITLE = "Double value";
	private static final String DESCRIPTION = "Enter a double value.";

	protected DoubleValueWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	protected void textModified(String textValue) {
		try {
			setValue( parseInput(textValue) );
			setErrorMessage(null);
			setDirty();
		} catch (NumberFormatException exception) {
			setErrorMessage(INVALID_DOUBLE_VALUE);
		}
	}

	private Double parseInput( String textValue ) {
		if ( textValue.length() != 0 ) {
			return Double.parseDouble( textValue );
		} else {
			return null;
		}
	}
}
