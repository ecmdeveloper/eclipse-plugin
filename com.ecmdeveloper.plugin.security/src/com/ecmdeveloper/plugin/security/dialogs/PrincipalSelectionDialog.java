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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.editor.SecurityLabelProvider;
import com.ecmdeveloper.plugin.security.mock.PrincipalMock;

/**
 * @author ricardo.belfor
 *
 */
public class PrincipalSelectionDialog extends FilteredItemsSelectionDialog {

	private static final String TITLE = "Principal Selection";
	private static final String SEPARATOR_LABEL = "Previous matches";
	private static final String NAME_TAG = "name";
	private static final String DIALOG_SETTINGS = "PrincipalSelectionDialogSettings";		
	private boolean showGroups = true;
	private boolean showUsers = true;
	
	private static ArrayList<IPrincipal> resources = new ArrayList<IPrincipal>();

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

	public PrincipalSelectionDialog(Shell shell) {
		super(shell,true);
		setTitle(TITLE);
		setListLabelProvider( new SecurityLabelProvider() );
		setSeparatorLabel(SEPARATOR_LABEL);
		
		setSelectionHistory(new SelectionHistory() {

			@Override
			protected Object restoreItemFromMemento(IMemento memento) {
				String name = memento.getString(NAME_TAG);
				return new PrincipalMock(name);
			}

			@Override
			protected void storeItemToMemento(Object item, IMemento memento) {
				memento.putString(NAME_TAG, ((IPrincipal)item).getName() );
			}}  );
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		// TODO Auto-generated method stub
		return null;
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
		
		public boolean matchItem(Object item) {
			IPrincipal principal = (IPrincipal) item;
			if ( !(showGroups && principal.isGroup() || showUsers && !principal.isGroup()) ) {
				return false;
			}
			return matches( principal.getName() );
		}
		public boolean equalsFilter(ItemsFilter filter) {
		
			PrincipalFilter principalFilter = (PrincipalFilter) filter;
			
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