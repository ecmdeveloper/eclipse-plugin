package com.ecmdeveloper.plugin.properties.editors;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;

public class PropertiesInputForm extends FormPage {

	private ClassDescription classDescription;
	private PropertiesInputBlock propertiesInputBlock;

	public PropertiesInputForm(FormEditor editor, ClassDescription classDescription) {
		super(editor, "propertiesInputPage", "Properties");
		this.classDescription = classDescription;
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		propertiesInputBlock = new PropertiesInputBlock(this, classDescription.getName() );
		propertiesInputBlock.createContent(managedForm);
	}	

	public void refreshFormContent() {
		IEditorInput editorInput = getEditor().getEditorInput();
		propertiesInputBlock.setInput( editorInput );
	}
}