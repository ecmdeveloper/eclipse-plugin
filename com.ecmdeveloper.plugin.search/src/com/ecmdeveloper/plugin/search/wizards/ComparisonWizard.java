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

import java.util.UUID;

import org.eclipse.jface.wizard.IWizardPage;

import com.ecmdeveloper.plugin.search.model.ComparisonOperation;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryFieldType;

/**
 * @author ricardo.belfor
 * 
 */
public class ComparisonWizard extends QueryComponentWizard {

	private static final String TITLE = "Query Condition Wizard";

	private ComparisonOperationWizardPage comparisonOperationWizardPage;
	private ValueWizardPage valueWizardPage;
	private ComparisonOperation comparisonOperation = ComparisonOperation.EQUAL;
	private Object value;
	
	public ComparisonWizard(Query query) {
		super(query);
		setWindowTitle(TITLE);
	}

	@Override
	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {

			@Override
			protected boolean select(IQueryField queryField) {
				return queryField.isQueryField();
			}
		};
	}

	public void addPages() {

		super.addPages();

		comparisonOperationWizardPage = new ComparisonOperationWizardPage();
		comparisonOperationWizardPage.setComparisonOperation(comparisonOperation);
		addPage(comparisonOperationWizardPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
		if ( page instanceof ComparisonOperationWizardPage ) {
			IQueryField field = getField();
			valueWizardPage = getValueWizardPage(field);
			if ( valueWizardPage != null ) {
				addPage(valueWizardPage);
				valueWizardPage.setValue(value);
				
				if ( field.getType().equals( QueryFieldType.GUID ) )
				{
					valueWizardPage.setValue(UUID.randomUUID().toString() );
				}
				return valueWizardPage;
			}
		}
		if ( page instanceof QueryFieldWizardPage ) {
			comparisonOperationWizardPage.setField(getQueryFieldWizardPage().getField() );
		}
		
		return super.getNextPage(page);
	}

	private ValueWizardPage getValueWizardPage(IQueryField field) {
		if ( field != null ) {
			switch (field.getType() ) {
			case STRING:
				return new StringValueWizardPage();
			case LONG:
				return new IntegerValueWizardPage();
			case DOUBLE:
				return new DoubleValueWizardPage();
			case GUID:
				return new IdValueWizardPage();
			case BOOLEAN:
				return new BooleanValueWizardPage();
			case DATE:
				return new DateValueWizardPage();
			case OBJECT:
				return new ObjectValueWizardPage();
			}
		}
		return null;
	}

	@Override
	public boolean canFinish() {
		return getField() != null && getComparisonOperation() != null && getValue() != null;
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

	public ComparisonOperation getComparisonOperation() {
		return comparisonOperationWizardPage.getComparisonOperation();
	}

	public void setComparisonOperation(ComparisonOperation comparisonOperation) {
		this.comparisonOperation = comparisonOperation;
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
