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

import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.jface.wizard.Wizard;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.IQueryTable;
import com.ecmdeveloper.plugin.search.model.QueryFieldType;
import com.ecmdeveloper.plugin.search.model.SortType;

/**
 * @author ricardo.belfor
 *
 */
public class FreeTextWizard extends Wizard {

	private StringValueWizardPage stringValueWizardPage;
	private String text;

	@Override
	public void addPages() {
		
		MultiValueWizardPage p = new DateMultiValueWizardPage(new IQueryField() {

			@Override
			public String getAlias() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getDisplayName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public IQueryTable getQueryTable() {
				return new IQueryTable() {

					@Override
					public void addChildQueryTable(IQueryTable childTable) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void addPropertyChangeListener(PropertyChangeListener listener) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void addQueryField(IQueryField queryField) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public String getAlias() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Collection<IQueryTable> getChildQueryTables() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getConnectionDisplayName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getConnectionName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getDisplayName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getObjectStoreDisplayName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getObjectStoreName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public IQueryField getQueryField(String fieldName) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Collection<IQueryField> getQueryFields() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Collection<IQueryField> getSelectedQueryFields() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean isCBREnabled() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean isContentEngineTable() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void mergeQueryFields(Collection<IQueryField> queryFields) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void notifyQueryFieldChanged(String propertyName, Object oldValue,
							Object newValue) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void removePropertyChangeListener(PropertyChangeListener listener) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void setAlias(String alias) {
						// TODO Auto-generated method stub
						
					}};
			}

			@Override
			public int getSortOrder() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public SortType getSortType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public QueryFieldType getType() {
				// TODO Auto-generated method stub
				return QueryFieldType.DATE;
			}

			@Override
			public boolean isCBREnabled() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isContainable() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isOrderable() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isSearchable() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isSelectable() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isSelected() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isSupportsWildcards() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setAlias(String alias) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setSelected(boolean selected) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setSortOrder(int sortOrder) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setSortType(SortType sortType) {
				// TODO Auto-generated method stub
				
			}});
		addPage(p);
		
		stringValueWizardPage = new StringValueWizardPage();
		if ( text != null ) {
			stringValueWizardPage.setValue(text);
		}
		addPage(stringValueWizardPage);
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}

	public String getText() {
		return (String) stringValueWizardPage.getValue();
	}

	public void setText(String text) {
		this.text = text;
	}
}
