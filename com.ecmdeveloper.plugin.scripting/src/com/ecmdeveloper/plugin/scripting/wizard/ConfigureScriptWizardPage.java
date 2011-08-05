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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.scripting.ScriptingProjectNature;
import com.ecmdeveloper.plugin.scripting.dialogs.MethodSelectionDialog;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureScriptWizardPage extends AbstractFieldEditorWizardPage {

	private static final String LAUNCH_METHOD_RPEF_KEY = "LaunchMethod";
	private static final String LAUNCH_DEBUG_PREF_KEY = "LaunchDebug";
	
	private static final String NO_SCRIPTING_PROJECTS_MESSAGE = "The Workspace does not contain open scripting projects. Use the \r\nNew Project wizard to create a new Content Engine Scripting Project.";
	private static final String TITLE = "Configure Script";

	private JavaElementLabelProvider javaElementLabelProvider;
	private StringButtonFieldEditor methodEditor;

	private IMethod method;
	private boolean debug = false;
	
	protected ConfigureScriptWizardPage(IPreferenceStore preferenceStore) {
		super(TITLE, preferenceStore);
		setTitle(TITLE);
		setDescription("Select the launch method and configure the launching options");
		javaElementLabelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_QUALIFIED | JavaElementLabelProvider.SHOW_ROOT);
	}

	@Override
	protected void createFieldEditors(Composite container) {
		createMethodEditor(container);
		createDebugButton(container);
		initializeFields();
		updateControls();
		setErrorMessage(null);
	}
	
	private void initializeFields() {
		String launchMethodHandle = getPreferenceStore().getString(LAUNCH_METHOD_RPEF_KEY);
		if ( !launchMethodHandle.isEmpty()) {
			method = (IMethod) JavaCore.create(launchMethodHandle, null );
			if ( method != null ) {
				methodEditor.setStringValue( javaElementLabelProvider.getText(method) );				
			}
		}
	}

	private void createMethodEditor(Composite container) {
		methodEditor = new StringButtonFieldEditor("", "Method:",container ) {
	
			@Override
			protected String changePressed() {
				return selectMethod();
			}
		};
		
		methodEditor.getTextControl(container).setEditable(false);
		methodEditor.setChangeButtonText("Select");
	}

	private void createDebugButton(Composite container) {
		BooleanFieldEditor editor = new BooleanFieldEditor(LAUNCH_DEBUG_PREF_KEY,"Launch in Debug mode", container ) {
			@Override
			public int getNumberOfControls() {
				return ConfigureScriptWizardPage.this.getNumberOfControls();
			}
		};
		editor.setPropertyChangeListener( new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				debug = (Boolean) event.getNewValue();
			}
		});
		
		addFieldEditor(editor);
	}

	protected String selectMethod() {
		
		if ( hasScriptingProjects()) {
			MethodSelectionDialog methodSelectionDialog = createMethodSelectionDialog();
			if ( methodSelectionDialog.open() == Dialog.OK ) {
				method = (IMethod) methodSelectionDialog.getFirstResult();
			}
		} else {
			MessageDialog.openError(getShell(), TITLE, NO_SCRIPTING_PROJECTS_MESSAGE);
		}

		updateControls();
		
		return javaElementLabelProvider.getText(method);
	}

	private void updateControls() {
		if ( method == null ) {
			setPageComplete(false);
			setErrorMessage("No method selected");
			return;
		}
		
		setPageComplete(true);
		setErrorMessage(null);
	}
	
	private boolean hasScriptingProjects() {
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for ( IProject project : root.getProjects() ) {
			if ( project.isOpen() ) {
				if ( ScriptingProjectNature.hasNature(project) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	private MethodSelectionDialog createMethodSelectionDialog() {
		MethodSelectionDialog methodSelectionDialog = new MethodSelectionDialog(getShell());
		methodSelectionDialog.setInitialPattern("**");
		return methodSelectionDialog;
	}

	public void store() {
		super.store();
		getPreferenceStore().setValue(LAUNCH_METHOD_RPEF_KEY, method.getHandleIdentifier() );
	}
	
	public IMethod getMethod() {
		return method;
	}

	public boolean isDebug() {
		return debug;
	}

	@Override
	public int getNumberOfControls() {
		return 3;
	}
}
