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

package com.ecmdeveloper.plugin.wizard;

import java.util.ArrayList;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import com.ecmdeveloper.plugin.jobs.SaveJob;
import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class SaveWizard extends Wizard {

	private ContentSelectionWizardPage page;
	@SuppressWarnings("unused")
	private IStructuredSelection selection;
	@SuppressWarnings("unused")
	private IWorkbench workbench;
	private Document document;

	public SaveWizard(Document document) {
		super();
		this.document = document;
	}
	public void addPages() {
		page = new ContentSelectionWizardPage();
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}
	
	@Override
	public boolean performFinish() {
		
		ArrayList<Object> content = page.getContent();
		String mimeType = page.getMimeType();
		
		Job saveJob = new SaveJob( document, getShell(), content, mimeType );
		saveJob.setUser(true);
		saveJob.schedule();
		
		return true;
	}
}
