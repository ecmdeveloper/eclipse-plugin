package com.ecmdeveloper.plugin.codemodule.model;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class JavaElementClassesFinder {

	public Collection<IType> findClasses(IJavaElement javaElement, String interfaceName ) throws JavaModelException {

		Collection<IType> files = new ArrayList<IType>();

		if ( javaElement instanceof IJavaProject ) {
			getProjectFiles((IJavaProject) javaElement, interfaceName, files);
		} else if ( javaElement instanceof IPackageFragment ) {
			getPackageFragmentFiles((IPackageFragment) javaElement, interfaceName, files);
		} else if ( javaElement instanceof ICompilationUnit ) {
			getClassFiles((ICompilationUnit) javaElement, interfaceName, files);
		}
		return files;
	}

	private void getProjectFiles(IJavaProject javaProject, String interfaceName, Collection<IType> files) throws JavaModelException {
		IPackageFragment[] packageFragmentRoots = javaProject.getPackageFragments();
		for (IPackageFragment packageFragment : packageFragmentRoots) {
			if ( packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE ) {
				getPackageFragmentFiles(packageFragment, interfaceName, files);
			}
		}
	}

	private void getPackageFragmentFiles(IPackageFragment packageFragment, String interfaceName, Collection<IType> files) throws JavaModelException {
		for ( ICompilationUnit compilationUnit : packageFragment.getCompilationUnits() ) {
			getClassFiles(compilationUnit, interfaceName, files);
		}
	}

	private void getClassFiles(ICompilationUnit compilationUnit, String interfaceName, Collection<IType> files) throws JavaModelException {

		IJavaElement[] children = compilationUnit.getChildren();
		for ( IJavaElement child : children ) {
			if ( child instanceof IType ) {
				IType type = (IType) child;
				for ( String typeInterfaceName : type.getSuperInterfaceNames() ) {
					System.out.println( typeInterfaceName );
					if ( isImplementsInterface(interfaceName, typeInterfaceName) ) {
						files.add(type);
					}
				}
			}
		}
	}

	private boolean isImplementsInterface(String interfaceName, String typeInterfaceName) {
		return interfaceName.equals(typeInterfaceName) ||  interfaceName.endsWith("." + typeInterfaceName );
	}
}
