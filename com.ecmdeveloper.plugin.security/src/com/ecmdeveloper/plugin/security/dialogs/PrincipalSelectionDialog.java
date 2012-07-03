/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.security.dialogs;

import java.util.ArrayList;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.editor.SecurityLabelProvider;
import com.ecmdeveloper.plugin.security.mock.PrincipalMock;
import com.ecmdeveloper.plugin.security.mock.SpecialAccountPrincipalMock;

/**
 * @author ricardo.belfor
 *
 */
public class PrincipalSelectionDialog extends FilteredItemsSelectionDialog {

	private static final String SHOW_SPECIAL_ACCOUNTS_LABEL = "Show Special Accounts";
	private static final String SHOW_GROUPS_LABEL = "Show Groups";
	private static final String SHOW_USERS_LABEL = "Show Users";
	private static final String TITLE = "Principal Selection";
	private static final String SEPARATOR_LABEL = "Previous matches";
	private static final String NAME_TAG = "name";
	private static final String DIALOG_SETTINGS = "PrincipalSelectionDialogSettings";
	protected static final String TYPE_TAG = "type";		
	private boolean showSpecialAccounts = true;
	private boolean showGroups = false;
	private boolean showUsers = false;
	
	private static ArrayList<IPrincipal> resources = new ArrayList<IPrincipal>();
	private SecurityLabelProvider labelProvider;
	private ArrayList<IPrincipal> initialSelection = new ArrayList<IPrincipal>();

	static {
		generateRescourcesTestCases('A', 'C', 8, ""); //$NON-NLS-1$
		generateRescourcesTestCases('a', 'c', 4, ""); //$NON-NLS-1$
	}

	private static void generateRescourcesTestCases(char startChar, char endChar, int length, String resource){
		for (char ch = startChar; ch <= endChar; ch++) {
			String res = resource + String.valueOf(ch);
			if (length == res.length()) 
				resources.add( new PrincipalMock(res) );
			else if ((res.trim().length() % 2) == 0)
				generateRescourcesTestCases(Character.toUpperCase((char)(startChar + 1)), Character.toUpperCase((char)(endChar + 1)), length, res);
			else 
				generateRescourcesTestCases(Character.toLowerCase((char)(startChar + 1)), Character.toLowerCase((char)(endChar + 1)), length, res);
		}
	}

	public PrincipalSelectionDialog(Shell shell, IPrincipal initialPrincipal ) {
		super(shell,true);
		setTitle(TITLE);
		labelProvider = new SecurityLabelProvider();
		
		setListLabelProvider( labelProvider );
		setDetailsLabelProvider(labelProvider);
		setSeparatorLabel(SEPARATOR_LABEL);
		setInitialSelection(initialPrincipal);
		setSelectionHistory(getPrincipalSelectionHistory()  );
	}

	private void setInitialSelection(IPrincipal initialPrincipal) {
		
		initialSelection.add( new SpecialAccountPrincipalMock("#CREATOR-OWNER") );
		initialSelection.add( new SpecialAccountPrincipalMock("#AUTHENTICATED-USERS" ) );
		
		if ( initialPrincipal != null ) {
			initialSelection.add(initialPrincipal);
			if ( initialPrincipal.isGroup() ) {
				showSpecialAccounts = false;
				showGroups = true;
			} else if ( initialPrincipal.isUser() ) {
				showSpecialAccounts = false;
				showUsers = true;
			}
		}
	}

	private SelectionHistory getPrincipalSelectionHistory() {
		return new SelectionHistory() {

			
			@Override
			protected Object restoreItemFromMemento(IMemento memento) {
				String name = memento.getString(NAME_TAG);
				String type = memento.getString(TYPE_TAG);
				PrincipalType principalType = null;
				
				if ( type != null ) {
					principalType = PrincipalType.valueOf(type);
				}
				
				if ( PrincipalType.SPECIAL_ACCOUNT.equals(principalType) ) {
					return null;
				}
				return new PrincipalMock(name);
			}

			@Override
			protected void storeItemToMemento(Object item, IMemento memento) {
				memento.putString(NAME_TAG, ((IPrincipal)item).getName() );
				memento.putString(TYPE_TAG, ((IPrincipal)item).getType().name() );
			}

			@Override
			public void load(IMemento memento) {
				super.load(memento);

				for ( IPrincipal principal : initialSelection ) {
					accessed( principal );
				}
			}};
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		Composite container = createContainer(parent,2);
		createRealmControls(container);
		
		Composite radioButtonsContainer = createContainer(container,3);
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		radioButtonsContainer.setLayoutData(layoutData);
		
		createSpecialAccountsButton(radioButtonsContainer);
		createGroupsButton(radioButtonsContainer);
		createUsersButton(radioButtonsContainer);

		return container;
	}

