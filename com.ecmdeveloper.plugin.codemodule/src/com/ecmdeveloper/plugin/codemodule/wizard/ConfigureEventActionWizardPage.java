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

package com.ecmdeveloper.plugin.codemodule.wizard;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.core.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureEventActionWizardPage extends WizardPage {

	private static final String NO_CLASSES_IMPLEMENTING_INTERFACE = "The Code Module does not contain classes implementing the ''{0}'' interface.";
	private static final String EVENT_ACTION_HANDLER_INTERFACE = "com.filenet.api.engine.EventActionHandler";

	private static final String NAME_FIELD  = "NAME_FIELD";
	private static final String CLASS_NAME_FIELD = "CLASS_NAME_FIELD";
	
	private static final String TITLE = "Configure Event Action";
	private static final String DESCRIPTION_FMT = "Configure the new Event Action based on the code module ''{0}''.";
	
	private final PreferenceStore preferenceStore = new PreferenceStore();
	private final CodeModuleFile codeModuleFile;

	private String name;
	private String className; 
	private boolean enabled = true;
	
	protected ConfigureEventActionWizardPage(CodeModuleFile codeModuleFile) {
		super(TITLE);
		setTitle(TITLE);
		setDescription(MessageFormat.format(DESCRIPTION_FMT, codeModuleFile.getName()));
		this.codeModuleFile = codeModuleFile;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = createContainer(parent);
		createNameEditor(container);
		createClassNameEditor(container);
		createEnabledEditor(container);
		setPageComplete(false);
	}

	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}

	protected void createNameEditor(Composite container) {
		
		StringFieldEditor nameEditor = new StringFieldEditor("", "Name:",container );
		nameEditor.setEmptyStringAllowed(false);
		nameEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					name = (String) event.getNewValue();
					updateControls(NAME_FIELD);
				}
			}
		});
	}

	private void createClassNameEditor(Composite container) {
		
		String[][] namesAndValues = getClassNames();
		ComboFieldEditor nameEditor = new ComboFieldEditor("", "&Class Name:",namesAndValues, container );

		nameEditor.setPropertyChangeListener( new IPropertyChangeListener() {
	
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ( event.getProperty().equals( FieldEditor.VALUE ) ) {
					className = (String) event.getNewValue();
					updateControls(CLASS_NAME_FIELD);
				}
			}
		});
	}

	private void createEnabledEditor(Composite container) {
		final String preferenceName = "Enabled";
		BooleanFieldEditor editor = new BooleanFieldEditor(preferenceName, "&Enabled:", BooleanFieldEditor.SEPARATE_LABEL, container );
		setFieldEditorValue(preferenceName, enabled, editor);
		editor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				enabled = (Boolean) event.getNewValue();
			}
		});
	}

	private void setFieldEditorValue(String preferenceName, boolean value, FieldEditor editor) {
		preferenceStore.setValue(preferenceName, value );
		editor.setPreferenceStore( preferenceStore );
		editor.load();
	}
	
	private String[][] getClassNames() {
		
		try {
			Collection<String> classNames = codeModuleFile.getClassNames(EVENT_ACTION_HANDLER_INTERFACE);
			String[][] values = new String[classNames.size()][];
			
			if ( classNames.size() != 0  ) {
				int index = 0;
				for ( String className : classNames) {
					values[index++] = new String[] { className, className };
				}
			} else { 
				setErrorMessage(MessageFormat.format(NO_CLASSES_IMPLEMENTING_INTERFACE,
						EVENT_ACTION_HANDLER_INTERFACE));
			}
			
			return values;
		} catch (Exception e) {
			PluginMessage.openError(getShell(), "Error", e.getLocalizedMessage(), e );
		}		
		return null;
	}

	protected void updateControls(String controlName ) {

		setPageComplete(name != null && !name.isEmpty() && className != null && !className.isEmpty() );
		setErrorMessage(null);

		if (NAME_FIELD.equals(controlName) ) {
			if (name == null || name.isEmpty() ) {
				setErrorMessage("The name field cannot be empty." );
				return;
			}
		}
	}

	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
