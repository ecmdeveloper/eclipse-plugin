/**
 * Copyright 2009,2010, Ricardo Belfor
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class StringDetailsPage extends BaseDetailsPage {

	private Text text;

	protected void createClientContent(Composite client) {
	
		super.createClientContent(client);
		
		FormToolkit toolkit = form.getToolkit();

		text = toolkit.createText(client, "", SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP ); 
		text.setLayoutData( new GridData(GridData.FILL_BOTH) );
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		text.setEnabled( !selected );
	}

	@Override
	protected int getNumClientColumns() {
		return 1;
	}

	private void setValue(String value) {
		if ( value != null ) {
			text.setText(value);
		} else {
			text.setText("");
		}
	}

	@Override
	protected void propertyChanged(Property property) {
		Object value = property.getValue();
		setValue( (String) value );
	}

	@Override
	protected Object getValue() {
		return text.getText();
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
