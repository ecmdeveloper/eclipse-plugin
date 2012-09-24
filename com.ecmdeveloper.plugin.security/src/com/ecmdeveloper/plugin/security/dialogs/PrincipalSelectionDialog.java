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
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.ui.dialogs.SearchPattern;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.util.PluginMessage;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.editor.SecurityLabelProvider;

/**
 * @author ricardo.belfor
 *
 */
public class PrincipalSelectionDialog extends FilteredItemsSelectionDialog {

	private static final String MESSAGE = "Select an item (* = any string):";
	private static final String LABEL_PROVIDER_STRING_2 = ")";
	private static final String LABEL_PROVIDER_STRING_1 = " (";
	private static final String SHORT_NAMES_LABEL = "Search Short Names";
	private static final String DISPLAY_NAMES_LABEL = "Search Display Names";
	private static final int INPUT_DELAY_IN_100_MS = 5;
	private static final String FETCHING_PRINCIPALS_FAILED_MESSAGE = "Fetching principals failed";
	private static final String SHOW_SPECIAL_ACCOUNTS_LABEL = "Show Special Accounts";
	private static final String SHOW_GROUPS_LABEL = "Show Groups";
	private static final String SHOW_USERS_LABEL = "Show Users";
	private static final String TITLE = "Principal Selection";
	private static final String SEPARATOR_LABEL = "Previous matches";
	private static final String DIALOG_SETTINGS = "PrincipalSelectionDialogSettings";
	private boolean showSpecialAccounts = true;
	private boolean showGroups = false;
	private boolean showUsers = false;
	private boolean searchShortNames = true;
	private boolean searchDisplayNames = false;
	
	private SecurityLabelProvider labelProvider;
	private ArrayList<IPrincipal> initialSelection = new ArrayList<IPrincipal>();
	private final Collection<IRealm> realms;
	private IRealm realm;

	public PrincipalSelectionDialog(Shell shell, Collection<IRealm> realms, IPrincipal initialPrincipal ) {
		super(shell,false);

		setTitle(TITLE);

		this.realms = realms;
		if ( realms != null && !realms.isEmpty() ) {
			realm = realms.iterator().next();
		} else {
			throw new IllegalArgumentException("No realms!");
		}

		labelProvider = new SecurityLabelProvider() {
			@Override
			public String getText(Object element) {
				if ( element instanceof IPrincipal ) {
					IPrincipal principal = (IPrincipal) element;
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append(principal.getShortName());
					if ( !principal.getShortName().equals(principal.getDisplayName()) ) {
						stringBuilder.append(LABEL_PROVIDER_STRING_1);
						stringBuilder.append(principal.getDisplayName());
						stringBuilder.append(LABEL_PROVIDER_STRING_2);
					}
					return stringBuilder.toString();
				}
				return super.getText(element);
			}
		};
		
		setMessage(MESSAGE);
		setListLabelProvider( labelProvider );
		setDetailsLabelProvider(labelProvider);
		setSeparatorLabel(SEPARATOR_LABEL);
		setInitialSelection(initialPrincipal);

		if ( initialPrincipal != null ) {
			setInitialPattern( searchShortNames ? initialPrincipal.getShortName() : initialPrincipal.getDisplayName() );
		}
		
		setSelectionHistory(getPrincipalSelectionHistory()  );
	}

	private void setInitialSelection(IPrincipal initialPrincipal) {
		
		try {
			initialSelection.addAll( realm.findPrincipals("", PrincipalType.SPECIAL_ACCOUNT) );
			
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
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private SelectionHistory getPrincipalSelectionHistory() {
		return new SelectionHistory() {

			
			@Override
			protected Object restoreItemFromMemento(IMemento memento) {
				return realm.restore(memento);
			}

			@Override
			protected void storeItemToMemento(Object item, IMemento memento) {
				realm.store( (IPrincipal)item, memento );
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
		
		Composite namesButtonsContainer = createContainer(container,2);
		GridData layoutData2 = new GridData();
		layoutData2.horizontalSpan = 2;
		namesButtonsContainer.setLayoutData(layoutData2);
		
		createShortNamesButton(namesButtonsContainer);
		createDisplayNamesButton(namesButtonsContainer);

		return container;
	}

	private void createRealmControls(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Select Realm: ");
		final ComboViewer realmCombo = new ComboViewer(parent, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		realmCombo.setContentProvider(new ArrayContentProvider());
		realmCombo.setLabelProvider(new LabelProvider());
		realmCombo.setInput(realms);
		realmCombo.setSelection( new StructuredSelection( realm ) );
		
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.horizontalSpan = 1;
		realmCombo.getCombo().setLayoutData(layoutData);
		
		realmCombo.addSelectionChangedListener( getRealmComboChangeListener(realmCombo) );
	}

	private ISelectionChangedListener getRealmComboChangeListener(final ComboViewer realmCombo) {
		return new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) realmCombo.getSelection();
				if ( !selection.isEmpty() ) {
					realm = (IRealm) selection.getFirstElement();
					applyFilter();
				}
			}};
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

	private void createShortNamesButton(Composite parent) {
		Button radioButton = new Button(parent, SWT.RADIO);
		radioButton.setText(SHORT_NAMES_LABEL);
		radioButton.setSelection( searchShortNames );
		radioButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (searchShortNames != ((Button) e.widget).getSelection()) {
					searchShortNames = ((Button) e.widget).getSelection();
					if ( searchShortNames ) {
						applyFilter();
					}
				}
			}
		});
	}

