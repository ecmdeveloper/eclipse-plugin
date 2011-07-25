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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageTwo;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.scripting.Activator;
import com.ecmdeveloper.plugin.scripting.ScriptingProjectNature;
import com.ecmdeveloper.plugin.scripting.util.PluginMessage;

/**
 * @author ricardo.belfor
 *
 */
public class NewScriptingProjectWizard extends Wizard implements INewWizard {

	private static final String FAILED_MESSAGE = "Creating Project failed";

	protected static final String TITLE = "New Content Engine Scripting Project";
	
	private NewJavaProjectWizardPageOne wizardPageOne;
	private NewJavaProjectWizardPageTwo wizardPageTwo;
	
	public NewScriptingProjectWizard() {
		setNeedsProgressMonitor(true);
	}

	
	@Override
	public void addPages() {

		wizardPageOne = new NewJavaProjectWizardPageOne() {

			@Override
			public IClasspathEntry[] getDefaultClasspathEntries() {
				ArrayList<IClasspathEntry> classpathList = new ArrayList<IClasspathEntry>();
				for ( IClasspathEntry classpathEntry : super.getDefaultClasspathEntries() ) {
					classpathList.add(classpathEntry);
				}
				String methodRunnerLocation;
				try {
					methodRunnerLocation = Activator.getDefault().getMethodRunnerLocation();
					classpathList.add( JavaCore.newLibraryEntry(new Path( methodRunnerLocation), null, null) );
					
					String[] libraries = com.ecmdeveloper.plugin.lib.Activator.getDefault().getLibraries();
					for ( String library : libraries ) {
						classpathList.add( JavaCore.newLibraryEntry(new Path( library), null, null) );
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				return classpathList.toArray( new IClasspathEntry[ classpathList.size()] );
			}
			
		};
		addPage(wizardPageOne);
		
		wizardPageTwo = new NewJavaProjectWizardPageTwo(wizardPageOne);
		addPage(wizardPageTwo);
	}

	@Override
	public boolean performFinish() {

		try {
			getContainer().run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					try {
						wizardPageTwo.performFinish(monitor);
						IProject project = wizardPageTwo.getJavaProject().getProject();
						ScriptingProjectNature.addNature(project);
					} catch (CoreException e) {
						PluginMessage.openErrorFromThread(TITLE, FAILED_MESSAGE, e );
					}
				}
				
			} );
		} catch (InvocationTargetException e) {
			PluginMessage.openErrorFromThread(TITLE, FAILED_MESSAGE, e );
		} catch (InterruptedException e) {
			PluginMessage.openErrorFromThread(TITLE, FAILED_MESSAGE, e );
		}

		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}	
}
