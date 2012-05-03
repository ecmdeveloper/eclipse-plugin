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

package com.ecmdeveloper.plugin.search.wizards;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.InTest;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryFieldType;

/**
 * @author ricardo.belfor
 *
 */
public class InTestWizard extends QueryComponentWizard {

	private static final String TITLE = "In Test Wizard";

	private MultiValueWizardPage multiValueWizardPage;
	private HashMap<QueryFieldType, MultiValueWizardPage> typePages;
	private List<?> value;

	public InTestWizard(Query query) {
		super(query);
		setWindowTitle(TITLE);
		typePages = new HashMap<QueryFieldType, MultiValueWizardPage>();
	}

	@Override
	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {

			@Override
			protected boolean select(IQueryField queryField) {
				return InTest.DESCRIPTION.isValidFor(queryField);
			}
		};
	}
	
	@Override
	public void addPages() {
		super.addPages();
		
		addPage( new WizardPage("dummy") {

			@Override
			public void createControl(Composite parent) {
				setControl( new Label(parent, SWT.NONE) );
			} } 
		);
	}


	@Override
	public IWizardPage getStartingPage() {
		if ( isSkipFieldSelection() ) {
			return getNextPage(selectFieldWizardPage);
		}
		return super.getStartingPage();
	}
	
	
	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		if ( page instanceof SelectFieldWizardPage ) {
			return null;
		} else if ( page instanceof MultiValueWizardPage ) {
			return selectFieldWizardPage;
		}
		return null;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
		if ( page instanceof SelectFieldWizardPage ) {
			IQueryField field = getField();
			multiValueWizardPage = getValueWizardPage(field);
			if ( multiValueWizardPage != null ) {
				multiValueWizardPage.setValue(value);
				return multiValueWizardPage;
			}
		} else if ( page.getName().equals("dummy") ) {
			return selectFieldWizardPage;
		} else if ( page instanceof MultiValueWizardPage ) {
			return null;
		}
		
		return null;
	}

	private MultiValueWizardPage getValueWizardPage(IQueryField field) {
		if ( field != null ) {
			
			if ( typePages.containsKey(field.getType() ) ) {
				return typePages.get(field.getType());
			}
			
			MultiValueWizardPage page = createValueWizardPage(field);
			addPage(page);
			typePages.put(field.getType(), page);
			return page;
		}
		return null;
	}

	private MultiValueWizardPage createValueWizardPage(IQueryField field) {
		MultiValueWizardPage page;
		
		switch (field.getType() ) {
		case STRING:
		case STRING_MV:
			page = new StringMultiValueWizardPage(field);
			break;
		case LONG:
			page = new IntegerMultiValueWizardPage(field);
			break;
		case DOUBLE:
			page = new DoubleMultiValueWizardPage(field);
			break;
		case GUID:
			page = new StringMultiValueWizardPage(field);
			break;
		case BOOLEAN:
			page = new BooleanMultiValueWizardPage(field);
			break;
		case DATE:
		case DATE_MV:
			page = new DateMultiValueWizardPage(field);
			break;
		default:
			throw new IllegalArgumentException();
		}
		return page;
	}
	
	public Object getValue() {
		if ( multiValueWizardPage == null) {
			return null;
		}
		return multiValueWizardPage.getValue();
	}

	public void setValue(List<?> value) {
		this.value = value;
	}

	@Override
	public boolean canFinish() {
		return getField() != null && getValue() != null;
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
