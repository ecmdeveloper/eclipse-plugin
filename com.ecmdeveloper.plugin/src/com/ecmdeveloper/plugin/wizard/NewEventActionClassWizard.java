/**
 * 
 */
package com.ecmdeveloper.plugin.wizard;


/**
 * @author Ricardo.Belfor
 *
 */
public class NewEventActionClassWizard extends NewClassWizard {
	
	@Override
	public void addPages() {
	
		newClassTypePage = new NewEventActionClassTypePage(); 
		addPage( newClassTypePage );
		newClassTypePage.init( getSelection() );
	}
}
