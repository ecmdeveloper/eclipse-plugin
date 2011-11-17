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

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.MultiValueInTest;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryFieldType;

/**
 * @author ricardo.belfor
 * 
 */
public class MultiValueInTestWizard extends QueryComponentWizard {

	private static final String TITLE = "Multi Value In Test Wizard";

	private ValueWizardPage valueWizardPage;
	private HashMap<QueryFieldType, ValueWizardPage> typePages;
	private Object value;
	
	public MultiValueInTestWizard(Query query) {
		super(query);
		setWindowTitle(TITLE);
		typePages = new HashMap<QueryFieldType, ValueWizardPage>();
	}

	@Override
	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {

			@Override
			protected boolean select(IQueryField queryField) {
				return MultiValueInTest.DESCRIPTION.isValidFor(queryField);
			}
		};
	}

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
		} else if ( page instanceof ValueWizardPage ) {
			return selectFieldWizardPage;
		}
		return null;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
		if ( page instanceof SelectFieldWizardPage ) {
			IQueryField field = getField();
			valueWizardPage = getValueWizardPage(field);
			if ( valueWizardPage != null ) {
				valueWizardPage.setValue(value);
				return valueWizardPage;
			}
		} else if ( page instanceof ValueWizardPage ) {
			return null;
		}
		
		return null;
	}

	private ValueWizardPage getValueWizardPage(IQueryField field) {
		if ( field != null ) {
			
			if ( typePages.containsKey(field.getType() ) ) {
				return typePages.get(field.getType());
			}
			
			ValueWizardPage page = createValueWizardPage(field);
			addPage(page);
			typePages.put(field.getType(), page);
			return page;
		}
		return null;
	}

	private ValueWizardPage createValueWizardPage(IQueryField field) {
		ValueWizardPage page;
		
		switch (field.getType() ) {
		case STRING:
		case STRING_MV:
			page = new StringValueWizardPage();
			break;
		case LONG:
			page = new IntegerValueWizardPage();
			break;
		case DOUBLE:
			page = new DoubleValueWizardPage();
			break;
		case GUID:
			page = new IdValueWizardPage();
			break;
		case BOOLEAN:
			page = new BooleanValueWizardPage();
			break;
		case DATE:
		case DATE_MV:
			page = new DateValueWizardPage();
			break;
		case OBJECT:
			page = new ObjectValueWizardPage();
			break;
		default:
			throw new IllegalArgumentException();
		}
		return page;
	}

	@Override
	public boolean canFinish() {
		return getField() != null && getValue() != null;
	}

	public Object getValue() {
		if ( valueWizardPage == null) {
			return null;
		}
		return valueWizardPage.getValue();
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
