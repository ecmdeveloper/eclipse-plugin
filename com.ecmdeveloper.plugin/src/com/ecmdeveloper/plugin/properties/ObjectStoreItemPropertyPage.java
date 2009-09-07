package com.ecmdeveloper.plugin.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import com.ecmdeveloper.plugin.model.IObjectStoreItem;

public class ObjectStoreItemPropertyPage extends PropertyPage {

	@Override
	protected Control createContents(Composite parent) {
		
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		panel.setLayout(layout);
		Label label = new Label(panel, SWT.NONE);
		label.setLayoutData(new GridData());
		
//		String name = getElement().getClass().getName();
		label.setText( "Name: " + ((IObjectStoreItem) getElement()).getName() );		

		Label label2 = new Label(panel, SWT.NONE);
		label2.setLayoutData(new GridData());
		
//		String name = getElement().getClass().getName();
		label2.setText( "Type: " + getElement().toString() );		

		return panel;
	}
}
