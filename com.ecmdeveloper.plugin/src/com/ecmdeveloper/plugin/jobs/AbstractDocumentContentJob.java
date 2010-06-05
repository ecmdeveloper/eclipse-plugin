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

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.GetContentInfoTask;
import com.ecmdeveloper.plugin.model.tasks.GetContentTask;
import com.ecmdeveloper.plugin.util.PluginMessage;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class AbstractDocumentContentJob extends Job {

	private static final String GET_CONTENT_INFORMATION_MESSAGE = "Getting content information for document \"{0}\"";
	private static final String SELECT_CONTENT_ELEMENT_TITLE = "Select Content Element";
	private static final String SELECT_CONTENT_ELEMENT_MESSAGE = "Select the content elements of the document \"{0}\":";
	private static final String NO_CONTENT_MESSAGE = "Document \"{0}\" contains no content";
	private static final String FAILED_MESSAGE = "Gettting content of document \"{0}\" failed";
	private static final String GETTING_CONTENT_ELEMENT_MESSAGE = "Getting content for document \"{0}\" content element {1}";
	
	protected Document document;
	protected IWorkbenchWindow window;
	protected Map<String, Integer> contentElementsMap;

	public AbstractDocumentContentJob(String name, Document document, IWorkbenchWindow window ) {
		super(name);
		this.document = document;
		this.window = window;
	}

	protected Collection<Integer> getContentElements(IProgressMonitor monitor) {
	 
		String subTask = MessageFormat.format( GET_CONTENT_INFORMATION_MESSAGE, document.getName() );
		monitor.subTask( subTask );
		
		Collection<Integer> contentElements = new ArrayList<Integer>();
	
		try {
			contentElementsMap = getContentElementsMap();
			
			if ( contentElementsMap.size() == 0 ) {
				showMessage( MessageFormat.format( NO_CONTENT_MESSAGE, document.getName() ) );
			} else 	if ( contentElementsMap.size() > 1 ) {
				selectContentElements(contentElementsMap, contentElements );
			} else {
				contentElements.add( new Integer(0) );
			}
		} catch ( final Exception e ) {
			showError(MessageFormat.format(FAILED_MESSAGE, document.getName() ), e );
		}

		return contentElements;
	}

	private Map<String,Integer> getContentElementsMap() throws ExecutionException {
		GetContentInfoTask task = new GetContentInfoTask(document);
		ObjectStoresManager.getManager().executeTaskSync(task);
		Map<String,Integer> contentElementsMap = task.getContentElementsMap();
		return contentElementsMap;
	}

	private void selectContentElements(Map<String, Integer> contentElementsMap, Collection<Integer> contentElements ) {
	
		final ListSelectionDialog dialog = getContentSelectionDialog(contentElementsMap);
	
		window.getShell().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				dialog.open();
			}
		} );
		
		if ( dialog.getReturnCode() == Dialog.OK ) {
			
			for ( Object result : dialog.getResult() ) {
				contentElements.add( contentElementsMap.get( result ) );
			}
		}
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
	
	protected void showError(final String message, final Exception e) {
		
		window.getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				PluginMessage.openError(window.getShell(), getName(), 
						message , e );
			}
		} );
	}

	protected void showMessage( final String message ) {
		
		window.getWorkbench().getDisplay().syncExec( new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(window.getShell(), getName(), message);
			}
		} );
	}
	
	protected String getContentElementFilename(Integer contentElement ) {
		if ( contentElementsMap == null || contentElement == null) {
			return null;
		}
		
		for ( String key : contentElementsMap.keySet() ) {
			Integer value = contentElementsMap.get(key);
			if ( value.equals( contentElement ) ) {
				int firstSpaceIndex = key.indexOf(" ");
				if ( firstSpaceIndex > 0 ) {
					return key.substring(firstSpaceIndex + 1 );
				} else {
					return null;
				}
			}
		}
		
		return null;
	}

	protected InputStream getContentStream(Integer contentElement, IProgressMonitor monitor) throws ExecutionException {
		
		String subTask = MessageFormat.format( GETTING_CONTENT_ELEMENT_MESSAGE, document.getName(), contentElement );
		monitor.subTask( subTask );

		GetContentTask task = new GetContentTask((Document) document, contentElement );
		ObjectStoresManager.getManager().executeTaskSync(task);
		
		InputStream contentStream = task.getContentStream();
		return contentStream;
	}
}
