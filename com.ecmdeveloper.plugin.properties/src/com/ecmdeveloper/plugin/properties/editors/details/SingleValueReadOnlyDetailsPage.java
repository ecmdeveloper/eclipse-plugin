/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.properties.editors.details;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class SingleValueReadOnlyDetailsPage extends BaseDetailsPage {

	private Text text;
	
	@Override
	protected void createClientContent(Composite client) {
		FormToolkit toolkit = form.getToolkit();
		text = toolkit.createText(client, "", SWT.MULTI | SWT.V_SCROLL | SWT.WRAP );
		text.setEditable(false);
		text.setLayoutData( new GridData(GridData.FILL_BOTH) );
	}

	@Override
	protected int getNumClientColumns() {
		return 1;
	}

	@Override
	protected Object getValue() {
		return null;
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
	}

	@Override
	protected void propertyChanged(Property property) {
		Object value = property.getValue();
		setValue( value );
	}

	private void setValue(Object value) {
		if ( value != null ) {
			text.setText( value.toString() );
		} else {
			text.setText("(empty)");
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}
}
