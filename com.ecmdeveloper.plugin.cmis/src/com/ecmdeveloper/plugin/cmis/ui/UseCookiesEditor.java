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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class UseCookiesEditor extends BooleanFieldEditor {

	private static final String LABEL = "Use Coo&kies";
	private static final String USE_COOKIES_NAME_FIELD = "USE_COOKIES";
	protected boolean useCookies;
	
	public UseCookiesEditor(Composite container) {
		super(USE_COOKIES_NAME_FIELD, LABEL, container );
		setPreferenceStore( new PreferenceStore() );
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);
		
		setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				useCookies = (Boolean) event.getNewValue();
			}
		});		
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}
	
	public boolean getValue() {
		return useCookies;
	}

	public void setValue(boolean useCookies) {
		this.useCookies = useCookies;
		getPreferenceStore().setValue(getPreferenceName(), useCookies );
		load();
	}
}
