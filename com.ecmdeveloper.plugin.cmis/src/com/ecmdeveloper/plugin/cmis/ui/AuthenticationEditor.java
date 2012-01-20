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

package com.ecmdeveloper.plugin.cmis.ui;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.cmis.model.Authentication;

/**
 * @author ricardo.belfor
 *
 */
public class AuthenticationEditor extends RadioGroupFieldEditor {

	private static final String AUTHENTICATION_NAME_FIELD = "AUTHENTICATION";
	private Authentication authentication;
	
	public AuthenticationEditor(Composite container) {
		super(AUTHENTICATION_NAME_FIELD, "&Authentication:", 3, getAuthenticationLabelsAndValues(), container, false);
		setPreferenceStore( new PreferenceStore() );
	}

	private static String[][] getAuthenticationLabelsAndValues() {
		String labelsAndValues[][] = new String[Authentication.values().length][2];
		
		int i = 0;
		for (Authentication format : Authentication.values()) {
			labelsAndValues[i++] = new String[] { format.toString(), format.name() };
		}
		return labelsAndValues;
	}

	@Override
	public void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);

		setPropertyChangeListener( new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				authentication = Authentication.valueOf((String) event.getNewValue());
				updateControls();
			}
		});
	}

	protected void updateControls() {
	}

	public int getNumberOfControls() {
		return 1;
	}

	public Authentication getValue() {
		return authentication;
	}

	public void setValue(Authentication authentication) {
		this.authentication = authentication;
		getPreferenceStore().setValue(getPreferenceName(), authentication.name() );
		load();
	}
}
