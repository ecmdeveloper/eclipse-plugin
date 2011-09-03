/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.ui.views;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * @author ricardo.belfor
 *
 */
public class ContentView extends ViewPart {

	public ContentView() {
		// TODO Auto-generated constructor stub
	}

	private TableViewer viewer;
	private TableColumn pathColumn;
	private TableColumn nameColumn;
	private TableColumn objectStoreColumn;
	
	@Override
	public void createPartControl(Composite parent) {
		createTableViewer(parent);
		hookContextMenu();
		hookMouse();
	}

	private void createTableViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION);
		
		final Table table = viewer.getTable();
		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		layout.setColumnData(nameColumn, new ColumnWeightData(1) );

		pathColumn = new TableColumn(table, SWT.LEFT);
		pathColumn.setText("Path");
		layout.setColumnData(pathColumn, new ColumnWeightData(1) );
		
		objectStoreColumn = new TableColumn(table, SWT.LEFT);
		objectStoreColumn.setText("Object Store");
		layout.setColumnData(objectStoreColumn, new ColumnWeightData(1) );

		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		viewer.setContentProvider( new ArrayContentProvider() );
		viewer.setLabelProvider( new LabelProvider() );
		viewer.setInput( new String[] {"Hello", "World" } );

		getSite().setSelectionProvider(viewer);
	}

	private void hookContextMenu() {
		
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ContentView.this.fillContextMenu(manager);
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

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void hookMouse() {
//		viewer.getTable().addMouseListener(new MouseAdapter() {
//			public void mouseDoubleClick(MouseEvent e) {
//				try {
//					IHandlerService handlerService = (IHandlerService) getSite().getService(
//							IHandlerService.class);
//					handlerService.executeCommand(EditTrackedFileHandler.ID, null);
//				} catch (Exception exception) {
//					MessageDialog.openError(getSite().getShell(), "", exception
//							.getLocalizedMessage());
//				}
//			}
//		});
	}
	
}
