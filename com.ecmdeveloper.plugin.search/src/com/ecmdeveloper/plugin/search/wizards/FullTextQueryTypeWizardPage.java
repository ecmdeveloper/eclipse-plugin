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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ecmdeveloper.plugin.search.model.constants.FullTextQueryType;

/**
 * @author ricardo.belfor
 *
 */
public class FullTextQueryTypeWizardPage extends WizardPage {

	private static final String TITLE = "Full Text Query Type";
	private static final String DESCRIPTION = "Select the full text query type.";

	private Button containsButton;
	private Button freeTextButton;
	private FullTextQueryType fullTextQueryType;
	private boolean cbrEnabled;
	
	protected FullTextQueryTypeWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION );
	}

	@Override
	public void createControl(Composite parent) {
		Composite client = new Composite(parent, SWT.NONE );
		client.setLayout( new RowLayout(SWT.VERTICAL) );
		setControl(client);

		containsButton = createButton(client, FullTextQueryType.CONTAINS );
		freeTextButton = createButton(client, FullTextQueryType.FREETEXT );
		if ( !cbrEnabled ) {
			Label label = new Label(client, SWT.WRAP );
			label.setText("\n\n\nFull text searching is not possible because the query does not contain any CBR enabled tables." );
		}
	}

	private Button createButton(Composite client, FullTextQueryType buttonQueryType) {
		Button button = new Button(client, SWT.RADIO);
		button.setText( buttonQueryType.toString() );
		button.setSelection( buttonQueryType.equals( fullTextQueryType) );
		button.setEnabled(cbrEnabled);
		button.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSelection();
			}} );
		return button;
	}

	protected void updateSelection() {
		if ( containsButton.getSelection() ) {
			fullTextQueryType = FullTextQueryType.CONTAINS;
		} else if ( freeTextButton.getSelection() ) {
			fullTextQueryType = FullTextQueryType.FREETEXT;
		} else {
			fullTextQueryType = null;
		}
		
		setPageComplete(fullTextQueryType != null );
		getWizard().getContainer().updateButtons();
	}

	public void setFullTextQueryType( FullTextQueryType fullTextQueryType ) {
		this.fullTextQueryType = fullTextQueryType;
	}
	
	public FullTextQueryType getFullTextQueryType() {
		return fullTextQueryType;
	}

	public boolean isCbrEnabled() {
		return cbrEnabled;
	}

	public void setCbrEnabled(boolean cbrEnabled) {
		this.cbrEnabled = cbrEnabled;
	}
}
