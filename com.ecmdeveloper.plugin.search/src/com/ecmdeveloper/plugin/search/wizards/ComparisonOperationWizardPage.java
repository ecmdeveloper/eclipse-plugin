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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.search.model.ComparisonOperation;
import com.ecmdeveloper.plugin.search.model.IQueryField;

/**
 * @author ricardo.belfor
 * 
 */
public class ComparisonOperationWizardPage extends WizardPage {

	private static final String TITLE = "Comparison Operation";
	private static final String DESCRIPTION = "Select the comparison operator.";
	private Map<Button, ComparisonOperation> buttonComparisonOperationMap = new HashMap<Button, ComparisonOperation>();
	private ComparisonOperation comparisonOperation;
	private IQueryField field;

	protected ComparisonOperationWizardPage() {
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
		createComparisonOperationButtons(container);
	}

	private void createComparisonOperationButtons(Composite container) {
		for (ComparisonOperation comparisonOperation : ComparisonOperation.values()) {
			Button button = createButton(container, comparisonOperation, comparisonOperation
					.equals(this.comparisonOperation));
			buttonComparisonOperationMap.put(button, comparisonOperation);
		}
	}

	private Button createButton(Composite container, ComparisonOperation comparisonOperation,
			boolean selection) {

		Button button = new Button(container, SWT.RADIO);
		button.setText(comparisonOperation.toString());
		button.setLayoutData(getFullRowGridData());
		button.setSelection(selection);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSelection(e.getSource());
			}

		});

		return button;
	}

	public void setComparisonOperation(ComparisonOperation comparisonOperation) {
		this.comparisonOperation = comparisonOperation;
	}

	public ComparisonOperation getComparisonOperation() {
		return comparisonOperation;
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if ( visible ) {
			
			for ( Button button : buttonComparisonOperationMap.keySet() ) {
				if ( field == null ) {
					button.setEnabled(false);
				} else {
					ComparisonOperation comparisonOperation = buttonComparisonOperationMap.get(button);
					button.setEnabled( !comparisonOperation.isRequiresOrderable() || field.isOrderable() );
				}
			}
		}
	}
	
	protected void updateSelection(Object source) {
		comparisonOperation = buttonComparisonOperationMap.get(source);
		getWizard().getContainer().updateButtons();
	}

	private GridData getFullRowGridData() {
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		return gd;
	}

	public void setField(IQueryField field) {
		this.field = field;
	}
}
