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

import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class BindingEditor extends RadioGroupFieldEditor {

	private static final String BINDING_NAME_FIELD = "BINDING";
	protected BindingType bindingType;

	public BindingEditor(Composite container) {
		super(BINDING_NAME_FIELD, "B&inding:", 3, getLabelsAndValue(), container, false);
		setPreferenceStore( new PreferenceStore() );
	}

	private static String[][] getLabelsAndValue() {
		String[][] labelsAndValues = { { "AtomPub", BindingType.ATOMPUB.name() },
				   { "Web Services", BindingType.WEBSERVICES.name() } };
		return labelsAndValues;
	}

	@Override
	public void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns);

		setPropertyChangeListener( new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				bindingType = BindingType.valueOf((String) event.getNewValue());
				updateControls();
			}
		});
		
	}

	protected void updateControls() {
	}

	public int getNumberOfControls() {
		return 1;
	}

	public BindingType getValue() {
		return bindingType;
	}

	public void setValue(BindingType bindingType) {
		this.bindingType = bindingType;
		getPreferenceStore().setValue(getPreferenceName(), bindingType.name() );
		load();
	}	
}
