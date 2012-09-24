/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.security.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.security.jobs.RefreshSecurityEditorJob;
import com.ecmdeveloper.plugin.security.jobs.SaveAccessControlEntriesJob;
import com.ecmdeveloper.plugin.security.util.PluginLog;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityEditor extends FormEditor implements PropertyChangeListener {

	public static final String EDITOR_ID = "com.ecmdeveloper.plugin.security.securityEditor";
	
	private SecurityPage securityPage;
	private IObjectStoreItem objectStoreItem;
//	private IAccessControlEntries accessControlEntries;
	private boolean isPageModified;

	@Override
	protected void addPages() {
		try {
			addSecurityPage();
			objectStoreItem = (IObjectStoreItem) getEditorInput().getAdapter( IObjectStoreItem.class);
			getAccessControlEntries().addPropertyChangeListener(this);
			isPageModified = false;
			
			updateTitle();
		} catch (PartInitException e) {
			PluginLog.error( e );
		}
	}

	private void updateTitle() {
		setPartName( objectStoreItem.getName() );
		setTitleToolTip( objectStoreItem.getName() );
	}

	private void addSecurityPage() throws PartInitException {
		securityPage = new SecurityPage(this);
		addPage(securityPage);
	}

	@Override
	protected void setActivePage(int pageIndex) {
		super.setActivePage(pageIndex);
		securityPage.refreshFormContent();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		Job job = new SaveAccessControlEntriesJob(objectStoreItem, getAccessControlEntries(), getSite().getShell() );
		job.setUser(true);
		job.addJobChangeListener( new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				if ( event.getResult().isOK() ) {
					isPageModified = false;
					doRefresh();
				}
			} } );
		job.schedule();
	}

	private IAccessControlEntries getAccessControlEntries() {
		return ((SecurityEditorInput)getEditorInput()).getAccessControlEntries();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if ( !isPageModified ) {
			isPageModified = true;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public void dispose() {
		getAccessControlEntries().removePropertyChangeListener(this);
		super.dispose();
	}

	@Override
	public boolean isDirty() {
		return isPageModified;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);

		if ( ! (input instanceof SecurityEditorInput ) )
	         throw new PartInitException( "Invalid Input: Must be CodeModuleEditorInput");
		
		site.setSelectionProvider( new ISelectionProvider() {

			@Override
			public void addSelectionChangedListener( ISelectionChangedListener listener) {
				// No changing selection
			}

			@Override
			public ISelection getSelection() {
				return new StructuredSelection( getEditorInput().getAdapter( IObjectStoreItem.class ) );
			}

			@Override
			public void removeSelectionChangedListener(ISelectionChangedListener listener) {
				// No changing selection
			}

			@Override
			public void setSelection(ISelection selection) {
				// No changing selection
			}
		});
	}

	protected void doRefresh() {

		if ( isDirty() ) {
			String message = "This editor contains changes, ary you sure you want to refresh?";
			if ( !MessageDialog.openConfirm(getSite().getShell(), getTitle(), message ) ) {
				return;
			}	
		}
		getAccessControlEntries().removePropertyChangeListener(this);
		
		try {
			final Shell shell = getSite().getShell();
			Job job = new RefreshSecurityEditorJob((SecurityEditorInput) getEditorInput(), shell );
			job.setUser(true);
			job.addJobChangeListener( new JobChangeAdapter() {

				@Override
				public void done(IJobChangeEvent event) {
					if ( event.getResult().isOK() ) {
						
						getAccessControlEntries().addPropertyChangeListener(SecurityEditor.this);
						shell.getDisplay().syncExec( new Runnable() {

							@Override
							public void run() {
								securityPage.refreshFormContent();
								isPageModified = false;
								firePropertyChange(IEditorPart.PROP_DIRTY);
							} } );
					}
				}} );
			job.schedule();
		} catch (Exception exception) {
			PluginLog.error( exception );
		}
	}
}
