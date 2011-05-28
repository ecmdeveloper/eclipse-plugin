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

package com.ecmdeveloper.plugin.search.ui;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;

/**
 * @author ricardo.belfor
 *
 */
public class SearchResultPage extends Page implements ISearchResultPage {

	private String id;
	private QuerySearchResult searchResult;
	private TableViewer viewer;
	private Composite viewerContainer;
	private PageBook pagebook;
	private ISearchResultViewPart viewPart;
	private IQueryListener fQueryListener;
	private SearchResultLabelProvider labelProvider;
	private MenuManager menuManager;
	private SelectionProviderAdapter viewerAdapter;

	private class SelectionProviderAdapter implements ISelectionProvider, ISelectionChangedListener {
		private ArrayList fListeners= new ArrayList(5);
		
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.add(listener);
		}

		public ISelection getSelection() {
			return viewer.getSelection();
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			fListeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			viewer.setSelection(selection);
		}

		public void selectionChanged(SelectionChangedEvent event) {
			// forward to my listeners
			SelectionChangedEvent wrappedEvent= new SelectionChangedEvent(this, event.getSelection());
			for (Iterator listeners= fListeners.iterator(); listeners.hasNext();) {
				ISelectionChangedListener listener= (ISelectionChangedListener) listeners.next();
				listener.selectionChanged(wrappedEvent);
			}
		}
	}
	
	@Override
	public void createControl(Composite parent) {
//		pagebook = new PageBook(parent, SWT.NULL);
//		pagebook.setLayoutData(new GridData(GridData.FILL_BOTH));
//		viewerContainer = new Composite(pagebook, SWT.NULL);
//		viewerContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
////		viewerContainer.setSize(100, 100);
//		viewerContainer.setLayout(new FillLayout());
		
		viewerContainer = new Composite(parent, SWT.NO_FOCUS);
		viewerContainer.setLayout(new FillLayout());

		createMenuManager();		
		
		//NewSearchUI.addQueryListener(fQueryListener);
		
		viewerAdapter = new SelectionProviderAdapter();
		getSite().setSelectionProvider(viewerAdapter);
		getSite().registerContextMenu(viewPart.getViewSite().getId(), menuManager, viewerAdapter);
		
		createViewer(viewerContainer);
	}

	private void createMenuManager() {
		menuManager = new MenuManager("#PopUp"); //$NON-NLS-1$
		menuManager.setRemoveAllWhenShown(true);
		menuManager.setParent(getSite().getActionBars().getMenuManager());
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				SearchView.createContextMenuGroups(mgr);
				fillContextMenu(mgr);
				viewPart.fillContextMenu(mgr);
			}
		});
	}

	protected void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator("edit") );
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator("other"));		
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		labelProvider = new SearchResultLabelProvider();
		viewer.setLabelProvider( labelProvider );
		viewer.setContentProvider( new SearchResultContentProvider() );
		viewer.addSelectionChangedListener(viewerAdapter);
		
		Table table = viewer.getTable();
		
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		
		table.setHeaderVisible(true);
        TableColumn column= new TableColumn(table, SWT.LEFT);
        column.setText("Class");
        column.setWidth(200);
	}

	@Override
	public Control getControl() {
		return viewerContainer;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public void setID(String id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		if (searchResult == null)
			return ""; //$NON-NLS-1$
		return searchResult.getLabel();
	}

	@Override
	public void setInput(ISearchResult searchResult, Object uiState) {
		
		if ( searchResult != null && !(searchResult instanceof QuerySearchResult) ) {
			return;
		}
		
		if ( this.searchResult != null ) {
			disconnectViewer();
		}
		
		if ( searchResult != null ) {

			this.searchResult = (QuerySearchResult) searchResult;
			connectViewer(this.searchResult);
			viewer.refresh();
			addSearchListener();
		}
	}

	private void addSearchListener() {
		this.searchResult.addListener( new ISearchResultListener() {

			@Override
			public void searchResultChanged(SearchResultEvent e) {
				
				System.out.println("searchResultChanged: " + e.toString() );
				
				getSite().getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						Table table = viewer.getTable();
						table.setRedraw(false);
						try {
							viewer.refresh();
						}
						finally {
							table.setRedraw(true);
						}
					};
				});
			}
		});
	}

	private void connectViewer(QuerySearchResult search) {
		
		if ( search != null ) {
			removeTableColumns();
			Table table = viewer.getTable();
			int index = 0;
			for ( String columnName : search.getColumnNames() ) {
		        TableColumn column= new TableColumn(table, SWT.LEFT);
		        column.setText( columnName );
		        column.setWidth(200);
		        labelProvider.connectIndexToName(index++, columnName);
			}
		}

		viewer.setInput(search);
	}

	private void disconnectViewer() {
		removeTableColumns();
		viewer.setInput(null);
	}

	private void removeTableColumns() {
		Table table = viewer.getTable();
		for ( TableColumn column : table.getColumns() ) {
			column.dispose();
		}
	}
	
	@Override
	public void setViewPart(ISearchResultViewPart viewPart) {
		this.viewPart = viewPart;
	}

	@Override
	public void dispose() {
		super.dispose();
		//NewSearchUI.removeQueryListener(fQueryListener);
	}

	@Override
	public void setFocus() {
		Control control = viewer.getControl();
		if (control != null && !control.isDisposed())
			control.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultPage#getUIState()
	 */
	@Override
	public Object getUIState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveState(IMemento memento) {
	}

	@Override
	public void restoreState(IMemento memento) {
	}
}
