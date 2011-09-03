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

package com.ecmdeveloper.plugin.codemodule.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFileListener;

/**
 * @author ricardo.belfor
 *
 */
public class JavaElementContentProvider implements IStructuredContentProvider,
		ITreeContentProvider, CodeModuleFileListener {

	private CodeModuleFile codeModuleFile;
	private Viewer viewer;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		this.viewer = viewer;
		
		if (codeModuleFile != null) {
			codeModuleFile.removePropertyFileListener(this);
		}
		
		if ( newInput instanceof CodeModuleFile ) {
			codeModuleFile = (CodeModuleFile) newInput;
			
			if (codeModuleFile != null) {
				codeModuleFile.addPropertyFileListener(this);
			}
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		
		try {
			ArrayList<IJavaElement> children = new ArrayList<IJavaElement>();
			if ( parentElement instanceof CodeModuleFile ) {
				CodeModuleFile codeModuleFile = (CodeModuleFile) parentElement;
				return codeModuleFile.getContentElements().toArray();
			}
			if ( parentElement instanceof IWorkspaceRoot ) {
				getJavaProjects((IWorkspaceRoot) parentElement, children);
			} else if ( parentElement instanceof IJavaElement ) {
				if ( parentElement instanceof IJavaProject ) {
					getPackageFragments((IJavaProject) parentElement, children);
				} if ( parentElement instanceof IPackageFragment ) {
					getCompilationUnits( (IPackageFragment) parentElement, children);			
				}
			}
			return children.toArray();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void getJavaProjects(IWorkspaceRoot workspaceRoot, ArrayList<IJavaElement> children)
			throws JavaModelException {
		IJavaProject[] projects = JavaCore.create(workspaceRoot).getJavaProjects();
		for (IJavaProject javaProject : projects) {
			children.add( javaProject );
		}
	}

	private void getPackageFragments(IJavaProject javaProject, ArrayList<IJavaElement> children)
			throws JavaModelException {
		IPackageFragment[] packageFragmentRoots = javaProject.getPackageFragments();
		for (IPackageFragment packageFragment : packageFragmentRoots) {
			if ( packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE ) {
				children.add(packageFragment);
			}
		}
	}

	private void getCompilationUnits(IPackageFragment packageFragment, ArrayList<IJavaElement> children)
		throws JavaModelException {
		
		for ( ICompilationUnit compilationUnit : packageFragment.getCompilationUnits() ) {
			children.add(compilationUnit);
		}
	}
	
	@Override
	public Object getParent(Object element) {
		if ( element instanceof IJavaElement ) {
			((IJavaElement)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof IPackageFragment || element instanceof IJavaProject;
	}

	@Override
	public void filesChanged() {
		viewer.refresh();
	}

	@Override
	public void nameChanged() {
	}

}
