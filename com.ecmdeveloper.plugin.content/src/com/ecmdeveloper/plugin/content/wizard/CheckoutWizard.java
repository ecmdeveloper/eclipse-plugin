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

package com.ecmdeveloper.plugin.content.wizard;

import java.text.MessageFormat;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.jobs.ChainedJobsSchedulingRule;
import com.ecmdeveloper.plugin.content.jobs.CheckoutJob;
import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckoutWizard extends Wizard {
	
	private static final String WINDOW_TITLE_MESSAGE = "Checkout Document {0}";

	private ConfigureCheckoutWizardPage page;
	private Document document;
	private IWorkbenchWindow window;

	public CheckoutWizard(Document document) {
		super();
		this.document = document;
		String title = MessageFormat.format( WINDOW_TITLE_MESSAGE, document.getName() );
		setWindowTitle(title);
	}
	
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void addPages() {
		page = new ConfigureCheckoutWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		scheduleCheckoutJob();
		return true;
	}

	private void scheduleCheckoutJob() {
		boolean download = page.isDowload();
		boolean openEditor = page.isEdit();
		boolean trackFile = page.isTracked();
		
		CheckoutJob job = new CheckoutJob(document, window, download, openEditor, trackFile );
		job.setRule( new ChainedJobsSchedulingRule(1) );
		job.setUser(true);
		job.schedule();
	}
}
