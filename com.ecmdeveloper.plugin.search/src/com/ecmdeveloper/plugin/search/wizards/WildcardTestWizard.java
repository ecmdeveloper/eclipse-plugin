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

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.constants.WildcardType;

/**
 * @author ricardo.belfor
 *
 */
public class WildcardTestWizard extends QueryComponentWizard {

	private WildcardTypeWizardPage wildcardTypeWizardPage;
	private StringValueWizardPage stringValueWizardPage;
	private WildcardType wildcardType = WildcardType.CONTAINS;
	private String value;
	
	public WildcardTestWizard(Query query) {
		super(query);
	}

	@Override
	protected QueryFieldFilter getQueryFieldFilter() {
		return new QueryFieldFilter() {

			@Override
			protected boolean select(IQueryField queryField) {
				return queryField.isSupportsWildcards();
			}
			
		};
	}

	public void addPages() {

		super.addPages();

		wildcardTypeWizardPage = new WildcardTypeWizardPage();
		wildcardTypeWizardPage.setWildcardType(wildcardType);
		addPage(wildcardTypeWizardPage);
		
		stringValueWizardPage = new StringValueWizardPage();
		stringValueWizardPage.setValue(value);
		addPage(stringValueWizardPage);
	}
	
	public WildcardType getWildcardType() {
		return wildcardTypeWizardPage.getWildcardType();
	}

	public void setWildcardType(WildcardType wildcardType) {
		this.wildcardType = wildcardType;
	}

	@Override
	public boolean canFinish() {
		String value = getValue();
		return getField() != null && getWildcardType() != null && value != null && !value.isEmpty();
	}

	public String getValue() {
		return (String) stringValueWizardPage.getValue();
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
