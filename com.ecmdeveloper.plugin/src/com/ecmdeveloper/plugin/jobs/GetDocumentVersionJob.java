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

package com.ecmdeveloper.plugin.jobs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.tasks.FetchPropertiesTask;
import com.ecmdeveloper.plugin.model.tasks.GetDocumentVersionsTask;
import com.ecmdeveloper.plugin.util.PluginMessage;
import com.filenet.api.constants.PropertyNames;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetDocumentVersionJob extends Job {

	private static final String SELECTION_MESSAGE = "Select the document versions";
	private static final String SELECTION_TITLE = "Document Versions Selection";
	private static final String JOB_NAME = "Get Document Versions";
	private static final String FAILED_MESSAGE = "Getting document versions for \"{0}\" failed";
	private static final String MONITOR_MESSAGE = "Getting document versions";
	
	private IStatus status;
	private Document document;
	private Shell shell;
	private List<Document> selectedVersions;
	
	public GetDocumentVersionJob(Document document, Shell shell) {
		super(JOB_NAME);
		this.document = document;
		this.shell = shell;
	}

	public Collection<Document> getSelectedVersions() {
		return selectedVersions;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			monitor.beginTask( MONITOR_MESSAGE, IProgressMonitor.UNKNOWN );
			final List<IDocument> versions = getDocumentVersions();
			monitor.done();

			monitor.beginTask("Getting version properties", versions.size() );
			getDocumentVersionsProperties(versions,monitor);
			monitor.done();
			
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					status = selectDocumentVersion(versions);
				}
			} );
			
			return status;
			
		} catch (Exception e) {
			String message = MessageFormat.format(FAILED_MESSAGE, document.getName());
			PluginMessage.openErrorFromThread(shell, JOB_NAME, message, e);
		}
		return Status.OK_STATUS;
	}

	private void getDocumentVersionsProperties(final List<IDocument> versions, IProgressMonitor monitor)
			throws ExecutionException {

		String[] propertyNames = { PropertyNames.MAJOR_VERSION_NUMBER, PropertyNames.MINOR_VERSION_NUMBER };

		for ( IDocument documentVersion : versions) {
			FetchPropertiesTask task = new FetchPropertiesTask(documentVersion, propertyNames );
			Activator.getDefault().getTaskManager().executeTaskSync(task);
			monitor.worked(1);
		}
	}

	private List<IDocument> getDocumentVersions() throws ExecutionException {
		GetDocumentVersionsTask task = new GetDocumentVersionsTask(document);
		Activator.getDefault().getTaskManager().executeTaskSync(task);
		List<IDocument> versions = task.getVersions();
		return versions;
	}

	private IStatus selectDocumentVersion(List<IDocument> versions) {
		
		ListSelectionDialog dialog = createSelectionDialog(versions);

		int returnCode = dialog.open();
		if ( returnCode == Window.CANCEL || dialog.getResult().length == 0) {
			return Status.CANCEL_STATUS;
		}

		selectedVersions = new ArrayList<Document>();
		for ( Object selectedVersion : dialog.getResult() ) {
			selectedVersions.add((Document) selectedVersion);
		}
		return Status.OK_STATUS;
	}

	private ListSelectionDialog createSelectionDialog(List<IDocument> versions) {
		LabelProvider labelProvider = new DocumentVersionLabelProvider();
		ListSelectionDialog dialog = new ListSelectionDialog(shell, versions.toArray(),
				new ArrayContentProvider(), labelProvider, SELECTION_MESSAGE);

		dialog.setTitle( SELECTION_TITLE );
		dialog.setMessage( SELECTION_MESSAGE );
		return dialog;
	}
	
	class DocumentVersionLabelProvider extends LabelProvider {

		private static final String VERSION_LABEL_FORMAT = "Version {1}.{2} ({0})";

		@Override
		public Image getImage(Object element) {
			String imageKey = ISharedImages.IMG_OBJ_FILE;
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}

		@Override
		public String getText(Object element) {
			if ( element instanceof Document) {
				Document version = (Document) element;
				String name = version.getName();
				Object majorVersionNumber = version.getValue( PropertyNames.MAJOR_VERSION_NUMBER );
				Object minorVersionNumber = version.getValue( PropertyNames.MINOR_VERSION_NUMBER );
				return MessageFormat.format( VERSION_LABEL_FORMAT, name, majorVersionNumber, minorVersionNumber );
			}

			return super.getText(element);
		}
	}
}
