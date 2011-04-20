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

package com.ecmdeveloper.plugin.search.model;

import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageDescriptor;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.QueryIcons;
import com.ecmdeveloper.plugin.search.model.constants.WildcardType;

/**
 * @author ricardo.belfor
 *
 */
public class WildcardTest extends QueryComponent {

	private static final long serialVersionUID = 1L;
	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			WildcardTest.class, "Like Test", "Query Field Wildcard Test",
			QueryIcons.WILDCARD_TEST_ICON, QueryIcons.WILDCARD_TEST_ICON_LARGE) {

				@Override
				public boolean isValidFor(IQueryField queryField) {
					return queryField.isSupportsWildcards();
				}};	

	private WildcardType wildcardType; 
	private String value;
	
	public WildcardTest(Query query) {
		super(query);
	}

	public static ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(QueryIcons.WILDCARD_TEST_ICON);
	}

	public static ImageDescriptor getLargeIcon() {
		return Activator.getImageDescriptor(QueryIcons.WILDCARD_TEST_ICON_LARGE);
	}

	public WildcardType getWildcardType() {
		return wildcardType;
	}

	public void setWildcardType(WildcardType wildcardType) {
		WildcardType oldField = this.wildcardType;
		this.wildcardType = wildcardType;
		firePropertyChange(PROPERTY_CHANGED, oldField, wildcardType);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		String oldField = this.value;
		this.value = value;
		firePropertyChange(PROPERTY_CHANGED, oldField, value);
	}

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toSQL() {
		return toString(true);
	}

	public String toString(boolean strict) {
		if ( getField() != null && value != null && wildcardType != null) {
			StringBuffer result = new StringBuffer();
			appendField(result, strict);
			result.append(" LIKE ");
			result.append(getFormatedValue());
			return result.toString();
		} else {
			return "";
		}
	}

	private String getFormatedValue() {
		
		if ( value == null || wildcardType == null ) {
			throw new UnsupportedOperationException();
		}
		
		switch (wildcardType) {
		case CONTAINS:
			return MessageFormat.format("%{0}%", value);
		case ENDS_WITH:
			return MessageFormat.format("%{0}", value);
		case STARTS_WITH:
			return MessageFormat.format("{0}%", value);
		}
		return value;
	}
}
