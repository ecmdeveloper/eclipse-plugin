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
package com.ecmdeveloper.plugin.editors.core;

import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.properties.Activator;
import com.ecmdeveloper.plugin.properties.editors.details.PropertiesInputDetailsPageProvider;
import com.ecmdeveloper.plugin.properties.util.IconFiles;

public class PropertiesInputBlock extends MasterDetailsBlock {

	private FormPage page;
	private TableViewer viewer;
	
	public PropertiesInputBlock(FormPage page) {
		this.page = page;
	}

	public void setInput(Object input) {
		viewer.setInput(input);
	}
	
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {

		FormToolkit toolkit = managedForm.getToolkit();
		Section section = createSection(parent, toolkit);
		Composite client = createClient(toolkit, section);
		final SectionPart spart = new SectionPart(section);
		Table table = createTable(toolkit, client);
		managedForm.addPart(spart);
		createTableViewer(managedForm, spart, table);
		section.setClient(client);
	}

	private Table createTable(FormToolkit toolkit, Composite client) {
		
		Table table = toolkit.createTable(client, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		gd.widthHint = 100;
		table.setLayoutData(gd);

		return table;
	}

	private void createTableViewer(final IManagedForm managedForm, final SectionPart sectionPart,
			Table table) {
	
		viewer = new TableViewer(table);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(sectionPart, event.getSelection());
			}
		});

		viewer.setLabelProvider( new PropertyLabelProvider() );
		viewer.setContentProvider( new PropertyContentProvider() );
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
		section.setText("Properties");
		section.setDescription("Select a property from the list");
		section.marginWidth = 5;
		section.marginHeight = 5;
		return section;
	}
	
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		Action commitAction = new Action("hor", Action.AS_PUSH_BUTTON) { 
			public void run() {
			}
		};
		commitAction.setToolTipText("Commit changes");
		commitAction.setImageDescriptor( Activator.getImageDescriptor( IconFiles.READ_ONLY ) );
		form.getToolBarManager().add(commitAction);
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {

		detailsPart.setPageProvider( new PropertiesInputDetailsPageProvider() );
//		detailsPart.registerPage(String.class, new StringDetailsPage() );
//		detailsPart.registerPage( Date.class, new DateDetailsPage() );
	}
}
