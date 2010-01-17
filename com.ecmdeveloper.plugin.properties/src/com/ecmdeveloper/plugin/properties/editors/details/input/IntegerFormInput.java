package com.ecmdeveloper.plugin.properties.editors.details.input;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

public class IntegerFormInput extends BaseFormInput {

	private static final String INVALID_INTEGER_VALUE = "Invalid integer value";
	private static final String INVALID_INTEGER_MESSAGE_KEY = "invalidInteger";
	
	public IntegerFormInput(Composite client, IManagedForm form )
	{
		super(client,form);
	}
	
	@Override
	protected void textModified(String textValue) {
		try {
			Integer integerValue = parseInput(textValue);
			messageManager.removeMessage(INVALID_INTEGER_MESSAGE_KEY, text );
			valueModified(integerValue);
			
		} catch (NumberFormatException exception) {
			messageManager.addMessage(INVALID_INTEGER_MESSAGE_KEY, INVALID_INTEGER_VALUE,
					null, IMessageProvider.ERROR, text );
		}
	}

	private Integer parseInput(String textValue) {
		if ( textValue.length() != 0 ) {
			return Integer.parseInt( textValue );
		} else {
			return null;
		}
	}
	
	public void setValue(Integer value ) {
		if ( value != null ) {
			text.setText( value.toString() );
		} else {
			text.setText("");
		}
	}
	
	public Integer getValue() {
		try {
			return parseInput( text.getText().trim() );
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
