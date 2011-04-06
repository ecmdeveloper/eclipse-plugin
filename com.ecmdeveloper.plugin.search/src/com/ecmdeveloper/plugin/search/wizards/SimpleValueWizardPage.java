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

	private Text text;
	private boolean multiLine = false	;
	
	protected SimpleValueWizardPage(String pageName) {
		super(pageName);
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	@Override
	protected void createInput(Composite container) {

		int style = SWT.BORDER + (multiLine? SWT.MULTI | SWT.V_SCROLL | SWT.WRAP : 0 );
		text = new Text(container, style ); 
		text.setLayoutData( new GridData(multiLine? GridData.FILL_BOTH : GridData.FILL_HORIZONTAL) );
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

	protected void setText(String value) {
		text.setText(value);
	}
	
	protected abstract void textModified(String textValue);
}
