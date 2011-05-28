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

import java.text.MessageFormat;

import org.eclipse.jface.wizard.IWizardPage;

import com.ecmdeveloper.plugin.search.model.FullTextQuery;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.constants.FullTextQueryType;

/**
 * @author ricardo.belfor
 *
 */
public class FullTextQueryWizard extends QueryComponentWizard {

	private static final String EXPRESSION_TITLE = "{0} expression";
	private static final String EXPRESSION_DESCRIPTION = "Enter the {0} expression.";
	private FullTextQueryTypeWizardPage fullTextQueryTypeWizardPage;
	private StringValueWizardPage stringValueWizardPage;

	private FullTextQueryType fullTextQueryType;
	private String text;
	private boolean allFields;

	public FullTextQueryWizard(Query query) {
		super(query);
	}

	@Override
	public void addPages() {
		fullTextQueryTypeWizardPage = new FullTextQueryTypeWizardPage();
		fullTextQueryTypeWizardPage.setFullTextQueryType(fullTextQueryType);
		fullTextQueryTypeWizardPage.setCbrEnabled( getQuery().isCRBEnabled() );
		addPage( fullTextQueryTypeWizardPage );
		
		selectFieldWizardPage = new SelectFullTextQueryFieldWizardPage(getQuery(),
				getFieldSelection());
		selectFieldWizardPage.setFilter(getQueryFieldFilter() );
		addPage(selectFieldWizardPage);
		
		stringValueWizardPage = new StringValueWizardPage();
		if ( text != null ) {
			stringValueWizardPage.setValue(text);
		}
		addPage(stringValueWizardPage);

	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof FullTextQueryTypeWizardPage ) {
			FullTextQueryType fullTextQueryType2 = getFullTextQueryType();
			if ( FullTextQueryType.FREETEXT.equals(fullTextQueryType2) ) {
				configureStringValuePage(fullTextQueryType2);
				return stringValueWizardPage;
			} else if ( FullTextQueryType.CONTAINS.equals(fullTextQueryType2 ) ) {
				configureStringValuePage(fullTextQueryType2);
				return selectFieldWizardPage;
			} else {
				return null;
			}
		}
		return super.getNextPage(page);
	}

	private void configureStringValuePage(FullTextQueryType fullTextQueryType) {
		stringValueWizardPage.setTitle( MessageFormat.format(EXPRESSION_TITLE, fullTextQueryType.name()));
		stringValueWizardPage.setDescription(MessageFormat.format(EXPRESSION_DESCRIPTION, fullTextQueryType.name()));
	}

	@Override
	public boolean canFinish() {
		FullTextQueryType type = getFullTextQueryType();
		if ( type != null ) {
			switch (type) {
			case CONTAINS: 
				return ( isAllFields() || getField() != null ) && getText() != null && !getText().isEmpty();
			case FREETEXT:
				return getText() != null && !getText().isEmpty();
			}
		}
		return false;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public void setFullTextQueryType(FullTextQueryType fullTextQueryType) {
		this.fullTextQueryType = fullTextQueryType;
	}
	
	public FullTextQueryType getFullTextQueryType() {
		if ( fullTextQueryTypeWizardPage != null ) {
			return fullTextQueryTypeWizardPage.getFullTextQueryType();
		}
		return null;
	}

	public String getText() {
		return (String) stringValueWizardPage.getValue();
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isAllFields() {
		if ( selectFieldWizardPage != null ) {
			return ((SelectFullTextQueryFieldWizardPage) selectFieldWizardPage).isAllFields();
		}
		return allFields;
	}

	public void setAllFields(boolean allFields) {
		this.allFields = allFields;
	}

	@Override
	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {

			@Override
			protected boolean select(IQueryField queryField) {
				return FullTextQuery.DESCRIPTION.isValidFor(queryField);
			}
		};
	}
}