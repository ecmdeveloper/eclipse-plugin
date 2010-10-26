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

package com.ecmdeveloper.plugin.properties.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.classes.model.constants.ClassType;
import com.ecmdeveloper.plugin.properties.editors.CustomObjectEditor;
import com.ecmdeveloper.plugin.properties.editors.input.NewCustomObjectEditorInput;

/**
 * @author Ricardo.Belfor
 *
 */
public class NewCustomObjectWizard extends NewObjectStoreItemWizard {

	private static final String DEFAULT_CLASS_NAME = "CustomObject";
	private static final String WINDOW_TITLE = "New Custom Object";

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle(WINDOW_TITLE);
	}
	
	@Override
	protected ClassType getClassType() {
		return ClassType.CUSTOM_OBJECT_CLASSES;
	}

	@Override
	protected String getEditorId() {
		return CustomObjectEditor.EDITOR_ID;
	}

	protected IEditorInput getEditorInput() {
		return new NewCustomObjectEditorInput( getClassDescription(), getParent() );
	}

	@Override
	protected String getDefaultClassName() {
		return DEFAULT_CLASS_NAME;
	}
}
