/**
 * 
 */
package com.ecmdeveloper.plugin.java.wizard;


/**
 * @author Ricardo.Belfor
 *
 */
public class NewDocumentLifecycleActionHandlerWizard extends NewClassWizard {

	@Override
	public void addPages() {
		newClassTypePage = new NewDocumentLifecycleActionHandlerTypePage(); 
		addPage( newClassTypePage );
		newClassTypePage.init( getSelection() );
	}
}
