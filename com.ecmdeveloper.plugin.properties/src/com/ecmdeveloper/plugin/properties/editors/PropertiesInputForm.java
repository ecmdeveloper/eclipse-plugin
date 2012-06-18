package com.ecmdeveloper.plugin.properties.editors;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.ecmdeveloper.plugin.core.model.IClassDescription;

public class PropertiesInputForm extends FormPage {

	private static final String TITLE = "Properties";
	private static final String ID = "propertiesInputPage";
	
	private IClassDescription classDescription;
	private PropertiesInputBlock propertiesInputBlock;

	public PropertiesInputForm(FormEditor editor, IClassDescription classDescription) {
		super(editor, ID, TITLE);
		
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