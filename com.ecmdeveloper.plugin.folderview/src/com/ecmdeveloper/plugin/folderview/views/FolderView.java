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

package com.ecmdeveloper.plugin.folderview.views;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import com.ecmdeveloper.plugin.folderview.Activator;
import com.ecmdeveloper.plugin.folderview.model.Categories;
import com.ecmdeveloper.plugin.folderview.model.Category;
import com.ecmdeveloper.plugin.folderview.model.MockItem;
import com.ecmdeveloper.plugin.folderview.utils.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class FolderView extends ViewPart {

	private TreeViewer viewer;
	private final List<String> visibleColumns = new ArrayList<String>();
	private final List<String> hiddenColumns = new ArrayList<String>();
	private final List<String> groupByColumns = new ArrayList<String>();
	
	private ArrayList<MockItem> items = new ArrayList<MockItem>();
	private Categories categories;
	private final FolderViewComparator viewerComparator = new FolderViewComparator();
	
	public FolderView() {

		items.add( createMockItem("Peter", "Griffin", "Male", 1, new Date(), "Family Guy") );
		items.add( createMockItem("Lois", "Griffin", "Female", 12, new Date(), "Family Guy") );
		items.add( createMockItem("Meg", "Griffin", "Female", 122, new Date(), "Family Guy") );
		items.add( createMockItem("Chris", "Griffin", "Male", 211, new Date(), "Family Guy") );
		items.add( createMockItem("Stewie", "Griffin", "Male", 21, new Date(), "Family Guy") );
		items.add( createMockItem("Brian", "Griffin", "Male", 200, new Date(), "Family Guy") );
		items.add( createMockItem("Glenn", "Quagmire", "Male", 12, new Date(), "Family Guy") );
		items.add( createMockItem("Cleveland", "Brown", "Male", 12, new Date(), "Family Guy") );
		items.add( createMockItem("Joe", "Swanson", "Male", 12, new Date(), "Family Guy") );
		items.add( createMockItem("Homer", "Simpson", "Male", 2, new Date(), "The Simpsons") );
		items.add( createMockItem("Marge", "Simpson", "Female", 2, new Date(), "The Simpsons") );
		items.add( createMockItem("Bart", "Simpson", "Male", 2, new Date(), "The Simpsons") );
		items.add( createMockItem("Lisa", "Simpson", "Female", 2, new Date(), "The Simpsons") );
		
		visibleColumns.addAll(items.get(0).getColumnNames() );

		updateModel();
	}

	private void updateModel() {
		categories = new Categories(items, groupByColumns);
	    viewer.setInput(categories);
		ArrayList<Object> roots = new ArrayList<Object>();
		roots.add(categories.getRoot());
		Categories.printTree(roots, "");
	}

	@Override
	public void createPartControl(Composite parent) {
		createTreeViewer(parent);
		String description = items.size() + " items";
		setContentDescription(description);
		viewer.expandAll();
	}

	private void createTreeViewer(Composite parent) {
	      viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION);
	      final Tree tree = viewer.getTree();
		TreeColumn[] currentColumns = new TreeColumn[0];
		updateColumns(currentColumns);
		viewer.setComparator(viewerComparator);
		
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		
	      viewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public Object[] getChildren(Object parentElement) {
				if ( parentElement instanceof Categories ) {
					Category root = ((Categories) parentElement).getRoot();
					return root.getChildren().toArray();
				} else if (parentElement instanceof Category ) {
					return ((Category) parentElement).getChildren().toArray();
				}

				return null;
			}

			@Override
			public Object getParent(Object element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				if ( element instanceof Categories ) {
					return true;
				} else if (element instanceof Category ) {
					return true;
				}
				return false;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub
				
			}
	    	  
	      });

	      viewer.setInput(categories);

	      getSite().setSelectionProvider(viewer);
	}

	private MockItem createMockItem(String firstName, String lastName, String gender, int luckyNumber, Date dayOfBirth, String show) {
		
		MockItem item = new MockItem();
		
		item.putValue("First Name", firstName);
		item.putValue("Last Name", lastName);
		item.putValue("Gender", gender);
		item.putValue("Lucky Number", new Integer(luckyNumber) );
		item.putValue("Day of Birth", dayOfBirth);
		item.putValue("Show", show);
		
		return item;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public List<String> getHiddenColumns() {
		return hiddenColumns;
	}

	public List<String> getVisibleColumns() {
		return visibleColumns;
	}

	public void setVisibleColumns(List<String> visibleColumns) {
		this.visibleColumns.clear();
		this.visibleColumns.addAll(visibleColumns);
	}

	public void setHiddenColumns(List<String> hiddenColumns) {
		this.hiddenColumns.clear();
		this.hiddenColumns.addAll(hiddenColumns);
	}

	public List<String> getGroupByColumns() {
		return groupByColumns;
	}

	public void setGroupByColumns(List<String> groupByColumns) {
		this.groupByColumns.clear();
		this.groupByColumns.addAll(groupByColumns);
	}

	public void updateColumns() {
		updateModel();
		updateColumns( viewer.getTree().getColumns() );
		viewer.refresh();
		viewer.expandAll();
	}
	
	public void updateColumns(TreeColumn[] currentColumns) {
		
		Tree tree = viewer.getTree();
		TableLayout layout = new TableLayout();

		String[] fields = visibleColumns.toArray( new String[visibleColumns.size()] );
		
//		IMemento columnWidths = null;
//		if (memento != null)
//			columnWidths = memento.getChild(TAG_COLUMN_WIDTHS);

		for (int i = 0; i < fields.length; i++) {

			final String markerField = fields[i];
			final int j = i;
			
			TreeViewerColumn column;
			if (i < currentColumns.length)
				column = new TreeViewerColumn(viewer, currentColumns[i]);
			else {
				column = new TreeViewerColumn(viewer, SWT.NONE);
				column.getColumn().setResizable(true);
				column.getColumn().setMoveable(true);
//				column.getColumn().addSelectionListener(getHeaderListener());
			}

//			column.getColumn().setData(MARKER_FIELD, markerField);

			column.setLabelProvider(new CellLabelProvider(){

				private final String columnName = markerField;
				private final int columnIndex = j;
				
				@Override
				public void update(ViewerCell cell) {
					if ( cell.getElement() instanceof MockItem) {
						MockItem item = (MockItem) cell.getElement();
						cell.setText( item.getValue(columnName).toString() );
					}
					
					if ( cell.getElement() instanceof Category) {
						if ( columnIndex == 0) {
							Category category = (Category) cell.getElement();
							String text = MessageFormat.format("{0}: {1}", category.getName(), category.getValueString() );
							cell.setText( text );
							int level = category.getLevel();
							int imageIndex = level % IconFiles.LABELS.length;
							cell.setImage( Activator.getImage( IconFiles.LABELS[imageIndex] ) );
						}
					}
					
				}} );
			
			column.getColumn().setText(markerField);

			TreeColumn columnControl = column.getColumn();

			removeSelectionListener(columnControl, SWT.Selection);
			removeSelectionListener(columnControl, SWT.DefaultSelection);
			
	        column.getColumn().addSelectionListener( getSelectionAdapter(column.getColumn(), markerField) );
	        
//			column.getColumn().setToolTipText(
//					markerField.getColumnTooltipText());
//			column.getColumn().setImage(markerField.getColumnHeaderImage());

//			EditingSupport support = markerField.getEditingSupport(viewer);
//			if (support != null)
//				column.setEditingSupport(support);

//			if (builder.getPrimarySortField().equals(markerField))
//				updateDirectionIndicator(column.getColumn(), markerField);

			int columnWidth = -1;

			// Compute and store a font metric
			GC gc = new GC(tree);
			gc.setFont(tree.getFont());
			FontMetrics fontMetrics = gc.getFontMetrics();
			gc.dispose();
			columnWidth = fontMetrics.getAverageCharWidth() * 20;

//			if (columnWidths != null) {
//				Integer value = columnWidths.getInteger(getFieldId(column
//						.getColumn()));
//
//				// Make sure we get a useful value
//				if (value != null && value.intValue() > 0)
//					columnWidth = value.intValue();
//			}

			// Take trim into account if we are using the default value, but not
			// if it is restored.
			layout.addColumnData(new ColumnPixelData(columnWidth, true, i != 0));
		}

		// Remove extra columns
		if (currentColumns.length > fields.length) {
			for (int i = fields.length; i < currentColumns.length; i++) {
				currentColumns[i].dispose();

			}
		}

		viewer.getTree().setLayout(layout);
		tree.layout(true);
	}

	private void removeSelectionListener(TreeColumn columnControl, int eventType) {
		Listener[] listeners = columnControl.getListeners(eventType);
		for ( Listener listener : listeners) {
			columnControl.removeListener(eventType, listener);
		}
	}

	private SelectionAdapter getSelectionAdapter(final TreeColumn column, final String sortValue) {
		
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewerComparator.setSortValue(sortValue);
				int dir = viewer.getTree().getSortDirection();
				if (viewer.getTree().getSortColumn() == column) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					dir = SWT.DOWN;
				}
				viewer.getTree().setSortDirection(dir);
				viewer.getTree().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

}
