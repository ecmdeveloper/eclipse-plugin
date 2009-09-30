/**
 * 
 */
package com.ecmdeveloper.plugin.wizard;


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
