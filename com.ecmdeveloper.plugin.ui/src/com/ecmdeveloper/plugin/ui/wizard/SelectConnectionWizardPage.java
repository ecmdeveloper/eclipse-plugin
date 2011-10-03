/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
package com.ecmdeveloper.plugin.ui.wizard;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ecmdeveloper.plugin.core.model.IConnection;

public abstract class SelectConnectionWizardPage extends WizardPage {

	private Collection<IConnection> connections;
	private Button newConnectionButton;
	private Button existingConnectionButton;
	private ComboViewer connectionsCombo;
	private Button connectButton;

	protected SelectConnectionWizardPage(Collection<IConnection> connections ) {
		super("selectConnection");
		this.connections = connections;
		setTitle("Select Connection");
		setDescription("Select the connection to the server.");
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
		updatePageComplete();
	}

	public boolean isNewConnection() {
		return newConnectionButton.getSelection();
	}

	public boolean isExistingConnection() {
		return existingConnectionButton.getSelection();
	}

	public IConnection getSelectedContentEngineConnection() {
		ISelection selection = connectionsCombo.getSelection();
		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		if ( iterator.hasNext() ) {
			return (IConnection) iterator.next();
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
			Collection<IConnection> connections) {

		connectionsCombo = new ComboViewer(container, SWT.VERTICAL
				| SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		connectionsCombo.setContentProvider(new ArrayContentProvider());
		connectionsCombo.setLabelProvider(new LabelProvider());
		connectionsCombo.setInput(connections);
		connectionsCombo.getCombo().setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL));
		connectionsCombo.getCombo().setEnabled(!connections.isEmpty());
		connectionsCombo.setFilters( new ViewerFilter[] { getConnectionsFilter() } );
		connectionsCombo
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						updateConnectButton();
						updatePageComplete();					}
				});
		
	}

	protected abstract ViewerFilter getConnectionsFilter();

	private void updateConnectButton() {
		
		boolean enabled = false;
		
		if ( existingConnectionButton.getSelection() ) {

			ISelection selection = connectionsCombo.getSelection();
			Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
			if ( iterator.hasNext() ) {
				IConnection connection = (IConnection) iterator.next();
				enabled = ! connection.isConnected();
				((AbstractImportObjectStoreWizard) getWizard()).setConnection( connection );
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
			IConnection connection = getSelectedContentEngineConnection();
			setPageComplete( connection != null && connection.isConnected() );
			return;
		}
		
		setPageComplete( false );
	}

	protected void performConnect() {
		IConnection connection = getSelectedContentEngineConnection();
		
		if ( connection != null ) {
			((AbstractImportObjectStoreWizard) getWizard()).connect( connection );
		} else {
			throw new UnsupportedOperationException( "Connection is null" );
		}
		updatePageComplete();
		updateConnectButton();
	}
}
