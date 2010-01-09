package com.ecmdeveloper.plugin.properties.editors.details;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ecmdeveloper.plugin.properties.model.Property;

public class BooleanDetailsPage extends BaseDetailsPage {

	private Button trueButton;
	private Button falseButton;

	@Override
	protected void createClientContent(Composite client) {
		super.createClientContent(client);
		FormToolkit toolkit = form.getToolkit();
		createTrueButton(client, toolkit);
		createFalseButton(client, toolkit);
	}

	private void createTrueButton(Composite client, FormToolkit toolkit) {
		trueButton = toolkit.createButton(client, "True", SWT.RADIO );
		trueButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
			}
		} );
	}

	private void createFalseButton(Composite client, FormToolkit toolkit) {
		falseButton = toolkit.createButton(client, "False", SWT.RADIO );
		falseButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
			}
		} );
	}

	@Override
	protected int getNumClientColumns() {
		return 1;
	}

	@Override
	protected Object getValue() {
		Boolean value = new Boolean( trueButton.getSelection() );
		return value;
	}

	@Override
	protected void handleEmptyValueButton(boolean selected) {
		trueButton.setEnabled( ! selected ); 
		falseButton.setEnabled( ! selected ); 
	}

	@Override
	protected void propertyChanged(Property property) {
		
		Boolean value = (Boolean) property.getValue();
		if ( value == null ) {
			trueButton.setSelection(false);
			falseButton.setSelection(false);
		} else if ( value ) {
			trueButton.setSelection(true);
			falseButton.setSelection(false);
		} else {
			trueButton.setSelection(false);
			falseButton.setSelection(true);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void setFocus() {
		trueButton.setFocus();
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}
}
