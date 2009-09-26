package com.ecmdeveloper.plugin.wizard;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ecmdeveloper.plugin.model.CodeModule;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;

public class ConfigureConnectionWizardPage extends WizardPage {
	private Text urlField;
	private Text usernameField;
	private Text passwordField;
	private Button connectButton;

	public ConfigureConnectionWizardPage() {
		super("configureConnection");
		setTitle("Configure Connection");
		setDescription("Configure the connection to the Content Engine");
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);
		setControl(container);


		addLabel(container, "URL:", true);
		urlField = addTextField(container);

		addLabel(container, "Name:", true);
		usernameField = addTextField(container);

		addLabel(container, "Password:", true);
		passwordField = addTextField(container);
		passwordField.setEchoChar('*');


		createConnectButton(container);
	}


	private void updateConnectButton() {
		
		boolean enabled = false;
		
		enabled = urlField.getText() != null
				&& !urlField.getText().isEmpty()
				&& usernameField.getText() != null
				&& !usernameField.getText().isEmpty();

		connectButton.setEnabled( enabled );
	}
	
	private void createConnectButton(Composite container) {
		connectButton = new Button(container, SWT.NONE);
		connectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performConnect();
			}
		});
		connectButton.setText("Connect");
		updateConnectButton();
	}


	protected void performConnect() {
		((ImportObjectStoreWizard) getWizard()).connect();
	}

	private Text addTextField(Composite container) {
		Text textField = new Text(container, SWT.BORDER);
		textField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateConnectButton();				
				updatePageComplete();
			}
		});
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return textField;
	}

	private void addLabel(Composite container, String text, boolean enabled) {
		final Label label_1 = new Label(container, SWT.NONE);
		final GridData gridData_1 = new GridData(GridData.BEGINNING);
		label_1.setLayoutData(gridData_1);
		label_1.setText(text);
		label_1.setEnabled(enabled);
	}

	protected void updatePageComplete() {

		setPageComplete(false);

		if (getURL() == null) {
			setMessage(null);
			setErrorMessage("Please enter a connection url");
			return;
		}

		if (getUsername() == null) {
			setMessage(null);
			setErrorMessage("Please enter an user name");
			return;
		}

		// setPageComplete(true);
		setMessage(null);
		setErrorMessage(null);
	}

	@Override
	public boolean canFlipToNextPage() {
		return ((ImportObjectStoreWizard) getWizard()).isConnected();
	}

	public String getURL() {
		String text = urlField.getText().trim();
		if (text.length() == 0) {
			return null;
		}

		return text;
	}

	public String getUsername() {
		String text = usernameField.getText().trim();
		if (text.length() == 0) {
			return null;
		}

		return text;
	}

	public String getPassword() {
		String text = passwordField.getText().trim();
		if (text.length() == 0) {
			return null;
		}

		return text;
	}
}
