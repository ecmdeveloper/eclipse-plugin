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

import java.util.Collection;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public abstract class QueryComponentWizard extends Wizard {

//	protected QueryFieldWizardPage queryFieldWizardPage;
//	private SelectTableWizardPage selectTableWizardPage;
	private SelectFieldWizardPage selectFieldWizardPage;
	private final Query query;
	private IQueryField selection;
	
	public QueryComponentWizard(Query query) {
		this.query = query;
	}	

	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {
			@Override
			protected boolean select(IQueryField queryField) {
				return true;
			}
		};
	}
	
	public void addPages() {

//		queryFieldWizardPage = new QueryFieldWizardPage( getFieldSelection() );
//
//		if ( query.getQueryTables().size() > 1 ) {
//			StructuredSelection tableSelection = getTableSelection();
//			selectTableWizardPage = new SelectTableWizardPage( query, tableSelection );
//			addPage( selectTableWizardPage );
//		} else {
//			IQueryTable queryTable = query.getQueryTables().iterator().next();
//			Collection<IQueryField> fields = queryTable.getQueryFields();		
//			queryFieldWizardPage.setContent(fields);
//			queryFieldWizardPage.setFilter( getQueryFieldFilter() );
//		}
//
//		addPage(queryFieldWizardPage);

		selectFieldWizardPage = new SelectFieldWizardPage(query, getFieldSelection());
		selectFieldWizardPage.setFilter(getQueryFieldFilter() );
		addPage(selectFieldWizardPage);
	}

//	public QueryFieldWizardPage getQueryFieldWizardPage() {
//		return queryFieldWizardPage;
//	}

//	public SelectTableWizardPage getSelectTableWizardPage() {
//		return selectTableWizardPage;
//	}

	private StructuredSelection getFieldSelection() {
		StructuredSelection fieldSelection = null;
		if ( selection != null ) {
			fieldSelection = new StructuredSelection( selection );
		}
		return fieldSelection;
	}	
	
	private StructuredSelection getTableSelection() {
		StructuredSelection tableSelection = null;
		if ( selection != null ) {
			tableSelection = new StructuredSelection( selection.getQueryTable() );
		}
		return tableSelection;
	}	

//	@Override
//	public IWizardPage getNextPage(IWizardPage page) {
//		if ( page instanceof SelectTableWizardPage ) {
//			IQueryTable queryTable = selectTableWizardPage.getQueryTable();
//			if ( queryTable != null ) {
//				Collection<IQueryField> fields = queryTable.getQueryFields();		
//				queryFieldWizardPage.setContent(fields);
//			} else {
//				return null;
//			}
//		}
//		return super.getNextPage(page);
//	}

	public IQueryField getField() {
//		return queryFieldWizardPage.getField();
		return selectFieldWizardPage.getField();
	}

	public void setSelection(IQueryField field) {
		this.selection = field;
	}
}
