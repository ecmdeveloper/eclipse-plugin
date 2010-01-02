package com.ecmdeveloper.plugin.editors.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.classes.model.PropertyDescription;
import com.ecmdeveloper.plugin.model.ObjectStoreItem;
import com.ecmdeveloper.plugin.properties.input.IPropertyInput;

public class PropertiesInputForm extends FormPage {

	private ClassDescription classDescription;
	private Map<String,IPropertyInput> propertyInputMap;
	private ObjectStoreItem objectStoreItem;
	private PropertiesInputBlock propertiesInputBlock;

	public PropertiesInputForm(FormEditor editor, ClassDescription classDescription) {
		super(editor, "propertiesInputPage", "Properties");
		this.classDescription = classDescription;
		propertyInputMap = new HashMap<String, IPropertyInput>();
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		propertiesInputBlock = new PropertiesInputBlock(this);
		propertiesInputBlock.createContent(managedForm);
	}	

	public void refreshFormContent(ObjectStoreItem objectStoreItem ) {
		
		IEditorInput editorInput = getEditor().getEditorInput();
		propertiesInputBlock.setInput( editorInput );
//		this.objectStoreItem = objectStoreItem;
//	
//		for (PropertyDescription propertyDescription : classDescription.getPropertyDescriptions() ) {
//			IPropertyInput propertyInput = getPropertyInput(propertyDescription);
//			propertyInput.setValue( objectStoreItem.getValue( propertyDescription.getName() ) );
//		}
	}
}