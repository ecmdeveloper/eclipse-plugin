/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.jobs;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.GetContentAsFileTask;
import com.ecmdeveloper.plugin.util.ContentCache;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ViewDocumentJob extends Job {

	private static final String NO_CONTENT_MESSAGE = "Document \"{0}\" contains no content";
	private static final String FAILED_MESSAGE = "Viewing \"{0}\" failed";
	private static final String HANDLER_NAME = "View Document";

	private Document document;
	private IWorkbenchWindow window;

	public ViewDocumentJob(Document document, IWorkbenchWindow window ) {
		super( HANDLER_NAME );
		this.document = document;
		this.window = window;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			IFileStore documentFile = getDocumentFile();
			if ( documentFile == null ) {
				showMessage( MessageFormat.format( NO_CONTENT_MESSAGE, document.getName() ) );
				return Status.OK_STATUS;
			}
			openFileInEditor(documentFile);
		} catch ( final Exception e ) {
			showError(e);
		}
		return Status.OK_STATUS;
	}

	private void openFileInEditor(final IFileStore documentFile) {
		
		window.getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				try {
					IDE.openEditorOnFileStore( window.getActivePage(), documentFile );
				} catch (PartInitException e) {
					showError(e);
				}
			}
		} );
	}

	private IFileStore getDocumentFile() throws java.util.concurrent.ExecutionException {

		IPath tempFolderPath = getTempFolderPath(document);

		GetContentAsFileTask task = new GetContentAsFileTask( (Document) document, tempFolderPath.toOSString() );
		String outputFile = (String) ObjectStoresManager.getManager().executeTaskSync(task);
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
//		IPath cacheLocation = Activator.getDefault().getTempContentPath()
//				.append(getTempFolderName(objectStoreItem));
//
//		if ( ! cacheLocation.toFile().exists() ) {
//			cacheLocation.toFile().mkdir();
//		}
//		
//		return cacheLocation;
	}

	private void showError(final Exception e) {
	
		window.getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				PluginMessage.openError(window.getShell(), HANDLER_NAME, 
						MessageFormat.format(FAILED_MESSAGE, document.getName() ) , e );
			}
		} );
	}

	private void showMessage( final String message ) {
		
		window.getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(window.getShell(), HANDLER_NAME, message);
			}
		} );
	}
}