	private void createRealmControls(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Select Realm: ");
		ComboViewer levelCombo = new ComboViewer(parent, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		levelCombo.setContentProvider(new ArrayContentProvider());
		levelCombo.setLabelProvider(new LabelProvider());
		
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 1;
		levelCombo.getCombo().setLayoutData(layoutData);
		
	}
	private Composite createContainer(Composite parent, int columns) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = columns;
		container.setLayout(gridLayout);
		return container;
	}

	private void createSpecialAccountsButton(Composite parent) {
		Button radioButton = new Button(parent, SWT.RADIO);
		radioButton.setText(SHOW_SPECIAL_ACCOUNTS_LABEL);
		radioButton.setSelection( showSpecialAccounts );
		radioButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (showSpecialAccounts != ((Button) e.widget).getSelection()) {
					showSpecialAccounts = ((Button) e.widget).getSelection();
					applyFilter();
				}
			}
		});
	}
	
	private void createGroupsButton(Composite parent) {
		Button radioButton = new Button(parent, SWT.RADIO);
		radioButton.setText(SHOW_GROUPS_LABEL);
		radioButton.setSelection( showGroups );
		radioButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (showGroups != ((Button) e.widget).getSelection()) {
					showGroups = ((Button) e.widget).getSelection();
					applyFilter();
				}
			}
		});
	}

	private void createUsersButton(Composite parent) {
		Button checkButton = new Button(parent, SWT.RADIO);
		checkButton.setText(SHOW_USERS_LABEL);
		checkButton.setSelection( showUsers);
		checkButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (showUsers != ((Button) e.widget).getSelection()) {
					showUsers = ((Button) e.widget).getSelection();
					applyFilter();
				}
			}
		});
	}
	
	@Override
	protected ItemsFilter createFilter() {
		return new PrincipalFilter();
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
		
		progressMonitor.beginTask("Searching", resources.size()); //$NON-NLS-1$
		for (IPrincipal principal : resources ) {
			contentProvider.add(principal, itemsFilter);
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			if ( progressMonitor.isCanceled() ) {
				System.err.println("Canceled!");
				return;
			}
			progressMonitor.worked(1);
		}
		progressMonitor.done();
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}
		return settings;	
	}

	@Override
	public String getElementName(Object item) {
		return item.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Comparator getItemsComparator() {
		return new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return arg0.toString().compareTo(arg1.toString());
			}
		};
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	private class PrincipalFilter extends ItemsFilter {
		
		public final boolean showGroups = PrincipalSelectionDialog.this.showGroups;
		public final boolean showUsers = PrincipalSelectionDialog.this.showUsers;
		public final boolean showSpecialAccounts = PrincipalSelectionDialog.this.showSpecialAccounts;
		
		public boolean matchItem(Object item) {
			IPrincipal principal = (IPrincipal) item;
			
			if ( principal.isSpecialAccount() ) {
				return showSpecialAccounts;
			}
			
			if ( !(showGroups && principal.isGroup() || showUsers && !principal.isGroup()) ) {
				return false;
			}
			return matches( principal.getName() );
		}
		public boolean equalsFilter(ItemsFilter filter) {
		
			PrincipalFilter principalFilter = (PrincipalFilter) filter;

			if (showSpecialAccounts != principalFilter.showSpecialAccounts ) {
				return false;
			}
			
			if (showGroups != principalFilter.showGroups) {
				return false;
			}

			if (showUsers != principalFilter.showUsers) {
				return false;
			}
			
			return super.equalsFilter(filter);
		}
		
		public boolean isSubFilter(ItemsFilter filter) {
			
			PrincipalFilter principalFilter = (PrincipalFilter) filter;
			
			if (showSpecialAccounts != principalFilter.showSpecialAccounts ) {
				return false;
			}

			if (showGroups != principalFilter.showGroups) {
				return false;
			}

			if (showUsers != principalFilter.showUsers) {
				return false;
			}

			return super.isSubFilter(filter);
		}
		
		public boolean isConsistentItem(Object item) {
			return true;
		}
	}
}