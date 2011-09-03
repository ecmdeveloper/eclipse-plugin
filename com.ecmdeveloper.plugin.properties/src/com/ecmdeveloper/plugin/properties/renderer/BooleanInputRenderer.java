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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ricardo.Belfor
 *
 */
public class BooleanInputRenderer extends BaseInputRenderer {

	private ComboViewer valuesCombo;
	private String[] inputValues = {"", "True", "False" };
	
	public BooleanInputRenderer(String labelText, String tooltipText) {
		super(labelText, tooltipText);
	}

	public void renderControls(Composite container, int numColumns) {
		renderLabel(container);
		
		valuesCombo = new ComboViewer(container, SWT.VERTICAL | SWT.BORDER
				| SWT.READ_ONLY);
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = numColumns - 1;
		valuesCombo.getCombo().setLayoutData( gd );
		valuesCombo.setContentProvider( new ArrayContentProvider() );
		
		valuesCombo.setInput(inputValues);
	}
	
	public void setValue(Boolean value) {
		int selectionIndex = 0;
		if ( value != null ) {
			selectionIndex = value.booleanValue() ? 1 : 2;
		}
		valuesCombo.setSelection( new StructuredSelection( inputValues[selectionIndex] ) );
	}
}
