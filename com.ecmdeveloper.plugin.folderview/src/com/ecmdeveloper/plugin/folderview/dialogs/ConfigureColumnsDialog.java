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

package com.ecmdeveloper.plugin.folderview.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author ricardo.belfor
 *
 */
public class ConfigureColumnsDialog  extends Dialog {

	private static final String RIGHT = ">>";
	private static final String LEFT = "<<";
	private Composite editArea;

	private final ArrayList<String> visible = new ArrayList<String>();
	private ArrayList<String> hidden = new ArrayList<String>();;
	private ArrayList<String> groupBy = new ArrayList<String>();;
	
	public ConfigureColumnsDialog(Shell parentShell, List<String> visibleColumns,
			List<String> hiddenColumns, List<String> groupByColumns) {

		super(parentShell);
		
		visible.addAll(visibleColumns);
		hidden.addAll( hiddenColumns );
		groupBy.addAll(groupByColumns);
	}

	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	protected Control createDialogArea(Composite parent) {

		Composite dialogArea = (Composite) super.createDialogArea(parent);

		editArea = new Composite(dialogArea, SWT.NONE);
		editArea.setLayout(new GridLayout());
		GridData editData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		editData.horizontalIndent = 10;
		editArea.setLayoutData(editData);

		createColumnsArea(dialogArea);

		applyDialogFont(dialogArea);
		return dialogArea;
	}

