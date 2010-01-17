/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.editors.details.input;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Ricardo.Belfor
 *
 */
public class DoubleFormInput extends BaseFormInput {

	private static final String INVALID_DOUBLE_VALUE = "Invalid double value";
	private static final String INVALID_DOUBLE_MESSAGE_KEY = "invalidDouble";

	public DoubleFormInput(Composite client, IManagedForm form) {
		super(client, form);
	}

	@Override
	protected void textModified(String textValue) {
		try {
			Double doubleValue = parseInput(textValue);
			messageManager.removeMessage(INVALID_DOUBLE_MESSAGE_KEY, text );
			valueModified( doubleValue );
		} catch (NumberFormatException exception) {
			messageManager.addMessage(INVALID_DOUBLE_MESSAGE_KEY, INVALID_DOUBLE_VALUE,
					null, IMessageProvider.ERROR, text );
		}
	}
	
	private Double parseInput( String textValue ) {
		if ( textValue.length() != 0 ) {
			return Double.parseDouble( textValue );
		} else {
			return null;
		}
	}

	public void setValue(Double value ) {
		if ( value != null ) {
			text.setText( value.toString() );
		} else {
			text.setText("");
		}
	}
	
	public Double getValue() {
		try {
			return parseInput( text.getText().trim() );
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
