/**
 * Copyright 2009,2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.diagrams.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramFile;
import com.ecmdeveloper.plugin.diagrams.util.PluginMessage;
import com.ecmdeveloper.plugin.diagrams.util.PluginTagNames;

/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class NewClassDiagramWizard extends Wizard implements INewWizard {

	private static final String WIZARD_TITLE = "New Content Engine Class Diagram";
	private NewClassDiagramWizardPage page;
	private IStructuredSelection selection;
	private IWorkbench workbench;

	public NewClassDiagramWizard() {
		super();
		setWindowTitle(WIZARD_TITLE);
	}
	
	public void addPages() {
		page = new NewClassDiagramWizardPage(selection);
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	public boolean performFinish() {
		IFile file = page.createNewFile();

		if (file != null) {
			try {
				IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(activePage, file, true);
				return true;
			} catch (PartInitException e) {
				PluginMessage.openError(getShell(), WIZARD_TITLE, e.getLocalizedMessage(), e);
			}
		}
		return false;
	}
}