	private void createDisplayNamesButton(Composite parent) {
		Button radioButton = new Button(parent, SWT.RADIO);
		radioButton.setText(DISPLAY_NAMES_LABEL);
		radioButton.setSelection( searchDisplayNames );
		radioButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (searchDisplayNames != ((Button) e.widget).getSelection()) {
					searchDisplayNames = ((Button) e.widget).getSelection();
					if ( searchDisplayNames ) {
						applyFilter();
					}
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
		
		progressMonitor.beginTask("Searching", IProgressMonitor.UNKNOWN);
		
		if ( waitForCancel(progressMonitor) ) {
			return;
		}

		PrincipalType type = showGroups ? PrincipalType.GROUP : (showUsers ? PrincipalType.USER : PrincipalType.SPECIAL_ACCOUNT );
		
		try {
			Collection<IPrincipal> principals = realm.findPrincipals(itemsFilter.getPattern(), type , progressMonitor);
			if ( principals != null) {
				for (IPrincipal principal : principals ) {
					contentProvider.add(principal, itemsFilter);
				}
			}
		} catch (ExecutionException e) {
			PluginMessage.openErrorFromThread(TITLE, FETCHING_PRINCIPALS_FAILED_MESSAGE, e);
		}
		progressMonitor.done();
	}

	private boolean waitForCancel(IProgressMonitor progressMonitor) {
		for ( int i = 0; i < INPUT_DELAY_IN_100_MS; ++i) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			if ( progressMonitor.isCanceled() ) {
				return true;
			}
		}
		return false;
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
		if ( item instanceof IPrincipal) {
			IPrincipal principal = (IPrincipal) item;
			if ( searchDisplayNames ) {
				return principal.getDisplayName();
			} else if ( searchShortNames ) {
				return principal.getName();
			}
		}
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
		public final boolean searchShortNames = PrincipalSelectionDialog.this.searchShortNames;
		public final boolean searchDisplayNames = PrincipalSelectionDialog.this.searchDisplayNames;
		
		public PrincipalFilter() {
			super(new SearchPattern(/*SearchPattern.RULE_EXACT_MATCH |*/
					SearchPattern.RULE_PREFIX_MATCH | SearchPattern.RULE_PATTERN_MATCH
					| SearchPattern.RULE_BLANK_MATCH));
			patternMatcher.setPattern( patternMatcher.getPattern().toLowerCase() );
		}

		public boolean matchItem(Object item) {
			IPrincipal principal = (IPrincipal) item;
			
			if ( principal.isSpecialAccount() ) {
				return showSpecialAccounts;
			}
			
			if ( !(showGroups && principal.isGroup() || showUsers && !principal.isGroup()) ) {
				return false;
			}
			
			if ( searchShortNames ) {
				return matches( principal.getShortName() );
			} else if (searchDisplayNames) {
				return matches( principal.getDisplayName() );
			} else {
				throw new IllegalStateException();
			}
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

			if ( searchDisplayNames != principalFilter.searchDisplayNames ) {
				return false;
			}
			
			if ( searchShortNames != principalFilter.searchShortNames ) {
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

			if ( searchDisplayNames != principalFilter.searchDisplayNames ) {
				return false;
			}
			
			if ( searchShortNames != principalFilter.searchShortNames ) {
				return false;
			}

			return super.isSubFilter(filter);
		}
		
		public boolean isConsistentItem(Object item) {
			return true;
		}
	}
}