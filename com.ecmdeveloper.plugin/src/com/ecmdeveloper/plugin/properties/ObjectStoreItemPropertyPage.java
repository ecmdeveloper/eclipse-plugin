/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.properties;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;

public class ObjectStoreItemPropertyPage extends PropertyPage {

	private StringFieldEditor nameEditor;

	@Override
	protected Control createContents(Composite parent) {
		
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		panel.setLayout(layout);

		IObjectStoreItem objectStoreItem = (IObjectStoreItem) getElement();
//		nameEditor = new StringFieldEditor("name", "Name:", panel);
//		nameEditor.setStringValue(objectStoreItem.getName() );
		
		createPageRow(panel, "Display Name:", objectStoreItem.getDisplayName() );		
		createPageRow(panel, "Name:", objectStoreItem.getName() );
		if ( !(objectStoreItem instanceof ObjectStore) ) {
			createPageRow(panel, "Id:", objectStoreItem.getId() );		
		}
		createPageRow(panel, "Type:", getElement().getClass().getSimpleName() );		

		return panel;
	}

	private void createPageRow(Composite panel, String name, String value) {
		
		Label namelabel = new Label(panel, SWT.NONE);
		namelabel.setLayoutData( new GridData(GridData.BEGINNING) );
		namelabel.setText( name );
		
		Text nameLabelValue = new Text(panel, SWT.WRAP
				| SWT.READ_ONLY);
		nameLabelValue.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		nameLabelValue.setText( value );
	}
}
