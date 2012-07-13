package com.ecmdeveloper.plugin.properties.editors;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.ecmdeveloper.plugin.core.model.IClassDescription;
import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.util.IconFiles;

public class PropertiesInputForm extends FormPage {

	private static final String TITLE = "Properties";
	private static final String ID = "propertiesInputPage";
	private static final String PROPERTIES_EDITOR_TITLE = "Properties Editor";
	
	private IClassDescription classDescription;
	private PropertiesInputBlock propertiesInputBlock;

	public PropertiesInputForm(FormEditor editor, IClassDescription classDescription) {
		super(editor, ID, TITLE);
		
		this.classDescription = classDescription;
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		setFormTitle(managedForm);
		propertiesInputBlock = new PropertiesInputBlock(this, classDescription.getName() );
		propertiesInputBlock.createContent(managedForm);
	}

	private void setFormTitle(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		managedForm.getToolkit().decorateFormHeading( form.getForm() );
		form.setText( PROPERTIES_EDITOR_TITLE );
		form.setImage( Activator.getImage( IconFiles.HEADER_IMAGE ) );
	}	

	public void refreshFormContent() {
		IEditorInput editorInput = getEditor().getEditorInput();
		propertiesInputBlock.setInput( editorInput );
	}
}