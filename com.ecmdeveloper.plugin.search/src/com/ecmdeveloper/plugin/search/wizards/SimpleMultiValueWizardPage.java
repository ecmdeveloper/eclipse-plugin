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

import com.ecmdeveloper.plugin.search.model.IQueryField;

/**
 * @author ricardo.belfor
 *
 */
public abstract class SimpleMultiValueWizardPage extends MultiValueWizardPage {

	private Text text;
	private boolean multiLine;
	
	protected SimpleMultiValueWizardPage(IQueryField queryField) {
		super(queryField);
	}
	
	protected void createValuesControls(Composite parent) {
		text = new Text(parent, getTextStyle() );
		text.setLayoutData( getGridData() );
		text.addModifyListener(  getModifyListener() );
	}

	private GridData getGridData() {
		if ( isMultiLine() ) {
			return new GridData( GridData.FILL_BOTH );
		} else { 
			return new GridData( GridData.FILL_HORIZONTAL );
		}
	}

	private int getTextStyle() {
		int style = SWT.BORDER;
		if ( multiLine ) {
			style |= SWT.MULTI | SWT.V_SCROLL | SWT.WRAP; 
		}
		return style;
	}

	private ModifyListener getModifyListener() {
		return new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String textValue = text.getText().trim();
				if ( textValue.isEmpty() ) {
					setErrorMessage("Empty value");
				} else {
					setErrorMessage(null);
				}
			}
		};
	}

	protected void setEditorValue(Object value) {
		text.setText( value.toString() );
	}

	@Override
	protected abstract Object getEditorValue();
	
	protected String getText() {
		return text.getText();
	}

	@Override
	protected void clearEditorValue() {
		text.setText("");
		setErrorMessage(null);
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	public boolean isMultiLine() {
		return multiLine;
	}
}
