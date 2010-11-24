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

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author ricardo.belfor
 *
 */
public class ClassesContentProvider {

	private ArrayList<ICompilationUnit> classes;
	
	public Collection<ICompilationUnit> getElements() throws JavaModelException {
		
		if ( classes == null ) {
			initializeClasses();
		}
		
		return classes;
	}

	private void initializeClasses() throws JavaModelException {
		classes = new ArrayList<ICompilationUnit>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IJavaProject[] projects = JavaCore.create(root).getJavaProjects();
		for (IJavaProject javaProject : projects) {
			getProjectClasses(javaProject);
		}
	}

	private void getProjectClasses(IJavaProject javaProject) throws JavaModelException {
		IPackageFragment[] packageFragmentRoots = javaProject.getPackageFragments();
		for (IPackageFragment packageFragment : packageFragmentRoots) {
			getPackageClasses(packageFragment);
		}
	}

	private void getPackageClasses(IPackageFragment packageFragment) throws JavaModelException {
		if ( packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE ) {
			for ( ICompilationUnit compilationUnit : packageFragment.getCompilationUnits() ) {
				classes.add(compilationUnit);
			}
		}
	}
}
