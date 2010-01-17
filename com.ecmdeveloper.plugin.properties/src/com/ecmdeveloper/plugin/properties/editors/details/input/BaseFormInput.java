package com.ecmdeveloper.plugin.properties.editors.details.input;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class BaseFormInput {

	protected Text text;
	protected IMessageManager messageManager;
	
	public BaseFormInput(Composite client, IManagedForm form ) {
		messageManager = form.getMessageManager();
		createText(client, form.getToolkit() );
	}

	private void createText(Composite client, FormToolkit toolkit) {
		text = toolkit.createText(client, "", SWT.BORDER ); 
		text.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String textValue = text.getText().trim();
				textModified(textValue);
			}
		} );
	}

	protected abstract void textModified(String textValue);
	
	protected void valueModified(Object value) {
		
	}

	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
	}

	public void setFocus() {
		text.setFocus();
	}
}
