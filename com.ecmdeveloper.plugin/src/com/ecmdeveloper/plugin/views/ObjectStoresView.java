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
package com.ecmdeveloper.plugin.views;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import com.ecmdeveloper.plugin.core.model.tasks.TaskManager;
import com.ecmdeveloper.plugin.util.PluginLog;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ObjectStoresView extends ViewPart {
	
	private static final String DOUBLE_CLICK_HANDLER_ID = "com.ecmdeveloper.plugin.objectStoresViewDoubleClick";
	
	private TreeViewer viewer;

	private ObjectStoresViewContentProvider contentProvider;
	
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public ObjectStoresView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		contentProvider = new ObjectStoresViewContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ObjectStoreItemLabelProvider());
		viewer.setInput( TaskManager.getInstance() );
		viewer.setSorter( new ObjectStoresViewSorter() );
		hookContextMenu();
		hookMouse();
		
		getSite().setSelectionProvider(viewer);
	}

	private void hookContextMenu() {
		
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ObjectStoresView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

   private void hookMouse() {
		viewer.getTree().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				handleDoubleClick();
			}
		});
	}
	
	private void handleDoubleClick() {
		
        IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
        Object element = selection.getFirstElement();

        if (viewer.isExpandable(element)) {
        	viewer.setExpandedState(element, !viewer.getExpandedState(element));
        } else {
			try {
				IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
				handlerService.executeCommand(DOUBLE_CLICK_HANDLER_ID, null );
			} catch (Exception exception) {
				PluginLog.error( exception );
			}
        }
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator("edit") );
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator("other"));		
		manager.add(new Separator("group.show") );
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public ObjectStoresViewContentProvider getContentProvider() {
		return contentProvider;
	}
}