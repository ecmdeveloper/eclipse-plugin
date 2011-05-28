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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public abstract class QueryComponentWizard extends Wizard {

	protected SelectFieldWizardPage selectFieldWizardPage;
	protected IQueryField selection;
	private boolean skipFieldSelection = false;
	private final Query query;
	
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

		selectFieldWizardPage = new SelectFieldWizardPage(query, getFieldSelection());
		selectFieldWizardPage.setFilter(getQueryFieldFilter() );
		selectFieldWizardPage.setField(selection);
		addPage(selectFieldWizardPage);
	}

	protected StructuredSelection getFieldSelection() {
		StructuredSelection fieldSelection = null;
		if ( selection != null ) {
			fieldSelection = new StructuredSelection( selection );
		}
		return fieldSelection;
	}	
	
	public IQueryField getField() {
		return selectFieldWizardPage.getField();
	}

	public void setSelection(IQueryField field) {
		this.selection = field;
	}
	
	protected IQueryField getSelection() {
		return selection;
	}

	protected Query getQuery() {
		return query;
	}

	public void setSkipFieldSelection(boolean skipFieldSelection ) {
		this.skipFieldSelection = skipFieldSelection;
	}

	protected boolean isSkipFieldSelection() {
		return skipFieldSelection;
	}
}
