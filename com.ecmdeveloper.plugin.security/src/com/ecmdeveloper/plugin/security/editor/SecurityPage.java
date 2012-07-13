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
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.menus.IMenuService;

import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityPage extends FormPage {

	private static final String SECURITY_EDITOR_TITLE = "Security Editor";
	private static final String TITLE = "Security";
	private static final String ID = "securityPage";

	private SecurityEditorBlock propertiesInputBlock;
	
	public SecurityPage(FormEditor editor) {
		super(editor, ID, TITLE);
	}

	
	@Override
	protected void createFormContent(IManagedForm managedForm) {

		setFormTitle(managedForm);
		setFormToolbar(managedForm);
		
		propertiesInputBlock = new SecurityEditorBlock(this);
		propertiesInputBlock.createContent(managedForm);
	}


	private void setFormTitle(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		managedForm.getToolkit().decorateFormHeading( form.getForm() );
		form.setText( SECURITY_EDITOR_TITLE );
		form.setImage( Activator.getImage( IconFiles.GROUP ) );
	}


	private void setFormToolbar(IManagedForm managedForm) {
		ToolBarManager manager = (ToolBarManager) managedForm.getForm().getToolBarManager();
		Separator action = new Separator("Actions");
		IMenuService menuService = (IMenuService) Activator.getDefault().getWorkbench().getService(IMenuService.class);
		menuService.populateContributionManager(manager, "popup:formsToolBar");
		manager.update(true);
		manager.add(action );
	}


	public void refreshFormContent() {
		IEditorInput editorInput = getEditor().getEditorInput();
		propertiesInputBlock.setInput( editorInput );
		setContentDescription(SECURITY_EDITOR_TITLE);
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
