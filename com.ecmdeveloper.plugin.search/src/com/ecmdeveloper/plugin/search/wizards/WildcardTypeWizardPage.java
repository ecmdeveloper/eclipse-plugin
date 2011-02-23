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

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.constants.WildcardType;

/**
 * @author ricardo.belfor
 *
 */
public class WildcardTypeWizardPage extends WizardPage {

	private static final String TITLE = "Wildcard Type";
	private static final String DESCRIPTION = "Select the wildcard type.";
	private Map<Button, WildcardType> buttonWildcardTypeMap = new HashMap<Button, WildcardType>();
	private WildcardType wildcardType;
	private IQueryField field;

	protected WildcardTypeWizardPage() {
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
		createWildcardTypeButtons(container);
	}

	private void createWildcardTypeButtons(Composite container) {
		for (WildcardType wildcardType : WildcardType.values()) {
			Button button = createButton(container, wildcardType, wildcardType
					.equals(this.wildcardType));
			buttonWildcardTypeMap.put(button, wildcardType);
		}
	}

	private Button createButton(Composite container, WildcardType wildcardType,
			boolean selection) {

		Button button = new Button(container, SWT.RADIO);
		button.setText(wildcardType.toString());
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

	protected void updateSelection(Object source) {
		wildcardType = buttonWildcardTypeMap.get(source);
		System.out.println(wildcardType.toString());
		getWizard().getContainer().updateButtons();
	}
	
	private GridData getFullRowGridData() {
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		return gd;
	}

	public WildcardType getWildcardType() {
		return wildcardType;
	}

	public void setWildcardType(WildcardType wildcardType) {
		this.wildcardType = wildcardType;
	}
}
