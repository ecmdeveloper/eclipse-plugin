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

import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.properties.editors.details.input.DoubleFormInput;
import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class DoubleDetailsPage extends BaseDetailsPage {

	private DoubleFormInput doubleInput;
	
	@Override
	protected void createClientContent(Composite client) {
		super.createClientContent(client);
		
		doubleInput = new DoubleFormInput(client, form) {
			@Override
			protected void valueModified(Object value) {
				setDirty(true);
			}
		};
	}
	
	@Override
	protected int getNumClientColumns() {
		return 1;
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		doubleInput.setEnabled( !selected );
	}

	@Override
	protected void propertyChanged(Property property) {
		Object value = property.getValue();
		doubleInput.setValue((Double) value);
	}

	@Override
	protected Object getValue() {
		return doubleInput.getValue();
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
		doubleInput.setFocus();
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}
}
