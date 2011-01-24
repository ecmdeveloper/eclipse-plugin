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

package com.ecmdeveloper.plugin.search.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryCondition;
import com.ecmdeveloper.plugin.search.model.QueryOperation;
import com.ecmdeveloper.plugin.search.model.QueryOperationType;
import com.ecmdeveloper.plugin.search.parts.QueryEditPartFactory;

/**
 * @author ricardo.belfor
 *
 */
public class GraphicalQueryEditor extends GraphicalEditorWithPalette {

	public GraphicalQueryEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	public void createPartControl(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		super.createPartControl(sashForm);
		createLabel(sashForm, "Hello, Editor 2!");
	}	

	private void createLabel(Composite container, String text) {
		final Label label = new Label(container, SWT.BORDER);
		final GridData gridData = new GridData(GridData.BEGINNING);
		label.setLayoutData(gridData);
		label.setText(text);
	}
	
	@Override
	protected PaletteRoot getPaletteRoot() {
		
		PaletteRoot root = new PaletteRoot();
		
		PaletteGroup instGroup = new PaletteGroup("Creation d'elemnts"); 
		root.add(instGroup);		
		
		instGroup.add(new CreationToolEntry("Condition", "Creation of a condition",
				new QueryComponentCreationFactory(QueryCondition.class), null, null));
		instGroup.add(new CreationToolEntry("Operation", "Creation of an operation",
				new QueryComponentCreationFactory(QueryOperation.class), null, null));
		return root;
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = configureViewer();
//		configureContextMenu(viewer);
	}

	private GraphicalViewer configureViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
	    viewer.setEditPartFactory(new QueryEditPartFactory() );
	    viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		return viewer;
	}

	@Override
	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();
		viewer.setContents(createMockModel());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	private Query createMockModel() {
		Query query = new Query();
        
		QueryOperation queryOperation = new QueryOperation(QueryOperationType.AND);
		query.setQueryOperation( queryOperation );
		queryOperation.add(new QueryCondition() ); 
		queryOperation.add(new QueryCondition() );

		QueryOperation orOperation = new QueryOperation(QueryOperationType.OR);
		orOperation.add(new QueryCondition() );
		orOperation.add(new QueryCondition() );
		queryOperation.add(orOperation );
		
		QueryOperation andOperation = new QueryOperation(QueryOperationType.AND);
		andOperation.add(new QueryCondition() );
		andOperation.add(new QueryCondition() );
		queryOperation.add(andOperation );

		return query;
	}
}
