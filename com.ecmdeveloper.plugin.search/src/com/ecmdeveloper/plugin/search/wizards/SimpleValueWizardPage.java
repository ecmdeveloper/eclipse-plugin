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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author ricardo.belfor
 *
 */
public abstract class SimpleValueWizardPage extends ValueWizardPage {

	protected SimpleValueWizardPage(String pageName) {
		super(pageName);
	}

	protected Text text;

	@Override
	protected void createInput(Composite container) {

		text = new Text(container, SWT.BORDER ); 
		text.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		Object value = getValue();
		if ( value != null ) {
			text.setText( value.toString() );
		}
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String textValue = text.getText().trim();
				textModified(textValue);
			}
		} );
	}

	protected abstract void textModified(String textValue);
}
