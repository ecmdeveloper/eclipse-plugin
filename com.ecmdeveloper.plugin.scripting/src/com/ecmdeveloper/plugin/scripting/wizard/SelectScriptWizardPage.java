/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.scripting.wizard;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ecmdeveloper.plugin.scripting.ScriptingProjectNature;

/**
 * @author ricardo.belfor
 *
 */
public class SelectScriptWizardPage extends WizardPage {

	private static final String TITLE = "Select Method";

	private TreeViewer javaElementsTree;
	private IMethod method;

	protected SelectScriptWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription("Select the method to launch. Only Content Engine Scripting Projects\r\ncan be used.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = getContainer(parent);
		createJavaElementsTree(container);
		
		if ( !hasScriptingProjects()) {
			setErrorMessage("The Workspace does not contain open scripting projects. Use the \r\nNew Project wizard to create a new Content Engine Scripting Project.");
		}
	}
	
	private void createJavaElementsTree(Composite container) {

		javaElementsTree = new TreeViewer(container/*, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL */);
		javaElementsTree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ITreeContentProvider contentProvider = new StandardJavaElementContentProvider(true);
		javaElementsTree.setContentProvider(contentProvider);
		
		JavaElementLabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT | 
				JavaElementLabelProvider.SHOW_ROOT );
		javaElementsTree.setLabelProvider( labelProvider );

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		javaElementsTree.setInput( JavaCore.create(root) );
		
		javaElementsTree.addFilter( getJavaElementFilter());

		javaElementsTree.addSelectionChangedListener( new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fieldSelectionChanged();
			}} 
		);
	}

	protected void fieldSelectionChanged() {
		IStructuredSelection selection = (IStructuredSelection) javaElementsTree.getSelection();
		Iterator<?> iterator = selection.iterator();
		if (iterator.hasNext() ) {
			Object selectedObject = iterator.next();
			if ( selectedObject instanceof IMethod ) {
				this.method = (IMethod) selectedObject;
			} else {
				this.method = null;
			}
		}		
	}

	private boolean hasScriptingProjects() {
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for ( IProject project : root.getProjects() ) {
			if ( project.isOpen() ) {
				if ( ScriptingProjectNature.hasNature(project) ) {
					return true;
				}
			}
		}
		return false;
	}

	private ViewerFilter getJavaElementFilter() {
		return new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if ( element instanceof IJavaElement ) {
					int type = ((IJavaElement) element).getElementType();
					
					if ( type == IJavaElement.METHOD ) {
						try {
							System.out.println(((IMethod)element).getSignature() );
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if ( type == IJavaElement.JAVA_PROJECT ) {
						IJavaProject javaProject = ((IJavaProject)element);
						return ScriptingProjectNature.hasNature(javaProject.getProject());
					} else if ( type == IJavaElement.PACKAGE_FRAGMENT || type == IJavaElement.COMPILATION_UNIT ||
						type == IJavaElement.TYPE || type == IJavaElement.METHOD) {
						return true;
					} else if (type == IJavaElement.PACKAGE_FRAGMENT_ROOT ) {
						try {
							IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot) element; 
							return packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE;
						} catch (JavaModelException e) {
							return false;
						}
					} else {
						System.err.println( element.toString() + "\t" + type);
					}
				}
				return false;
			}
			
		};
	}	

	private Composite getContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL );
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		setControl(container);
		return container;
	}
}
