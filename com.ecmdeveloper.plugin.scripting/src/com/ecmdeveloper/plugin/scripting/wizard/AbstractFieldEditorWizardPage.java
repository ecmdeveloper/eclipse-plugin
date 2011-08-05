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

package com.ecmdeveloper.plugin.scripting.wizard;

import java.util.ArrayList;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public abstract class AbstractFieldEditorWizardPage extends WizardPage {

	private ArrayList<FieldEditor> fieldEditors;
	private Composite container;
	private final IPreferenceStore preferenceStore;
	
	protected AbstractFieldEditorWizardPage(String pageName, IPreferenceStore preferenceStore) {
		super(pageName);
		this.preferenceStore = preferenceStore;
		fieldEditors = new ArrayList<FieldEditor>();
	}

	@Override
	public final void createControl(Composite parent) {
 		container = createContainer(parent);
 		createFieldEditors(container);
 		loadFields();
	}

	protected abstract void createFieldEditors(Composite container);

	private void loadFields() {
		for ( FieldEditor fieldEditor : fieldEditors ) {
			fieldEditor.setPreferenceStore( getPreferenceStore() );
			fieldEditor.load();
		}
	}

	protected IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}
	
	private Composite createContainer(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = getNumberOfControls();
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}
	
	protected Composite getFieldEditorsContainer() {
		return container;
	}
	
	protected void addFieldEditor(FieldEditor fieldEditor) {
		fieldEditors.add(fieldEditor);
	}
	
	public void store() {
		for ( FieldEditor fieldEditor : fieldEditors ) {
			fieldEditor.store();
		}
	}
	
	abstract public int getNumberOfControls();	
}
