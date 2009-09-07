package com.ecmdeveloper.plugin.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SelectCodeModuleWizardPage extends WizardPage {

	public SelectCodeModuleWizardPage() {
		super( "selectCodeModule" );
		setTitle("Select Code Module");
		setDescription("Select the Code Module to import" );
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL );
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		setControl(container);
		
		Label selectObjectStoreLabel = new Label(container, SWT.NULL);
		selectObjectStoreLabel.setText("Select Object Store:");

		final Combo combo1 = new Combo(container, SWT.VERTICAL | SWT.DROP_DOWN
				| SWT.BORDER | SWT.READ_ONLY);
		
		Label selectCodeModuleLabel = new Label(container, SWT.NULL);
		selectCodeModuleLabel.setText("Select Code Module:");
		
		final Combo combo2 = new Combo(container, SWT.VERTICAL | SWT.BORDER
				| SWT.READ_ONLY);

		combo1.add("Tea");
		combo1.add("Coffee");
		combo1.add("Cold drink");
		combo1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (combo1.getText().equals("Cold drink")) {
					String[] drinks = new String[] { "Pepsi", "CocaCola",
							"Miranda", "Sprite", "ThumbsUp" };
					combo2.setItems(drinks);
					combo2.setEnabled(true);
					combo2.add("-Select-");
					combo2.setText("-Select-");
				} else if (combo1.getText().equals("Tea")) {
					combo2.add("Not Applicable");
					combo2.setText("Not Applicable");
				} else {
					combo2.add("Not Applicable");
					combo2.setText("Not Applicable");
				}
			}
		});
		
	}
}
