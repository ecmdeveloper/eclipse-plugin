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

import org.eclipse.jface.wizard.IWizardPage;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class NullTestWizard extends QueryComponentWizard {

	private NullTestWizardPage nullTestWizardPage;
	private final boolean negated;

	public NullTestWizard(Query query, boolean negated) {
		super(query);
		this.negated = negated;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		
		nullTestWizardPage = new NullTestWizardPage();
		nullTestWizardPage.setNegated(negated);
		addPage( nullTestWizardPage );
	}

	@Override
	public IWizardPage getStartingPage() {
		if ( isSkipFieldSelection() ) {
			return nullTestWizardPage;
		}
		return super.getStartingPage();
	}
	
	@Override
	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {
	
			@Override
			protected boolean select(IQueryField queryField) {
				return NullTest.DESCRIPTION.isValidFor(queryField);
			}
			
		};
	}

	@Override
	public boolean canFinish() {
		return getField() != null;
	}

	@Override
	public boolean performFinish() {
		return true;
	}
	
	public boolean isNegated() {
		return nullTestWizardPage.isNegated();
	}
}
