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

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ricardo.belfor
 *
 */
public class IdValueWizardPage extends SimpleValueWizardPage {

	private static final String INVALID_ID_VALUE = "Invalid Id value";
	private static final String TITLE = "Id value";
	private static final String DESCRIPTION = "Enter an Id value.";

	protected IdValueWizardPage() {
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
		} catch (IllegalArgumentException exception) {
			setErrorMessage(INVALID_ID_VALUE);
		}
	}
	
	private String parseInput( String textValue ) {
		if ( textValue.length() != 0 ) {
//			if ( !textValue.startsWith("{") || !textValue.endsWith("}") ) {
//				throw new IllegalArgumentException();
//			}

			
			String regexp = "\\{){0,1}[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}(\\}){0,1}$";
			//Pattern p = Pattern.compile(regexp);
			if ( !textValue.matches(regexp) ) {
				throw new IllegalArgumentException();
			}
			
			
			// TODO parse guid better...
//			System.out.println( textValue.substring(1, textValue.length() - 1 ) );
//			UUID.fromString( textValue.substring(1, textValue.length() - 1 ) );
			return textValue;
		} else {
			return null;
		}
	}
}
