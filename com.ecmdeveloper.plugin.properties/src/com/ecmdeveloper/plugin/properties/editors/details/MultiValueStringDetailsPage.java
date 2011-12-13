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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ricardo.Belfor
 *
 */
public class MultiValueStringDetailsPage extends BaseMultiValueDetailsPage {

	private Text text;
	
	@Override
	protected void createInput(Composite client, FormToolkit toolkit) {
		createText(client, toolkit);
	}

	private void createText(Composite client, FormToolkit toolkit) {
		text = toolkit.createText(client, "", SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP ); 
		text.setLayoutData( new GridData(GridData.FILL_BOTH ) );
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
			}
		});
	}

	@Override
	protected void setInputValue(Object value) {
		if ( value == null) {
			text.setText("");
		} else {
			text.setText( value.toString() );
		}
	}

	@Override
	protected Object getInputValue() {
		return text.getText();
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		super.handleEmptyValueButton(selected);
		text.setEnabled( !selected );
	}
	
	
}
