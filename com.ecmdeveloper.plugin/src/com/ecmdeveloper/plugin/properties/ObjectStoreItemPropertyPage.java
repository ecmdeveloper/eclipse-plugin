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
		layout.numColumns = 2;
		panel.setLayout(layout);
		
		createPageRow(panel, "Name:", ((IObjectStoreItem) getElement()).getName() );		
		createPageRow(panel, "Type:", getElement().getClass().getSimpleName() );		

		return panel;
	}

	private void createPageRow(Composite panel, String name, String value) {
		
		Label namelabel = new Label(panel, SWT.NONE);
		namelabel.setLayoutData( new GridData(GridData.BEGINNING) );
		namelabel.setText( name );
		
		Label nameLabelValue = new Label(panel, SWT.NONE);
		nameLabelValue.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
		nameLabelValue.setText( value );
	}
}
