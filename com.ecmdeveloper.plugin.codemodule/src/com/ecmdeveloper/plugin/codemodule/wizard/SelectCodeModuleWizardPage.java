package com.ecmdeveloper.plugin.codemodule.wizard;

import java.util.ArrayList;
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

import com.ecmdeveloper.plugin.core.model.ICodeModule;
import com.ecmdeveloper.plugin.core.model.IObjectStore;
import com.ecmdeveloper.plugin.ui.views.ObjectStoreItemLabelProvider;

public class SelectCodeModuleWizardPage extends WizardPage {

	private Button connectButton;
	private ComboViewer objectStoresCombo;
	private ComboViewer codeModulesCombo;
	private ImportCodeModuleWizard wizard;
	
	public SelectCodeModuleWizardPage() {
		super( "selectCodeModule" );
		setTitle("Select Code Module");
		setDescription("Select the Code Module to import" );
	}

	@Override
	public void createControl(Composite parent) {

		wizard = (ImportCodeModuleWizard) getWizard();

		Composite container = new Composite(parent, SWT.NULL );
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		
		Label selectObjectStoreLabel = new Label(container, SWT.NULL);
		selectObjectStoreLabel.setText("Select Object Store:");

		createObjectStoresCombo( container );
		createConnectButton( container );
		
		Label selectCodeModuleLabel = new Label(container, SWT.NULL);
		selectCodeModuleLabel.setText("Select Code Module:");
		
		createCodeModulesCombo(container);		
	}

	private void createCodeModulesCombo(Composite container) {

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		codeModulesCombo = new ComboViewer(container, SWT.VERTICAL | SWT.BORDER
				| SWT.READ_ONLY);

		codeModulesCombo.getCombo().setLayoutData( gd );
		codeModulesCombo.setContentProvider( new ArrayContentProvider() );
		codeModulesCombo.getCombo().setEnabled( false );
		
		codeModulesCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				ICodeModule codeModule = (ICodeModule) element;
				return codeModule.getName();
			}
		});

		codeModulesCombo.addSelectionChangedListener( new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
				ISelection selection = event.getSelection();

				if (!(selection instanceof IStructuredSelection))
					return;

				Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
				if ( iterator.hasNext() ) {
					wizard.setCodeModule( (ICodeModule) iterator.next() );
				}
			} 
		});
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
					wizard.setObjectStore( (IObjectStore) iterator.next() );
					connectButton.setEnabled( ! wizard.getObjectStore().isConnected() );
					updateCodeModulesCombo( false );
				}
			} 
		});
		
		objectStoresCombo.setInput( wizard.getObjectStores() );
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		objectStoresCombo.getCombo().setLayoutData( gd );
//		objectStoresCombo.setFilters( new ViewerFilter() {
//
//			@Override
//			public boolean select(Viewer viewer, Object parentElement, Object element) {
//				if ( element instanceof IObjectStore ) {
//					((IObjectStore) element).getConnection()
//				}
//				return false;
//			} } );
	}

	private void createConnectButton( Composite parent ) {
		
		connectButton = new Button( parent, SWT.PUSH);
		connectButton.setText( "Connect..." );
		connectButton.setEnabled( false );
		connectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateCodeModulesCombo( true );
			}
		});

		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		connectButton.setLayoutData(gd);
	}

	protected void updateCodeModulesCombo( boolean connect ) {

		IObjectStore objectStore = wizard.getObjectStore();
		setErrorMessage(null);
		
		if ( objectStore != null ) {
			
			if ( ! objectStore.isConnected() ) {
				if ( connect ) {
					wizard.connectObjectStore();
					connectButton.setEnabled( ! wizard.getObjectStore().isConnected() );
				} else {
					codeModulesCombo.getCombo().setEnabled( false );
					codeModulesCombo.setInput( new ArrayList<ICodeModule>() );
					wizard.setCodeModule(null);
					return;
				}
			} 
			
			if ( objectStore.isConnected() ) {
				Collection<ICodeModule> codeModules = wizard.getCodeModules();
				codeModulesCombo.setInput( codeModules );
				codeModulesCombo.getCombo().setEnabled( true );
				wizard.setCodeModule(null);
	
				if ( codeModules.isEmpty() ) {
					setErrorMessage( "There are no more Code Modules left to import from this Object Store." );
				}
			}
		} 
		else 
		{
			codeModulesCombo.getCombo().setEnabled( false );
			codeModulesCombo.setInput( new ArrayList<ICodeModule>() );
			wizard.setCodeModule(null);
		}
	}
}
