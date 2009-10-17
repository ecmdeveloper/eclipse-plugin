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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ecmdeveloper.plugin.codemodule.util.Messages;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;

public class NewCodeModuleWizardPage extends WizardPage {

	private static final String PAGE_NAME = Messages.NewCodeModuleWizardPage_PageName;
	private static final String DESCRIPTION = Messages.NewCodeModuleWizardPage_Description;
	private static final String TITLE = Messages.NewCodeModuleWizardPage_Title;
	private static final String CONNECT_LABEL = Messages.NewCodeModuleWizardPage_ConnectLabel;
	private static final String SELECT_OBJECT_STORE_LABEL = Messages.NewCodeModuleWizardPage_SelectObjectStoreLabel;
	private static final String NAME_LABEL = Messages.NewCodeModuleWizardPage_NameLabel;
	
	private Button connectButton;
	private ComboViewer objectStoresCombo;
	private NewCodeModuleWizard wizard;
	private Text nameText;
	
	public NewCodeModuleWizardPage() {
		super( PAGE_NAME );
		setTitle( TITLE );
		setDescription(DESCRIPTION );
	}

	@Override
	public void createControl(Composite parent) {

		wizard = (NewCodeModuleWizard) getWizard();

		Composite container = new Composite(parent, SWT.NULL );
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		
		Label selectObjectStoreLabel = new Label(container, SWT.NULL);
		selectObjectStoreLabel.setText(SELECT_OBJECT_STORE_LABEL);

		createObjectStoresCombo( container );
		createConnectButton( container );
		createNameText( container );
	}

	private void createNameText(Composite container) {
		final Label nameLabel = new Label( container, SWT.NONE );
		nameLabel.setLayoutData( new GridData(GridData.BEGINNING) );
		nameLabel.setText(NAME_LABEL);
		
		nameText = new Text(container, SWT.BORDER);
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				wizard.setName( nameText.getText() );
			}
		});
		nameText.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
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
					wizard.setObjectStore( (ObjectStore) iterator.next() );
					connectButton.setEnabled( ! wizard.getObjectStore().isConnected() );
				}
			} 
		});
		
		objectStoresCombo.setInput( wizard.getObjectStores() );
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		objectStoresCombo.getCombo().setLayoutData( gd );
	}

	private void createConnectButton( Composite parent ) {
		
		connectButton = new Button( parent, SWT.PUSH);
		connectButton.setText( CONNECT_LABEL );
		connectButton.setEnabled( false );
		connectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				ObjectStore objectStore = wizard.getObjectStore();
				if ( ! objectStore.isConnected() ) {
					wizard.connectObjectStore();
					connectButton.setEnabled( ! wizard.getObjectStore().isConnected() );
				}
			}
		});

		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		connectButton.setLayoutData(gd);
	}
}
