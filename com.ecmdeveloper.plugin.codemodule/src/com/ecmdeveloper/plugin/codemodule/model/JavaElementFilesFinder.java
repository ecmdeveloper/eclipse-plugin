package com.ecmdeveloper.plugin.codemodule.model;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

public class JavaElementFilesFinder {

	public Collection<File> findFiles(IJavaElement javaElement) throws JavaModelException {

		Collection<File> files = new ArrayList<File>();

		IClasspathEntry[] classPathEntries = javaElement.getJavaProject().getResolvedClasspath(true);
		
		if ( javaElement instanceof IJavaProject ) {
			getProjectFiles((IJavaProject) javaElement, classPathEntries, files);
		} else if ( javaElement instanceof IPackageFragment ) {
			getPackageFragmentFiles((IPackageFragment) javaElement, classPathEntries, files);
		} else if ( javaElement instanceof ICompilationUnit ) {
			getClassFiles((ICompilationUnit) javaElement, classPathEntries, files);
		}
		return files;
	}

	private void getProjectFiles(IJavaProject javaProject, IClasspathEntry[] classPathEntries, Collection<File> files) throws JavaModelException {
		IPackageFragment[] packageFragmentRoots = javaProject.getPackageFragments();
		for (IPackageFragment packageFragment : packageFragmentRoots) {
			if ( packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE ) {
				getPackageFragmentFiles(packageFragment, classPathEntries, files);
			}
		}
	}

	private void getPackageFragmentFiles(IPackageFragment packageFragment, IClasspathEntry[] classPathEntries, Collection<File> files) throws JavaModelException {
		for ( ICompilationUnit compilationUnit : packageFragment.getCompilationUnits() ) {
			getClassFiles(compilationUnit, classPathEntries, files);
		}
	}

	private void getClassFiles(ICompilationUnit compilationUnit, IClasspathEntry[] classPathEntries, Collection<File> files) throws JavaModelException {

		IJavaProject javaProject = compilationUnit.getJavaProject();
		Path relativePathToClassFile = getRelativeClassFilePath(compilationUnit);
		
		for (IClasspathEntry classPathEntry : classPathEntries) {
			if ( classPathEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE ) {
				IPath outputPath = getEntryOutputPath(classPathEntry, relativePathToClassFile, javaProject );
				File file = outputPath.toFile();
				if (file.exists() ) {
					addCompilationUnitFiles(file, compilationUnit, files);
					return;
				}
			}
		}
	}

	private void addCompilationUnitFiles(File file, ICompilationUnit compilationUnit, Collection<File> files) {
		files.add(file);
		File[] relatedFiles = getRelatedFiles(file, compilationUnit);
		for ( File relatedFile : relatedFiles ) {
			files.add( relatedFile );
		}
		return;
	}

	private File[] getRelatedFiles(File file, ICompilationUnit compilationUnit) {
		File parent = file.getParentFile();
		final String filterPrefix = file.getName().replaceAll("\\.class", "\\$");
		File[] relatedFiles = parent.listFiles( new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith( filterPrefix );
			}
			
		} );
		return relatedFiles;
	}

	private Path getRelativeClassFilePath(ICompilationUnit compilationUnit) throws JavaModelException {

		IPackageDeclaration[] packageDeclarations = compilationUnit.getPackageDeclarations();
		StringBuffer pathName = new StringBuffer();
		if ( packageDeclarations.length > 0 ) {
			pathName.append( packageDeclarations[0].getElementName().replace('.', '/') );
		}
		
		pathName.append("/");
		pathName.append( compilationUnit.getElementName().replaceAll(".java", ".class") );

		return new Path( pathName.toString() );
	}

	private IPath getEntryOutputPath(IClasspathEntry entry, Path relativePathToClassFile, IJavaProject project)
			throws JavaModelException {
		
		IPath path = entry.getOutputLocation();
		IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		if (path != null) {
			path = rootLoc.append(path);
		} else {
			path = rootLoc.append(project.getOutputLocation());
		}
		path = path.append(relativePathToClassFile);
		return path;
	}
}
