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

package com.ecmdeveloper.plugin.content.jobs;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchWindow;

import com.ecmdeveloper.plugin.content.compare.DocumentCompareEditorInput;
import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class CompareDocumentJob extends AbstractDocumentContentJob {

	private static final String MULTIPLE_CONTENT_ELEMENTS_MESSAGE = "For comparing only one content element must be selected";
	private static final String COMPARE_TITLE_FORMAT = "Compare(''{0}'' - ''{1}'')";
	private static final String HANDLER_NAME = "Compare Document";
	private static final String TASK_MESSAGE = "Comparing document \"{0}\"";
	private static final String FAILED_MESSAGE = "Comparing \"{0}\" failed";
	private IFile compareFile;
	
	public CompareDocumentJob(Document document, IWorkbenchWindow window, IFile compareFile) {
		super(HANDLER_NAME, document, window);
		this.compareFile = compareFile;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		String taskName = MessageFormat.format( TASK_MESSAGE, document.getName() );
		monitor.beginTask(taskName, IProgressMonitor.UNKNOWN );
		Collection<Integer> contentElements = getContentElements(monitor);
		
		if ( contentElements.size() == 0 ) {
			monitor.done();
			return Status.CANCEL_STATUS;
		}

		if ( contentElements.size() > 1 ) {
			monitor.done();
			String message = MULTIPLE_CONTENT_ELEMENTS_MESSAGE;
			showMessage(message );
			return Status.CANCEL_STATUS;
		}
		
		Integer contentElement = contentElements.iterator().next();
		try {
			compareContentElement(contentElement,monitor);
		} catch (Exception e) {
			showError( MessageFormat.format(FAILED_MESSAGE, document.getName() ), e);
		}
		monitor.done();
		return Status.OK_STATUS;
	}

	private void compareContentElement(Integer contentElement, IProgressMonitor monitor) throws Exception {

		DocumentCompareEditorInput editorInput = createCompareEditorInput(contentElement,
				compareFile);
		
		openCompareEditor( editorInput );
	}

	private DocumentCompareEditorInput createCompareEditorInput(Integer contentElement,
			IFile compareFile) {

		CompareConfiguration configuration = new CompareConfiguration();
		configuration.setLeftLabel( document.getName() );
		configuration.setRightLabel( compareFile.getName() );
		
		DocumentCompareEditorInput editorInput = new DocumentCompareEditorInput(configuration,
				document, contentElement, compareFile);

		String title = MessageFormat.format(COMPARE_TITLE_FORMAT, configuration
				.getLeftLabel(null), configuration.getRightLabel(null));
		editorInput.setTitle( title );

		return editorInput;
	}

	private void openCompareEditor(final DocumentCompareEditorInput editorInput) {
		window.getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				CompareUI.openCompareEditor(editorInput);;
			}
		} );
	}
}