	private void createColumnsArea(Composite dialogArea) {

		initializeDialogUnits(dialogArea);
		Group columnsComposite = new Group(dialogArea, SWT.NONE);
		columnsComposite.setText("Hide/Show Columns");
		FormLayout layout = new FormLayout();
		columnsComposite.setLayout(layout);

		columnsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,true));
		
		Label hiddenLabel = createHiddenLabel(columnsComposite);
		Label visibleLabel = createVisibleLabel(columnsComposite);
		Label groupByLabel = createGroupByLabel(columnsComposite);
		
		int rightMargin = IDialogConstants.BUTTON_MARGIN * -1;
		
		final ListViewer visibleViewer = createColumnsViewer(columnsComposite, visibleLabel, rightMargin);
		visibleViewer.setInput(visible);
		
		final ListViewer hiddenViewer = createColumnsViewer(columnsComposite, hiddenLabel, rightMargin);
		hiddenViewer.setInput(hidden);

		final ListViewer groupByViewer = createColumnsViewer(columnsComposite, groupByLabel, rightMargin);
		groupByViewer.setInput(groupBy);
		
		hiddenViewer.addDoubleClickListener( getDoubleClickListener(hiddenViewer, hidden, visibleViewer, visible) );
		visibleViewer.addDoubleClickListener( getDoubleClickListener(visibleViewer, visible, groupByViewer, groupBy) );
		groupByViewer.addDoubleClickListener( getDoubleClickListener(groupByViewer, groupBy, visibleViewer, visible ) );
		
		Button toHiddenButton = createToHiddenButton(columnsComposite, rightMargin, hiddenViewer, visibleViewer);
		createFromHiddenButton(columnsComposite, rightMargin, hiddenViewer, visibleViewer, toHiddenButton);
		
		Button toGroupByButton = createToGroupByButton(columnsComposite, rightMargin, visibleViewer, groupByViewer );
		createFromGroupByButton(columnsComposite, rightMargin, visibleViewer,groupByViewer, toGroupByButton);
	}

	private IDoubleClickListener getDoubleClickListener(final ListViewer fromViewer, final ArrayList<String> fromList, 
			final ListViewer toViewer, final ArrayList<String> toList) {
		
		return new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				updateColumnViewers(fromViewer, fromList, toViewer, toList );
			}
			
		};
	}

	private ListViewer createColumnsViewer(Group columnsComposite, Label label, int rightMargin) {
		
		final ListViewer columnsViewer = new ListViewer(columnsComposite, SWT.BORDER | SWT.MULTI );

		FormData columnsViewerData = new FormData();

		columnsViewerData.right = new FormAttachment(label, 0, SWT.RIGHT);
		columnsViewerData.left = new FormAttachment(label, 0, SWT.LEFT);
		columnsViewerData.top = new FormAttachment(label, IDialogConstants.BUTTON_MARGIN);
		columnsViewerData.bottom = new FormAttachment(100, rightMargin);
		
		columnsViewerData.height = convertHeightInCharsToPixels(15);
		columnsViewerData.width = convertWidthInCharsToPixels(25);

		columnsViewer.getControl().setLayoutData(columnsViewerData);

		columnsViewer.setLabelProvider(new LabelProvider());
		columnsViewer.setContentProvider( new ArrayContentProvider() );
		
		return columnsViewer;
	}

	private void createFromHiddenButton(Group columnsComposite, int rightMargin, final ListViewer hiddenViewer,
			final ListViewer visibleViewer, Button toNonVisibleButton) {
		
		Button button = new Button(columnsComposite, SWT.PUSH);
		button.setText( RIGHT );

		FormData buttonData = new FormData();

		buttonData.top = new FormAttachment(toNonVisibleButton,IDialogConstants.BUTTON_MARGIN);
		buttonData.left = new FormAttachment(hiddenViewer.getControl(), IDialogConstants.BUTTON_MARGIN);
		buttonData.right = new FormAttachment(visibleViewer.getControl(), rightMargin);
		button.setLayoutData(buttonData);

		button.addSelectionListener( getButtonListener(hiddenViewer, hidden, visibleViewer, visible) );
	}

	private Button createToHiddenButton(Group columnsComposite, int rightMargin,
			final ListViewer hiddenViewer, final ListViewer visibleViewer) {
		
		Button button = new Button(columnsComposite, SWT.PUSH);
		button.setText( LEFT );

		FormData buttonData = new FormData();

		buttonData.top = new FormAttachment(hiddenViewer.getControl(), IDialogConstants.BUTTON_BAR_HEIGHT, SWT.TOP);
		buttonData.left = new FormAttachment(hiddenViewer.getControl(), IDialogConstants.BUTTON_MARGIN);
		buttonData.right = new FormAttachment(visibleViewer.getControl(), rightMargin);
		button.setLayoutData(buttonData);
		button.addSelectionListener( getButtonListener( visibleViewer, visible, hiddenViewer, hidden ) ); 
		
		return button;
	}

	private Button createToGroupByButton(Group columnsComposite, int rightMargin,
			final ListViewer visibleViewer, final ListViewer groupByViewer) {
		
		Button button = new Button(columnsComposite, SWT.PUSH);
		button.setText(RIGHT);

		FormData buttonData = new FormData();

		buttonData.top = new FormAttachment(visibleViewer.getControl(), IDialogConstants.BUTTON_BAR_HEIGHT, SWT.TOP);
		buttonData.left = new FormAttachment(visibleViewer.getControl(), IDialogConstants.BUTTON_MARGIN);
		buttonData.right = new FormAttachment(groupByViewer.getControl(), rightMargin);
		button.setLayoutData(buttonData);
		button.addSelectionListener( getButtonListener(visibleViewer, visible, groupByViewer, groupBy ) );
		
		return button;
	}

	private void createFromGroupByButton(Group columnsComposite, int rightMargin, final ListViewer visibleViewer,
			final ListViewer groupByViewer, Button toGroupByButton) {
		
		Button button = new Button(columnsComposite, SWT.PUSH);
		button.setText( LEFT );

		FormData buttonData = new FormData();

		buttonData.top = new FormAttachment(toGroupByButton,IDialogConstants.BUTTON_MARGIN);
		buttonData.left = new FormAttachment(visibleViewer.getControl(), IDialogConstants.BUTTON_MARGIN);
		buttonData.right = new FormAttachment(groupByViewer.getControl(), rightMargin);
		button.setLayoutData(buttonData);

		button.addSelectionListener( getButtonListener(groupByViewer, groupBy, visibleViewer, visible) );
	}
	
	private SelectionAdapter getButtonListener(final ListViewer fromViewer, final ArrayList<String> fromList, final ListViewer toViewer, final ArrayList<String> toList) {
		
		return new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				updateColumnViewers(fromViewer, fromList, toViewer, toList);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void updateColumnViewers(final ListViewer fromViewer, final ArrayList<String> fromList,
			final ListViewer toViewer, final ArrayList<String> toList) {
		List<String> selection = ((IStructuredSelection) fromViewer.getSelection()).toList();
		toList.addAll(selection);
		fromList.removeAll(selection);
		fromViewer.refresh();
		toViewer.refresh();
	}
	
	private Label createHiddenLabel(Group columnsComposite) {

		Label visibleItemsLabel = new Label(columnsComposite, SWT.NONE);
		visibleItemsLabel.setText("Hidden");
		FormData visibleLabelData = new FormData();
		visibleLabelData.right = new FormAttachment(30, 0);
		visibleLabelData.left = new FormAttachment(IDialogConstants.BUTTON_MARGIN);
		visibleLabelData.top = new FormAttachment(0);
		visibleItemsLabel.setLayoutData(visibleLabelData);
		
		return visibleItemsLabel;
	}

	private Label createVisibleLabel(Group columnsComposite) {
		
		Label label = new Label(columnsComposite, SWT.NONE);
		label.setText("Visible");
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(70);
		labelData.left = new FormAttachment(35, 0);
		labelData.top = new FormAttachment(0);
		label.setLayoutData(labelData);
		
		return label;
	}

	private Label createGroupByLabel(Group columnsComposite) {
		
		Label label = new Label(columnsComposite, SWT.NONE);
		label.setText("Group by");
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(100);
		labelData.left = new FormAttachment(75, 0);
		labelData.top = new FormAttachment(0);
		label.setLayoutData(labelData);
		
		return label;
	}
	
	public List<String> getVisibleColumns() {
		ArrayList<String> visibleColumns = new ArrayList<String>();
		visibleColumns.addAll(visible);
		return visibleColumns;
	}

	public List<String> getHiddenColumns() {
		ArrayList<String> hiddenColumns = new ArrayList<String>();
		hiddenColumns.addAll(hidden);
		return hiddenColumns;
	}

	public List<String> getGroupByColumns() {
		ArrayList<String> groupByColumns = new ArrayList<String>();
		groupByColumns.addAll(groupBy);
		return groupByColumns;
	}
}
