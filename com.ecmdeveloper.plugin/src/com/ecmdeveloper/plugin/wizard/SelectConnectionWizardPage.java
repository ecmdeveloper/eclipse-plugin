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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;

public class SelectConnectionWizardPage extends WizardPage {

	private Collection<ContentEngineConnection> connections;
	private Button newConnectionButton;
	private Button existingConnectionButton;
	private ComboViewer connectionsCombo;
	private Button connectButton;

	protected SelectConnectionWizardPage(Collection<ContentEngineConnection> connections ) {
		super("selectConnection");
		this.connections = connections;
		setTitle("Select Connection");
		setDescription("Select the connection to the Content Engine");
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
				
		createNewConnectionButton(container);
		createExistingConnectionButton(container, !connections.isEmpty());
		addLabel(container, "Connection:", !connections.isEmpty());
		createConnectionsCombo(container, connections);
		createConnectButton( container );
	}

	public boolean isNewConnection() {
		return newConnectionButton.getSelection();
	}

	public boolean isExistingConnection() {
		return existingConnectionButton.getSelection();
	}

	public ContentEngineConnection getSelectedContentEngineConnection() {
		ISelection selection = connectionsCombo.getSelection();
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		if ( iterator.hasNext() ) {
			return (ContentEngineConnection) iterator.next();
		} else {
			return null;
		}
	}

	private void createNewConnectionButton(Composite container) {

		newConnectionButton = new Button(container, SWT.RADIO);
		newConnectionButton.setText("Configure new connection");
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		newConnectionButton.setLayoutData(gd);
		newConnectionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateConnectButton();
				updatePageComplete();			}
		});
	}
	
	private void createExistingConnectionButton(Composite container,
			boolean enabled) {

		existingConnectionButton = new Button(container, SWT.RADIO);
		existingConnectionButton.setText("Use existing connection");
		existingConnectionButton.setEnabled(enabled);

		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		existingConnectionButton.setLayoutData(gd);
		
		existingConnectionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateConnectButton();
				updatePageComplete();
			}
		});
	}

	private void addLabel(Composite container, String text, boolean enabled) {
		final Label label_1 = new Label(container, SWT.NONE);
		final GridData gridData_1 = new GridData(GridData.BEGINNING);
		label_1.setLayoutData(gridData_1);
		label_1.setText(text);
		label_1.setEnabled(enabled);
	}

	private void createConnectionsCombo(Composite container,
			Collection<ContentEngineConnection> connections) {

		connectionsCombo = new ComboViewer(container, SWT.VERTICAL
				| SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		connectionsCombo.setContentProvider(new ArrayContentProvider());
		connectionsCombo.setLabelProvider(new LabelProvider());
		connectionsCombo.setInput(connections);
		connectionsCombo.getCombo().setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		connectionsCombo.getCombo().setEnabled(!connections.isEmpty());
		
		connectionsCombo
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateConnectButton();
					}
				});
		
	}

	private void updateConnectButton() {
		
		boolean enabled = false;
		
		if ( existingConnectionButton.getSelection() ) {

			ISelection selection = connectionsCombo.getSelection();
			Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
			if ( iterator.hasNext() ) {
				ContentEngineConnection connection = (ContentEngineConnection) iterator.next();
				enabled = ! connection.isConnected();
			}
			
		} 
		
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

	protected void updatePageComplete() {
		
		if ( isNewConnection() ) {
			setPageComplete( true );
			return;
		} else if ( isExistingConnection() ) {
			ContentEngineConnection connection = getSelectedContentEngineConnection();
			setPageComplete( connection != null && connection.isConnected() );
			return;
		}
		
		setPageComplete( false );
	}

	protected void performConnect() {
		ContentEngineConnection connection = getSelectedContentEngineConnection();
		
		if ( connection != null ) {
			((ImportObjectStoreWizard) getWizard()).connect( connection );
		} else {
			throw new UnsupportedOperationException( "Connection is null" );
		}
		updatePageComplete();
	}
}
