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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.ide.IDE;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.GetContentAsFileTask;
import com.ecmdeveloper.plugin.model.tasks.GetContentInfoTask;
import com.ecmdeveloper.plugin.util.ContentCache;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ViewDocumentJob extends Job {

	private static final String SELECT_CONTENT_ELEMENT_TITLE = "Select Content Element";
	private static final String SELECT_CONTENT_ELEMENT_MESSAGE = "Select the content elements of the document \"{0}\"\nto view:";
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
			final Map<String, Integer> contentElementsMap = getContentElementsMap();
			
			if ( contentElementsMap.size() == 0 ) {
				showMessage( MessageFormat.format( NO_CONTENT_MESSAGE, document.getName() ) );
				return Status.OK_STATUS;
			}

			if ( contentElementsMap.size() > 1 ) {
				viewMultipleContentElements(contentElementsMap);
			} else {
				viewSingleContentElement();
			}
		} catch ( final Exception e ) {
			showError(e);
		}
		return Status.OK_STATUS;
	}

	private void viewSingleContentElement() throws ExecutionException {
		IFileStore documentFile = getDocumentFile(0);
		openFileInEditor(documentFile);
	}

	private void viewMultipleContentElements(final Map<String, Integer> contentElementsMap) {
		final ListSelectionDialog dialog = getContentSelectionDialog(contentElementsMap);

		window.getShell().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if ( dialog.open() == Dialog.OK ) {
					for ( Object result : dialog.getResult() ) {
						viewContentElement(contentElementsMap.get( result ) );
					}
				}
			}

			private void viewContentElement(int index) {
				try {
					IFileStore documentFile = getDocumentFile( index );
					openFileInEditor(documentFile);
				} catch (ExecutionException e) {
					showError(e);
				}
			} }
		);
	}

	private ListSelectionDialog getContentSelectionDialog( Map<String, Integer> contentElementsMap) {
		String message = MessageFormat.format( SELECT_CONTENT_ELEMENT_MESSAGE, document.getName() );
		LabelProvider labelProvider = new LabelProvider();
		
		ArrayList<String> input = new ArrayList<String>();
		input.addAll( contentElementsMap.keySet() );
		Collections.sort(input);
		
		ListSelectionDialog dialog = new ListSelectionDialog(window.getShell(),
				input, new ArrayContentProvider(), labelProvider, message);
		dialog.setTitle( SELECT_CONTENT_ELEMENT_TITLE );
		return dialog;
	}

	private Map<String,Integer> getContentElementsMap() throws ExecutionException {
		GetContentInfoTask task = new GetContentInfoTask(document);
		ObjectStoresManager.getManager().executeTaskSync(task);
		Map<String,Integer> contentElementsMap = task.getContentElementsMap();
		return contentElementsMap;
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

	private IFileStore getDocumentFile(int index) throws java.util.concurrent.ExecutionException {

		IPath tempFolderPath = getTempFolderPath(document);

		GetContentAsFileTask task = new GetContentAsFileTask((Document) document, tempFolderPath
				.toOSString(), index);
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
