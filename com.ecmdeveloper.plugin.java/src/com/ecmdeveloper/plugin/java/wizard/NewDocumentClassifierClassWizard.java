package com.ecmdeveloper.plugin.java.wizard;

public class NewDocumentClassifierClassWizard extends NewClassWizard {

	@Override
	public void addPages() {
		newClassTypePage = new NewDocumentClassifierTypePage(); 
		addPage( newClassTypePage );
		newClassTypePage.init( getSelection() );
	}
	
}
