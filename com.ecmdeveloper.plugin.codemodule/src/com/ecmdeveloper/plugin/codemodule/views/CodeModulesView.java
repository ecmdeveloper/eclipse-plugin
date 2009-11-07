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

package com.ecmdeveloper.plugin.codemodule.views;

import java.util.Iterator;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditor;
import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditorInput;
import com.ecmdeveloper.plugin.codemodule.editors.CodeModuleEditorUtils;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.model.CodeModulesManager;
import com.ecmdeveloper.plugin.codemodule.util.PluginLog;

public class CodeModulesView extends ViewPart {

	private TableViewer viewer;
	private TableColumn typeColumn;
	private TableColumn nameColumn;
	private TableColumn objectStoreColumn;
	
	public CodeModulesView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		createTableViewer(parent);
		hookMouse();
		hookContextMenu();
	}

	private void createTableViewer(Composite parent) {

		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION);
		
		final Table table = viewer.getTable();
		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("");
		layout.setColumnData(typeColumn, new ColumnPixelData(18) );

		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		layout.setColumnData(nameColumn, new ColumnWeightData(1) );

		objectStoreColumn = new TableColumn(table, SWT.LEFT);
		objectStoreColumn.setText("Object Store");
		layout.setColumnData(objectStoreColumn, new ColumnWeightData(1) );

		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		viewer.setContentProvider( new CodeModulesViewContentProvider() );
		viewer.setLabelProvider( new CodeModulesViewLabelProvider() );
		viewer.setInput( CodeModulesManager.getManager() );

		getSite().setSelectionProvider(viewer);
	}
	
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

   private void hookMouse() {
		viewer.getTable().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				openEditor(viewer.getSelection());
			}
		});
	}

	private void hookContextMenu() {
		
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CodeModulesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
   
	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator("edit") );
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator("other"));		
	}
	
   	public void openEditor(ISelection selection) {

		// Get the first element.

		if (!(selection instanceof IStructuredSelection))
			return;
		Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		if (!iter.hasNext())
			return;

		Object elem = iter.next();

		try {
			
			if ( ! CodeModuleEditorUtils.isEditorActive( getSite().getPage(), (CodeModuleFile) elem ) ) {
				IEditorInput input = new CodeModuleEditorInput( (CodeModuleFile) elem );
				String editorId = CodeModuleEditor.CODE_MODULE_EDITOR_ID;
				IDE.openEditor(getSite().getPage(), input, editorId);
			}
			
		} catch (PartInitException e) {
			PluginLog.error("Open editor failed" , e);
		}
   	}
}
