package com.ecmdeveloper.plugin.search.wizards;

import org.eclipse.swt.widgets.Text;

public class IntegerValueWizardPage extends SimpleValueWizardPage {

	private static final String INVALID_INTEGER_VALUE = "Invalid integer value";
	private static final String TITLE = "Integer value";
	private static final String DESCRIPTION = "Enter a integer value.";

	protected Text text;
	
	protected IntegerValueWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	protected void textModified(String textValue) {
		try {
			setValue( parseInput(textValue) );
			setErrorMessage(null);
			setDirty();
		} catch (NumberFormatException exception) {
			setErrorMessage(INVALID_INTEGER_VALUE);
		}
	}

	private Integer parseInput(String textValue) {
		if ( textValue.length() != 0 ) {
			return Integer.parseInt( textValue );
		} else {
			return null;
		}
	}
}
