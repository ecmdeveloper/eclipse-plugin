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
package com.ecmdeveloper.plugin.content.jobs;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.content.util.ContentCache;
import com.ecmdeveloper.plugin.core.model.IDocument;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.tasks.IGetContentAsFileTask;
import com.ecmdeveloper.plugin.core.model.tasks.ITaskFactory;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ViewDocumentJob extends AbstractDocumentContentJob {

	private static final String TASK_MESSAGE = "Viewing document \"{0}\"";
	private static final String FAILED_MESSAGE = "Viewing \"{0}\" failed";
	private static final String HANDLER_NAME = "View Document";
	private static final String VIEW_CONTENT_ELEMENT_MESSAGE = "Opening viewer for document \"{0}\" content element {1}";

	private String filePrefix;
	
	public ViewDocumentJob(IDocument document, IWorkbenchWindow window ) {
		this(document, null, window);
	}

	public ViewDocumentJob(IDocument document, String filePrefix, IWorkbenchWindow window ) {
		super( HANDLER_NAME, document, window );
		this.filePrefix = filePrefix;
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

		for ( Integer contentElement : contentElements ) {
			try {
				viewContentElement(contentElement,monitor);
			} catch (ExecutionException e) {
				showError( MessageFormat.format(FAILED_MESSAGE, document.getName() ), e);
			}
		}
		
		monitor.done();
		return Status.OK_STATUS;
	}

	private void viewContentElement(int index, IProgressMonitor monitor) throws ExecutionException {

		String subTask = MessageFormat.format( VIEW_CONTENT_ELEMENT_MESSAGE, document.getName(), index );
		monitor.subTask( subTask );
		
		IFileStore documentFile = getDocumentFile(index);
		openFileInEditor(documentFile);
	}

	private void openFileInEditor(final IFileStore documentFile) {
		
		window.getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				try {
					IDE.openEditorOnFileStore( window.getActivePage(), documentFile );
				} catch (PartInitException e) {
					showError( MessageFormat.format(FAILED_MESSAGE, document.getName() ), e);
				}
			}
		} );
	}

	private IFileStore getDocumentFile(int index) throws java.util.concurrent.ExecutionException {

		IPath tempFolderPath = getTempFolderPath(document);

		ITaskFactory taskFactory = document.getTaskFactory();
		IGetContentAsFileTask task = taskFactory.getGetContentAsFileTask((IDocument) document, tempFolderPath
				.toOSString(), index);
		task.setFilePrefix(filePrefix);
		String outputFile = (String) Activator.getDefault().getTaskManager().executeTaskSync(task);
		if ( outputFile == null ) {
			return null;
		}
		
		File file = new File( outputFile);
		file.setReadOnly();

		IFileStore store = EFS.getLocalFileSystem().getStore( new Path( outputFile ) );
		addFileToContentCache(tempFolderPath.toFile(), store.toURI() );
		return store;
	}

	private void addFileToContentCache(File file, URI uri) {
		ContentCache contentCache = Activator.getDefault().getContentCache();
		contentCache.registerFile( uri.toString(), file.getPath() );
	}

	private IPath getTempFolderPath(IObjectStoreItem objectStoreItem) {
	
		ContentCache contentCache = Activator.getDefault().getContentCache();
		return contentCache.getTempFolderPath(objectStoreItem);
	}
}
