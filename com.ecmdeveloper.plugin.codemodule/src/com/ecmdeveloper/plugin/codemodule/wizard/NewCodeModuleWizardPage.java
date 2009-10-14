package com.ecmdeveloper.plugin.codemodule.wizard;

import java.util.Iterator;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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

import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;

public class NewCodeModuleWizardPage extends WizardPage {

	private Button connectButton;
	private ComboViewer objectStoresCombo;
	
	public NewCodeModuleWizardPage() {
		super( "newCodeModule" );
		setTitle("New Code Module");
		setDescription("Select the new Code Module location." );
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NULL );
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		
		Label selectObjectStoreLabel = new Label(container, SWT.NULL);
		selectObjectStoreLabel.setText("Select Object Store:");

		createObjectStoresCombo( container );
		createConnectButton( container );
		
	}

	private void createObjectStoresCombo(Composite container ) {
		
		objectStoresCombo = new ComboViewer( container, SWT.VERTICAL | SWT.DROP_DOWN
				| SWT.BORDER | SWT.READ_ONLY);
		objectStoresCombo.setContentProvider( new ArrayContentProvider() );
		objectStoresCombo.setLabelProvider( new ObjectStoreItemLabelProvider() );
		objectStoresCombo.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
				ISelection selection = event.getSelection();

				if (!(selection instanceof IStructuredSelection))
					return;

				Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
				if ( iterator.hasNext() ) {
//					wizard.setObjectStore( (ObjectStore) iterator.next() );
//					connectButton.setEnabled( ! wizard.getObjectStore().isConnected() );
//					updateCodeModulesCombo( false );
				}
			} 
		});
		
//		objectStoresCombo.setInput( wizard.getObjectStores() );
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		objectStoresCombo.getCombo().setLayoutData( gd );
	}

	private void createConnectButton( Composite parent ) {
		
		connectButton = new Button( parent, SWT.PUSH);
		connectButton.setText( "Connect..." );
		connectButton.setEnabled( false );
		connectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				updateCodeModulesCombo( true );
			}
		});

		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		connectButton.setLayoutData(gd);
	}
}
