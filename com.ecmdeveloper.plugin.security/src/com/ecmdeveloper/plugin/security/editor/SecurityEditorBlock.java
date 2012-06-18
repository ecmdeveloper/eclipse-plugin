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

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;

import com.ecmdeveloper.plugin.core.model.constants.AccessControlEntrySource;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntries;
import com.ecmdeveloper.plugin.core.model.security.IAccessControlEntry;
import com.ecmdeveloper.plugin.security.Activator;
import com.ecmdeveloper.plugin.security.util.IconFiles;
import com.ecmdeveloper.plugin.security.util.PluginLog;

/**
 * @author ricardo.belfor
 *
 */
public class SecurityEditorBlock extends MasterDetailsBlock {

	private static final String PRINCIPAL = "Principal";
	private static final String REFRESH_LABEL = "Refresh Security";
	private static final String ADD_GROUP_LABEL = "Add Entry";
	private static final String DELETE_GROUP_LABEL = "Delete Entry";
	
	private static final String REFRESH_PROPERTIES_COMMAND_ID = "com.ecmdeveloper.plugin.refreshProperties";
	private static final String DESCRIPTION_TEXT = "To edit an access contol entry select an item from the list.";
	private static final String TITLE_BAR_TEXT = "Access Control Entries";
	
	private FormPage page;
	private TreeViewer viewer;
	
	public SecurityEditorBlock(FormPage page) {
		this.page = page;
	}

	public void setInput(Object input) {
		IEditorInput  editorInput = (IEditorInput) input;
		IAccessControlEntries accessControlEntries = (IAccessControlEntries) editorInput
				.getAdapter(IAccessControlEntries.class);
		viewer.setInput(accessControlEntries);
		viewer.expandAll();
	}
	
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {

		FormToolkit toolkit = managedForm.getToolkit();
		Section section = createSection(parent, toolkit);
		//createToolbar(section);
		Composite client = createClient(toolkit, section);

		Composite buttons = createLinksClient(client);
		createAddGroupLink(toolkit, buttons);
		createDeleteGroupLink(toolkit, buttons);
		createRefreshLink(toolkit, buttons);
		
		final SectionPart spart = new SectionPart(section);
		Tree tree = createTree(toolkit, client);
		managedForm.addPart(spart);
		createTableViewer(managedForm, spart, tree);
		section.setClient(client);
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
				performAddGroup();
			}
	    });
	}

	protected void performAddGroup() {
		viewer.refresh();
	}

	private void createDeleteGroupLink(FormToolkit toolkit, Composite client) {
		ImageHyperlink link = toolkit.createImageHyperlink(client, SWT.WRAP);
		link.setText(DELETE_GROUP_LABEL);
		link.setImage( Activator.getImage(IconFiles.GROUP_DELETE) );
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				performDeleteGroup();
			}
	    });
	}
	
	protected void performDeleteGroup() {
		// TODO Auto-generated method stub
		
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
		IHandlerService handlerService = (IHandlerService) page.getSite().getService(IHandlerService.class);
		try {
			handlerService.executeCommand(REFRESH_PROPERTIES_COMMAND_ID, null );
		} catch (Exception exception) {
			PluginLog.error( exception );
		}
	}

	private void createToolbar(Section section) {
		ToolBar tbar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createFilterReadOnlyButton(tbar);
//		createRefreshButton(tbar);        
        section.setTextClient(tbar);
	}

//	private void createFilterReadOnlyButton(ToolBar tbar) {
//		ToolItem titem = new ToolItem(tbar, SWT.CHECK );
//        titem.setImage(Activator.getImage( IconFiles.READ_ONLY ));
//        titem.setToolTipText(FILTER_LABEL);
//        titem.setSelection( filterReadOnly );
//        titem.addSelectionListener( new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				ToolItem toolItem = (ToolItem) e.getSource();
//				filterReadOnly = toolItem.getSelection();
//				viewer.refresh();
//			}
//			} 
//        );
//	}

//	private void createRefreshButton(ToolBar tbar) {
//		ToolItem titem = new ToolItem(tbar, SWT.PUSH );
//        titem.setToolTipText(REFRESH_LABEL);
//        titem.addSelectionListener( new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				performRefresh();
//			}} 
//        );
//        titem.setImage(Activator.getImage( IconFiles.REFRESH ));
//	}

	private Tree createTree(FormToolkit toolkit, Composite client) {
		
		Tree tree = toolkit.createTree(client, SWT.FULL_SELECTION | SWT.BORDER );
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		tree.setLayoutData(gd);

		return tree;
	}

	private void createTableViewer(final IManagedForm managedForm, final SectionPart sectionPart,
			Tree tree) {
	
		viewer = new TreeViewer(tree);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(sectionPart, event.getSelection());
			}
		});
		ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);
		
//		createSourceAndTypeColumn();
//		createPrincipalColumn();		
		
		viewer.setLabelProvider( new SecurityLabelProvider() );
		viewer.setContentProvider( new SecurityContentProvider() );
		tree.setHeaderVisible(true);
		
//		viewer.addFilter( new ViewerFilter() {
//
//			@Override
//			public boolean select(Viewer viewer, Object parentElement, Object element) {
//				if ( ((Property) element).isSettableOnEdit() || ! filterReadOnly ) {
//					return true;
//				}
//				return false;
//			}
//			} 
//		);
	}

//	private void createSourceAndTypeColumn() {
//		final TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
//		col.getColumn().setWidth(20);
//		col.getColumn().setText("");
//		col.setLabelProvider( getSourceAndTypeLabelProvider() );
//	}
	
	private CellLabelProvider getSourceAndTypeLabelProvider() {
		
		return new ColumnLabelProvider() {

			@Override
			public Image getImage(Object element) {
				final IAccessControlEntry ace = (IAccessControlEntry) element;
				if ( ace.getSource().equals( AccessControlEntrySource.INHERITED ) ) {
					return Activator.getImage(IconFiles.INHERITED);
				}
				return null;
			}

			@Override
			public String getToolTipText(Object element) {
				final IAccessControlEntry ace = (IAccessControlEntry) element;
				return ace.getSource().toString();
			}

			@Override
			public String getText(Object element) {
				return "";
			}
		};
	}

//	private void createPrincipalColumn() {
//		final TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
//		col.getColumn().setWidth(200);
//		col.getColumn().setText(PRINCIPAL);
//		col.setLabelProvider( getPrincipalLabelProvider() );
//	}
//
//	private ColumnLabelProvider getPrincipalLabelProvider() {
//
//		return new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				IAccessControlEntry ace = (IAccessControlEntry) element;
//				return ace.getPrincipal() + "\r\n" + "nog een keer!";
//			}
//
//			@Override
//			public Image getImage(Object element) {
//				IAccessControlEntry ace = (IAccessControlEntry) element;
//				if ( ace.isGroup() == null || ace.isGroup() ) {
//					return Activator.getImage(IconFiles.GROUP);
//				} else {
//					return Activator.getImage(IconFiles.USER);
//				}
//			}
//
//			@Override
//			public String getToolTipText(Object element) {
//				return element.toString();
//			}
//		};
//	}

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
//		final ScrolledForm form = managedForm.getForm();
//		Action commitAction = new Action("hor", Action.AS_PUSH_BUTTON) { 
//			public void run() {
//			}
//		};
//		commitAction.setToolTipText("Commit changes");
//		commitAction.setImageDescriptor( Activator.getImageDescriptor( IconFiles.READ_ONLY ) );
//		form.getToolBarManager().add(commitAction);
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {

		detailsPart.setPageProvider( new SecurityInputDetailsPageProvider() );
	}
}
