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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.properties.model.Property;

/**
 * @author Ricardo.Belfor
 *
 */
public class DoubleDetailsPage extends BaseDetailsPage {

	private static final String INVALID_DOUBLE_VALUE = "Invalid double value";
	private static final String INVALID_DOUBLE_MESSAGE_KEY = "invalidDouble";
	
	private Text text;
	private IMessageManager messageManager;

	@Override
	protected void createClientContent(Composite client) {
		super.createClientContent(client);
		
		FormToolkit toolkit = form.getToolkit();
		messageManager = form.getMessageManager();
		createText(client, toolkit);
	}

	private void createText(Composite client, FormToolkit toolkit) {

		text = toolkit.createText(client, "", SWT.BORDER ); 
		text.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					String textValue = text.getText().trim();
					if ( textValue.trim().length() != 0 ) {
						Double.parseDouble( textValue );
					}
					messageManager.removeMessage(INVALID_DOUBLE_MESSAGE_KEY, text );
					setDirty(true);
				} catch (NumberFormatException exception) {
					messageManager.addMessage(INVALID_DOUBLE_MESSAGE_KEY, INVALID_DOUBLE_VALUE,
							null, IMessageProvider.ERROR, text );
				}
			}
		});
	}
	
	@Override
	protected int getNumClientColumns() {
		return 1;
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		text.setEnabled( !selected );
	}

	@Override
	protected void propertyChanged(Property property) {
		Object value = property.getValue();
		if ( value != null ) {
			if ( value instanceof Double ) {
				text.setText( ((Double) value).toString() );
			}
		} else {
			text.setText("");
		}
	}

	@Override
	protected Object getValue() {
		try {
			String textValue = text.getText().trim();
			return Double.parseDouble( textValue );
		} catch (NumberFormatException e) {
			return null;
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
