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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ecmdeveloper.plugin.core.model.constants.PrincipalType;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.core.model.security.IPrincipal;
import com.ecmdeveloper.plugin.core.model.security.IRealm;
import com.ecmdeveloper.plugin.core.model.security.ISecurityPrincipal;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.dialogs.PrincipalSelectionDialog;
import com.ecmdeveloper.plugin.security.jobs.RefreshSecurityEditorJob;
import com.ecmdeveloper.plugin.security.mock.PrincipalMock;
import com.ecmdeveloper.plugin.security.util.IconFiles;
import com.ecmdeveloper.plugin.security.util.PluginLog;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityEditorBlock extends MasterDetailsBlock {

	private static final String DELETE_MENU_TEXT = "&Delete";
	private static final String ADD_MENU_TEXT = "&Add";
	private static final String PRINCIPAL_CONTAINS_NO_EDITABLE_ENTRIES = "The principal ''{0}'' contains no deletable access control entries.";
	private static final String CONFIRM_PRINCIPAL_ENTRIES_DELETE = "Are you sure you want to delete all the deletable access control entries for principal ''{0}''?";
	private static final String CONFIRM_ENTRY_DELETE = "Are you sure you want to delete this access control entries for principal ''{0}''?";
	private static final String NOT_EDITABLE_ENTRY = "This access control entry cannot be deleted.";

	private static final String SECURITY_EDITOR_TITLE = "Security editor";
	private static final String REFRESH_LABEL = "Refresh Security";
	private static final String ADD_GROUP_LABEL = "Add Entry";
	private static final String DELETE_GROUP_LABEL = "Delete Entry";
	
	private static final String REFRESH_PROPERTIES_COMMAND_ID = "com.ecmdeveloper.plugin.refreshProperties";
	private static final String DESCRIPTION_TEXT = "To edit an access contol entry select an item from the list.";
	private static final String TITLE_BAR_TEXT = "Access Control Entries";
	
	private FormPage page;
	private TreeViewer viewer;
	private IAccessControlEntries accessControlEntries;
	private Collection<IRealm> realms;
	private SecurityEditorInput editorInput;
	
	public SecurityEditorBlock(FormPage page) {
		this.page = page;
	}

	public void setInput(Object input) {
		editorInput = (SecurityEditorInput) input;
		accessControlEntries = (IAccessControlEntries) editorInput.getAdapter(IAccessControlEntries.class);
		realms = editorInput.getRealms();
		viewer.setInput(accessControlEntries);
		viewer.expandAll();
	}
	
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {

		FormToolkit toolkit = managedForm.getToolkit();
		Section section = createSection(parent, toolkit);
		Composite client = createClient(toolkit, section);

		Composite buttons = createLinksClient(client);
		createAddGroupLink(toolkit, buttons);
		createDeleteGroupLink(toolkit, buttons);
		createRefreshLink(toolkit, buttons);
		
		final SectionPart spart = new SectionPart(section);
		Tree tree = createTree(toolkit, client);
		managedForm.addPart(spart);
		createTreeViewer(managedForm, spart, tree);
		createContextMenu(client);
		section.setClient(client);
	}

	private void createContextMenu(Composite client) {
		final Menu menu = new Menu(client);
		
		menu.addMenuListener( new MenuAdapter() {

			@Override
			public void menuShown(MenuEvent e) {
		        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				MenuItem item = menu.getItem(1);
				item.setEnabled( !selection.isEmpty() );
			}
		} );
		
		createAddMenuItem(menu);
		createDeleteMenuItem(menu);
		viewer.getTree().setMenu( menu );
	}

	private void createAddMenuItem(final Menu menu) {
		
		MenuItem menuItem = new MenuItem(menu, SWT.PUSH );
		menuItem.setText(ADD_MENU_TEXT);
		menuItem.setImage( Activator.getImage(IconFiles.GROUP_ADD) );
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performAddAccessControlEntry();
			}
		} );
	}

	private void createDeleteMenuItem(final Menu menu) {
		
		MenuItem menuItem = new MenuItem(menu, SWT.PUSH );
		menuItem.setText(DELETE_MENU_TEXT);
		menuItem.setImage( Activator.getImage(IconFiles.GROUP_DELETE) );
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performDeleteSelection();
			}
		} );
	}
	
	private Composite createLinksClient(Composite client) {
		Composite buttons = new Composite(client, SWT.NONE );
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		fillLayout.spacing = 10;
		buttons.setLayout(fillLayout);
		return buttons;
	}

	private void createAddGroupLink(FormToolkit toolkit, Composite client) {
		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText(ADD_GROUP_LABEL);
		link.setImage( Activator.getImage(IconFiles.GROUP_ADD) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				performAddAccessControlEntry();
			}
	    });
	}

	protected void performAddAccessControlEntry() {

		final IPrincipal principal = getPrincipal();

		if ( principal != null) {
			IAccessControlEntry accessControlEntry = accessControlEntries.addAccessControlEntry(principal);
			viewer.refresh();
			viewer.expandToLevel(accessControlEntry.getPrincipal(), 2);
			viewer.setSelection( new StructuredSelection( accessControlEntry ) );
	
			viewer.refresh();
		}
	}

	private IPrincipal getPrincipal() {
		
		final IPrincipal initialPrincipal = getInitialPrincipal();
		final Shell shell = getShell();

		if ( realms != null) {
			final FilteredItemsSelectionDialog dialog = new PrincipalSelectionDialog(shell, realms, initialPrincipal );
			
			if ( dialog.open() == Window.OK ) {
				return (IPrincipal) dialog.getFirstResult();
			}
		} else {
			String oldName = initialPrincipal != null ? initialPrincipal.getName() : "";
			InputDialog inputDialog = new InputDialog( shell, SECURITY_EDITOR_TITLE, "Enter the name of the principal", oldName, null );
			int open = inputDialog.open();
			
			if ( open == InputDialog.OK ) {
				// FIXME
				return new PrincipalMock( inputDialog.getValue(), PrincipalType.UNKNOWN );
				
			}
		}
		return null;
	}

	private IPrincipal getInitialPrincipal() {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		IPrincipal initialPrincipal = null;
		if ( !selection.isEmpty() ) {
			Object object = selection.iterator().next();
			if (object instanceof ISecurityPrincipal ) {
				initialPrincipal = (ISecurityPrincipal) object;
			} else if ( object instanceof IAccessControlEntry ) {
				initialPrincipal = ((IAccessControlEntry) object).getPrincipal();
			}
		}
		return initialPrincipal;
	}

	private void createDeleteGroupLink(FormToolkit toolkit, Composite client) {
		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText(DELETE_GROUP_LABEL);
		link.setImage( Activator.getImage(IconFiles.GROUP_DELETE) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				if ( performDeleteSelection() ) {
					viewer.refresh();
				}
			}
	    });
	}
	
	protected boolean performDeleteSelection() {
		boolean result = false;
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if ( !selection.isEmpty() ) {
			Object object = selection.iterator().next();
			if (object instanceof ISecurityPrincipal ) {
				result = deletePrincipalEntries((ISecurityPrincipal) object );
			} else if ( object instanceof IAccessControlEntry ) {
				result = deleteAccessControlEntry(object);
			}
		}
		
		if ( result ) {
			viewer.refresh();
		}
		return result;
	}

	private boolean deletePrincipalEntries(ISecurityPrincipal securityPrincipal) {
	
		List<IAccessControlEntry> deletableEntries = getDeletableEntries(securityPrincipal);
		if ( !deletableEntries.isEmpty() ) {
			String message = MessageFormat.format(CONFIRM_PRINCIPAL_ENTRIES_DELETE, securityPrincipal.getDisplayName() );
			if ( MessageDialog.openConfirm(getShell(), SECURITY_EDITOR_TITLE, message) ) {
				securityPrincipal.getAccessControlEntries().removeAll( deletableEntries );
				return true;
			}
		} else {
			String message = MessageFormat.format(PRINCIPAL_CONTAINS_NO_EDITABLE_ENTRIES, securityPrincipal.getDisplayName() );
			MessageDialog.openWarning(getShell(), SECURITY_EDITOR_TITLE, message);
		}
		return false;
	}

	private List<IAccessControlEntry> getDeletableEntries(ISecurityPrincipal securityPrincipal) {
		List<IAccessControlEntry> deletableEntries = new ArrayList<IAccessControlEntry>(); 
		for (IAccessControlEntry accessControlEntry : securityPrincipal.getAccessControlEntries() ) {
			if ( accessControlEntry.canDelete() ) {
				deletableEntries.add(accessControlEntry);
			}
		}
		return deletableEntries;
	}

	private boolean deleteAccessControlEntry(Object object) {
		IAccessControlEntry accessControlEntry = (IAccessControlEntry) object;
		
		if ( accessControlEntry.canDelete() ) {
			String message = MessageFormat.format(CONFIRM_ENTRY_DELETE, accessControlEntry.getPrincipal().getDisplayName());
			if ( MessageDialog.openConfirm(getShell(), SECURITY_EDITOR_TITLE, message) ) {
				ISecurityPrincipal securityPrincipal = accessControlEntry.getPrincipal();
				securityPrincipal.getAccessControlEntries().remove(accessControlEntry);
				return true;
			}
		} else {
			MessageDialog.openWarning(getShell(), SECURITY_EDITOR_TITLE, NOT_EDITABLE_ENTRY);
		}
		return false;
	}

	private void createRefreshLink(FormToolkit toolkit, Composite client) {
		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText(REFRESH_LABEL);
		link.setImage( Activator.getImage(IconFiles.REFRESH) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				performRefresh();
			}
	    });
	}
	
	protected void performRefresh() {
		((SecurityEditor)page.getEditor()).doRefresh();
	}

	private Tree createTree(FormToolkit toolkit, Composite client) {
		
		Tree tree = toolkit.createTree(client, SWT.FULL_SELECTION | SWT.BORDER );
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		tree.setLayoutData(gd);

		return tree;
	}

	private void createTreeViewer(final IManagedForm managedForm, final SectionPart sectionPart,
			Tree tree) {
	
		viewer = new TreeViewer(tree);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(sectionPart, event.getSelection());
			}
		});
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
		
		viewer.setLabelProvider( new SecurityLabelProvider() );
		viewer.setContentProvider( new SecurityContentProvider() );
		viewer.setSorter( new ViewerSorter() { } );
		tree.setHeaderVisible(false);
	}

	private Composite createClient(FormToolkit toolkit, Section section) {
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		client.setLayout(layout);
		toolkit.paintBordersFor(client);
		
		return client;
	}

	private Section createSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR );
		section.setText( MessageFormat.format( TITLE_BAR_TEXT, "TODO" ) );
		section.setDescription( DESCRIPTION_TEXT );
		section.marginWidth = 5;
		section.marginHeight = 5;
		return section;
	}
	
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		form.getToolBarManager().add( createTileHorizontalAction(form) );
		form.getToolBarManager().add( createTileVerticalAction(form) );
	}

	private Action createTileVerticalAction(final ScrolledForm form) {
		Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) {
			public void run() {
				sashForm.setOrientation(SWT.VERTICAL);
				form.reflow(true);
			}
		};
		vaction.setChecked(false);
		vaction.setToolTipText("Vertical orientation");
		vaction.setImageDescriptor(Activator.getImageDescriptor(IconFiles.VERTICAL) );
		return vaction;
	}

	private Action createTileHorizontalAction(final ScrolledForm form) {
		Action haction = new Action("hor", Action.AS_RADIO_BUTTON) {
			public void run() {
				sashForm.setOrientation(SWT.HORIZONTAL);
				form.reflow(true);
			}
		};
		haction.setChecked(true);
		haction.setToolTipText("Horizontal orientation");
		haction.setImageDescriptor(Activator.getImageDescriptor(IconFiles.HORIZONTAL) );
		return haction;
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {

		detailsPart.setPageProvider( new SecurityInputDetailsPageProvider(this) );
	}

	public void refresh(IAccessControlEntry accessControlEntry) {
		viewer.update(accessControlEntry, null);
	}
	
	public Shell getShell() {
		return Display.getCurrent().getActiveShell();
//		return page.getEditorSite().getShell();
	}
}
