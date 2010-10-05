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

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.classes.model.constants.ClassType;
import com.ecmdeveloper.plugin.content.wizard.ContentSelectionWizardPage;
import com.ecmdeveloper.plugin.properties.editors.DocumentEditor;
import com.ecmdeveloper.plugin.properties.editors.input.NewDocumentEditorInput;

/**
 * @author ricardo.belfor
 *
 */
public class NewDocumentWizard extends NewObjectStoreItemWizard {

	private static final String WINDOW_TITLE = "New Document";
	private static final String DEFAULT_CLASS_NAME = "Document";
	private ContentSelectionWizardPage contentSelectionWizardPage;
	private ConfigureCreateDocumentWizardPage configureCreateDocumentWizardPage;
	private ArrayList<Object> files;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setWindowTitle(WINDOW_TITLE);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addContentSelectionWizardPage();
		addConfigureCreateDocumentWizardPage();
	}

	private void addConfigureCreateDocumentWizardPage() {
		configureCreateDocumentWizardPage = new ConfigureCreateDocumentWizardPage();
		addPage(configureCreateDocumentWizardPage);
	}

	private void addContentSelectionWizardPage() {
		contentSelectionWizardPage = new ContentSelectionWizardPage(null);
		if ( files != null ) {
			contentSelectionWizardPage.setContent(files, null);
		}
		addPage(contentSelectionWizardPage);
	}

	@Override
	protected ClassType getClassType() {
		return ClassType.DOCUMENT_CLASSES;
	}

	@Override
	protected String getEditorId() {
		return DocumentEditor.EDITOR_ID;
	}

	protected IEditorInput getEditorInput() {
		
		ArrayList<Object> content = contentSelectionWizardPage.getContent();
		String mimeType = contentSelectionWizardPage.getMimeType();
		
		boolean checkinMajor = configureCreateDocumentWizardPage.isCheckinMajor();
		boolean autoClassify = configureCreateDocumentWizardPage.isAutoClassify();
		
		NewDocumentEditorInput newDocumentEditorInput = new NewDocumentEditorInput( getClassDescription(), getParentFolder() );
		
		newDocumentEditorInput.setContent( content);
		newDocumentEditorInput.setMimeType( mimeType );
		newDocumentEditorInput.setCheckinMajor(checkinMajor);
		newDocumentEditorInput.setAutoClassify(autoClassify);
		
		return newDocumentEditorInput;
	}

	@Override
	protected String getDefaultClassName() {
		return DEFAULT_CLASS_NAME;
	}

	public void setFiles(ArrayList<Object> files) {
		this.files = files;
	}
}
