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

package com.ecmdeveloper.plugin.security.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityPage extends FormPage {

	private static final String TITLE = "Security";
	private static final String ID = "securityPage";

	private SecurityEditorBlock propertiesInputBlock;
	
	public SecurityPage(FormEditor editor) {
		super(editor, ID, TITLE);
	}

	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		propertiesInputBlock = new SecurityEditorBlock(this);
		propertiesInputBlock.createContent(managedForm);
	}	

	public void refreshFormContent() {
		IEditorInput editorInput = getEditor().getEditorInput();
		propertiesInputBlock.setInput( editorInput );
		setContentDescription("Security Editor");
	}


	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return super.isDirty();
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		super.doSave(monitor);
	}


	@Override
	public boolean isEditor() {
		return true;
	}
	
	
	
}
