/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.scripting.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.ecmdeveloper.plugin.scripting.Activator;

/**
 * @author ricardo.belfor
 *
 */
public class MethodSelectionDialog extends FilteredItemsSelectionDialog {

	private static final String DIALOG_SETTINGS = "ClassSelectionDialogSettings";
	private ClassesContentProvider classesContentProvider;
	private TableViewer methodsTable;
	private IMethod selectedMethod;
	
	public MethodSelectionDialog(Shell shell) {
		super(shell);
		setTitle("Script Selection");
		setListLabelProvider( new JavaElementLabelProvider() );

		setDetailsLabelProvider( createDetailsLabelProvider());
		classesContentProvider = new ClassesContentProvider();
	}


	private JavaElementLabelProvider createDetailsLabelProvider() {
		return new JavaElementLabelProvider() {

			@Override
			public Image getImage(Object element) {
				ICompilationUnit compilationUnit = (ICompilationUnit) element;
				return super.getImage(compilationUnit.getParent());
			}

			@Override
			public String getText(Object element) {
				ICompilationUnit compilationUnit = (ICompilationUnit) element;
				return super.getText(compilationUnit.getParent());
			}
			
		};
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		return createMethodsTable(parent);
	}


	private Control createMethodsTable(Composite parent) {
		methodsTable = new TableViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		methodsTable.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		methodsTable.setContentProvider( new ArrayContentProvider() );
		methodsTable.setLabelProvider( new JavaElementLabelProvider() );
		methodsTable.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				methodsTableSelectionChanged();
			}});
		return methodsTable.getControl();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void computeResult() {
		List objectsToReturn = new ArrayList();
		if ( selectedMethod != null )
		{
			objectsToReturn.add(selectedMethod);
		}
		setResult(objectsToReturn);
	}

	@SuppressWarnings("unchecked")
	protected void methodsTableSelectionChanged() {
		IStructuredSelection selection = (IStructuredSelection) methodsTable.getSelection();
		Iterator<IMethod> iterator = selection.iterator();
		if ( iterator.hasNext() ) {
			selectedMethod = iterator.next();
			updateStatus( Status.OK_STATUS );
		}
	}


	@Override
	protected ItemsFilter createFilter() {
		return new ItemsFilter()
		{
			@Override
			public boolean isConsistentItem(Object item) {
				return true;
			}

			@Override
			public boolean matchItem(Object item) {
				return matches( ((ICompilationUnit)item).getElementName() );
			}
		};
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
		
		Collection<ICompilationUnit> elements = classesContentProvider.getElements();
		progressMonitor.beginTask("Getting classes", elements.size());
		for ( Object element : elements ) {
			contentProvider.add(element, itemsFilter);
			progressMonitor.worked(1);
		}
		progressMonitor.done();
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(
				DIALOG_SETTINGS);
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
	        	 String name0 = ((ICompilationUnit)arg0).getElementName();
	        	 String name1 = ((ICompilationUnit)arg1).getElementName();
	        	 return name0.toString().compareTo(name1.toString());
	          }
	       };
	}

	@Override
	protected IStatus validateItem(Object item) {
		
		ICompilationUnit compilationUnit = (ICompilationUnit)item;
		try {
			selectedMethod = null;
			setMethods(compilationUnit);
		} catch (JavaModelException e) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage() );
		}
		
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "No method selected");
	}


	private void setMethods(ICompilationUnit compilationUnit) throws JavaModelException {

		IJavaElement[] children = compilationUnit.getChildren();
		for ( IJavaElement child : children ) {
			if ( child instanceof IType ) {
				ArrayList<IMethod> filteredMethods = getFilteredMethods(child);
				methodsTable.setInput( filteredMethods );
				return;
			}
		}
	}


	private ArrayList<IMethod> getFilteredMethods(IJavaElement child) throws JavaModelException {
		ArrayList<IMethod> filteredMethods = new ArrayList<IMethod>();
		
		IMethod[] methods = ((IType)child).getMethods();
		for ( IMethod method : methods ) {
			System.out.println( method.getElementName() );
			if ( Flags.isPublic(method.getFlags() ) ) {
				filteredMethods.add(method);
			}
		}
		return filteredMethods;
	}
}
