/**
 * Copyright 2011, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.search.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author ricardo.belfor
 *
 */
public class NullTestWizardPage extends WizardPage {

	private static final String TITLE = "Null Test";

	private static final String DESCRIPTION = "Select the null test.";

	private Button isNullButton;
	private boolean negated = false;
	
	protected NullTestWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);

		isNullButton = createButton(container, "Is Null", !negated);
		createButton(container, "Is Not Null", negated);
		
		isNullButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				negated = !((Button)e.getSource()).getSelection();
			}
		});
	}

	private Button createButton(Composite container, String label, boolean selection) {

		Button button = new Button(container, SWT.RADIO);
		button.setText(label);
		button.setLayoutData(getFullRowGridData());
		button.setSelection(selection);

		return button;
	}
	
	private GridData getFullRowGridData() {
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		return gd;
	}

	public boolean isNegated() {
		return negated;
	}

	public void setNegated(boolean negated) {
		this.negated = negated;
	}
}
