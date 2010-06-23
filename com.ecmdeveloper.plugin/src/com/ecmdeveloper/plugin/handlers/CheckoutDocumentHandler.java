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

package com.ecmdeveloper.plugin.handlers;

import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ecmdeveloper.plugin.jobs.CheckoutJob;
import com.ecmdeveloper.plugin.jobs.DownloadDocumentJob;
import com.ecmdeveloper.plugin.jobs.GetDocumentVersionJob;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.util.PluginMessage;
import com.ecmdeveloper.plugin.wizard.CheckinWizard;
import com.ecmdeveloper.plugin.wizard.CheckoutWizard;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckoutDocumentHandler extends AbstractHandler implements IHandler {

//	private static final String HANDLER_NAME = "Checkout";

	private IWorkbenchWindow window;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		if (window == null)	return null;

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		Iterator<?> iterator = ((IStructuredSelection) selection).iterator();
		while ( iterator.hasNext() ) {
			IObjectStoreItem objectStoreItem = (IObjectStoreItem) iterator.next();
			openCheckoutWizard((Document) objectStoreItem);
		}

		return null;
	}

//	class CheckoutDocumentHandlerJobListener extends JobChangeAdapter {
//
//		private static final String DOWNLOAD_QUESTION = "Download content of document \"{0}\"?";
//
//		@Override
//		public void done(IJobChangeEvent event) {
//
//			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
//				return;
//			}
//			
//			final CheckoutJob job = (CheckoutJob) event.getJob();
//			
//			Display.getDefault().syncExec(new Runnable() {
//				@Override
//				public void run() {
//					String message = MessageFormat.format(DOWNLOAD_QUESTION, job.getDocument().getName());
//					boolean answerTrue = MessageDialog.openQuestion(window.getShell(), HANDLER_NAME,
//							message);
//					if ( answerTrue ) {
//						scheduleDownloadJob(job);
//					}
//				}
//
//				private void scheduleDownloadJob(final CheckoutJob job) {
//					DownloadDocumentJob downloadJob = new DownloadDocumentJob( job.getDocument(), window );
//					downloadJob.setUser(true);
//					downloadJob.schedule();
//				}
//			});
//		}		
//	}

	private void openCheckoutWizard(Document document) {
		CheckoutWizard wizard = new CheckoutWizard( document );
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		wizard.init(window);
		dialog.create();
		dialog.open();
	}
}
