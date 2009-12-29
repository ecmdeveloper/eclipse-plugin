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

package com.ecmdeveloper.plugin.properties.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Ricardo Belfor
 *
 */
public class ReadOnlyInputRenderer extends BaseInputRenderer {

	private Label textInput;

	public ReadOnlyInputRenderer(String labelText, String tooltipText) {
		super(labelText, tooltipText);
	}

	public void renderControls(Composite container, int numColumns) {
		renderLabel(container);
		renderReadOnlyInput( container, numColumns );
	}

	public void setValue(String value) {
		if ( value != null ) {
			textInput.setText(value);
		} else {
			textInput.setText("");
		}
	}
	
	protected Label renderReadOnlyInput(Composite container, int numColumns ) {
		textInput = new Label( container, SWT.NONE);
		GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
		gridData.horizontalSpan = numColumns - 1;
		textInput.setLayoutData(gridData);
		textInput.setText("Bla");
		return textInput;
	}
}
