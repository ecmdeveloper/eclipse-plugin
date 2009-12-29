/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.editors.core;

import java.awt.Color;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Ricardo.Belfor
 *
 */
public class StringDetailsPage implements IDetailsPage{

	private IManagedForm form;
	private Button flag1;
	private Button flag2;

	@Override
	public void createContents(Composite parent) {
//		TableWrapLayout layout1 = new TableWrapLayout();
//		layout.topMargin = 5;
//		layout.leftMargin = 5;
//		layout.rightMargin = 2;
//		layout.bottomMargin = 2;
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 1;
		parent.setLayout(layout1);

		
		FormToolkit toolkit = form.getToolkit();
//		Section s1 = toolkit.createSection(parent, Section.DESCRIPTION|Section.TITLE_BAR);
//		s1.setText("Name");
//		s1.setDescription("Description");
//
//		
//		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
//		td.grabHorizontal = true;
//		td.grabVertical = true;
//		s1.setLayoutData(td);
//		Composite client = toolkit.createComposite(s1);
//		GridLayout glayout = new GridLayout();
//		glayout.marginWidth = glayout.marginHeight = 0;
//		
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		client.setLayoutData(gd);
//
//		client.setLayout(glayout);

		Section section = toolkit.createSection(parent,
				Section.DESCRIPTION | Section.TITLE_BAR );

		section.setText("Name");
		section.setDescription("Description");

		GridData gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);

		Composite client = toolkit.createComposite( section, SWT.WRAP );
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		client.setLayout(layout);
		
		toolkit.createButton(client, "Empty Value", SWT.CHECK );
		
		Text text = toolkit.createText(client, "", SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP ); 
		
		text.setLayoutData( new GridData(GridData.FILL_BOTH) );
		text.setText( "Hello, String" );

		section.setClient(client);
	}

	@Override
	public void commit(boolean onSave) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean setFormInput(Object input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
