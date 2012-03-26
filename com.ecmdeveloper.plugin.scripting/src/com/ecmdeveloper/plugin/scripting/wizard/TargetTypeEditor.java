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

package com.ecmdeveloper.plugin.scripting.wizard;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class TargetTypeEditor extends RadioGroupFieldEditor {
	
	private static final String TARGET_TYPE_NAME_FIELD = "TARGET_TYPE";
	private TargetType targetType;
	
	public TargetTypeEditor(Composite container) {
		super(TARGET_TYPE_NAME_FIELD, "&Target Type:", 1, getTargetTypeLabelsAndValues(), container, true);
		setPreferenceStore( new PreferenceStore() );
	}

	private static String[][] getTargetTypeLabelsAndValues() {
		String labelsAndValues[][] = new String[TargetType.values().length][2];
		
		int i = 0;
		for (TargetType format : TargetType.values()) {
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
				targetType = TargetType.valueOf((String) event.getNewValue());
				updateControls();
			}
		});
	}

	protected void updateControls() {
	}

	public int getNumberOfControls() {
		return 1;
	}

	public TargetType getValue() {
		return targetType;
	}

	public void setValue(TargetType targetType) {
		this.targetType = targetType;
		getPreferenceStore().setValue(getPreferenceName(), targetType.name() );
		load();
	}
}
