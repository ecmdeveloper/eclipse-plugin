/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.core.properties;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class StorePasswordEditor extends BooleanFieldEditor {

	private static final String LABEL = "St&ore password";
	private static final String STORE_PASSWORD_NAME_FIELD = "STORE_PASSWORD";
	protected boolean storePassword;
	
	public StorePasswordEditor(Composite container) {
		super(STORE_PASSWORD_NAME_FIELD, LABEL, container );
		setPreferenceStore( new PreferenceStore() );
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);
		
		setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				storePassword = (Boolean) event.getNewValue();
			}
		});		
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}
	
	public boolean getValue() {
		return storePassword;
	}

	public void setValue(boolean storePassword) {
		this.storePassword = storePassword;
		getPreferenceStore().setValue(getPreferenceName(), storePassword );
		load();
	}
}
