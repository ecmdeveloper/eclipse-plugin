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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.ecmdeveloper.plugin.compare.DocumentVersionCompareEditorInput;
import com.ecmdeveloper.plugin.jobs.GetDocumentVersionJob;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.model.tasks.GetContentInfoTask;
import com.ecmdeveloper.plugin.util.PluginMessage;
import com.filenet.api.constants.PropertyNames;

/**
 * @author Ricardo.Belfor
 *
 */
public class CompareDocumentVersionsHandler extends AbstractDocumentVersionHandler {

	@Override
	protected IJobChangeListener getJobChangeListener(Document document) {
		return new ComparedDocumentVersionsJobListener();
	}
	
	class ComparedDocumentVersionsJobListener extends JobChangeAdapter {

		private static final String HANDLER_NAME = "Compare Document Versions";
		private static final String TWO_VERSIONS_MESSAGE = "Selected two versions for comparison";
		private static final String COMPARE_TITLE_FORMAT = "Compare(''{0}'' - ''{1}'')";
		private static final String VERSION_NAME_FORMAT = "Version {1}.{2} {0}";
		private static final String SELECT_CONTENT_ELEMENT_TITLE = "Select Content Element";
		private static final String SELECT_CONTENT_ELEMENT_MESSAGE = "Select the content elements of the document \"{0}\" to view:";
		
		@Override
		public void done(IJobChangeEvent event) {

			if ( event.getResult().equals( Status.CANCEL_STATUS ) ) {
				return;
			}

			GetDocumentVersionJob job = (GetDocumentVersionJob) event.getJob();
			
			if ( job.getSelectedVersions().size() != 2 ) {
				PluginMessage.openErrorFromThread(window.getShell(), HANDLER_NAME, TWO_VERSIONS_MESSAGE, null);
				return;
			}
			
			Collection<Document> selectedVersions = job.getSelectedVersions();
			
			try {
				DocumentVersionCompareEditorInput editorInput = createCompareEditorInput(selectedVersions);
				openCompareEditor(editorInput);
			} catch ( InterruptedException ie ) {
				return;
			} catch ( Exception e ) {
				PluginMessage.openErrorFromThread(window.getShell(), HANDLER_NAME, e
						.getLocalizedMessage(), e);
			}
		}

		private void openCompareEditor(final DocumentVersionCompareEditorInput editorInput) {
			window.getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					CompareUI.openCompareEditor(editorInput);;
				}
			} );
		}

		private DocumentVersionCompareEditorInput createCompareEditorInput(
				Collection<Document> selectedVersions) throws Exception {

			Iterator<Document> iterator = selectedVersions.iterator();
			
			Document document1 = iterator.next();
			int contentIndex1 = getDocumentContentIndex(document1);
			
			Document document2 = iterator.next();
			int contentIndex2 = getDocumentContentIndex(document2);
			
			CompareConfiguration configuration = new CompareConfiguration();
			configuration.setLeftLabel( getDocumentLabel( document1 ) );
			configuration.setRightLabel( getDocumentLabel( document2 ) );

			DocumentVersionCompareEditorInput editorInput = new DocumentVersionCompareEditorInput(
					configuration, document1, contentIndex1, document2, contentIndex2 );

			String title = MessageFormat.format(COMPARE_TITLE_FORMAT, configuration
					.getLeftLabel(null), configuration.getRightLabel(null));
			editorInput.setTitle( title );
			return editorInput;
		}

		private int getDocumentContentIndex(Document document ) throws Exception {
			final Map<String, Integer> contentElementsMap = getContentElementsMap(document);

			if ( contentElementsMap.size() == 1 ) {
				return 0;
			}
			else if ( contentElementsMap.size() > 1 ) {

				final ElementListSelectionDialog dialog = getContentSelectionDialog(
						contentElementsMap, document);
				
				window.getShell().getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						dialog.open();
					}
				});
				
				if ( dialog.getReturnCode() == Dialog.OK ) {
					if ( dialog.getResult().length == 1 ) {
						return contentElementsMap.get( dialog.getResult()[0] );
					}
				} else {
					throw new InterruptedException();
				}
			}

			return -1;
		}
		
		private Map<String,Integer> getContentElementsMap(Document document) throws Exception {
			GetContentInfoTask task = new GetContentInfoTask(document);
			ObjectStoresManager.getManager().executeTaskSync(task);
			Map<String,Integer> contentElementsMap = task.getContentElementsMap();
			return contentElementsMap;
		}

		private String getDocumentLabel(Document document) {
			Object majorVersionNumber = document.getValue( PropertyNames.MAJOR_VERSION_NUMBER );
			Object minorVersionNumber = document.getValue( PropertyNames.MINOR_VERSION_NUMBER );
			return MessageFormat.format(VERSION_NAME_FORMAT, document.getName(),
					majorVersionNumber, minorVersionNumber);
		}

		private ElementListSelectionDialog getContentSelectionDialog( Map<String, Integer> contentElementsMap, Document document) {
			
			LabelProvider labelProvider = new LabelProvider();
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(window.getShell(), labelProvider );

			ArrayList<String> input = new ArrayList<String>();
			input.addAll( contentElementsMap.keySet() );
			Collections.sort(input);
			dialog.setElements( input.toArray() );
			
			dialog.setTitle( SELECT_CONTENT_ELEMENT_TITLE );

			String message = MessageFormat.format( SELECT_CONTENT_ELEMENT_MESSAGE, getDocumentLabel(document) );
			dialog.setMessage(message);
			
			return dialog;
		}
	}
}
