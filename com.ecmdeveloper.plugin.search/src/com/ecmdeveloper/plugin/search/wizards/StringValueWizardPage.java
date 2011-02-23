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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author ricardo.belfor
 *
 */
public class StringValueWizardPage extends ValueWizardPage {

	private static final String TITLE = "String value";
	private static final String DESCRIPTION = "Enter a string value.";
	private Text text;
	
	protected StringValueWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	protected void createInput(Composite container) {
		
		text = new Text(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP );
		Object value = getValue();
		if ( value != null && value instanceof String) {
			text.setText((String) value);
		}
		text.setLayoutData( new GridData(GridData.FILL_BOTH) );
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setValue( text.getText() );
				setDirty();
			}
		});
	}
}
