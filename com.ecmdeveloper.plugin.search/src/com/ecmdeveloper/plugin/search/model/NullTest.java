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

import org.eclipse.jface.resource.ImageDescriptor;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.editor.QueryIcons;

/**
 * @author ricardo.belfor
 *
 */
public class NullTest extends QueryComponent {

	private static final long serialVersionUID = 1L;
	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			NullTest.class, "Null Test", "Query Field Null Test", QueryIcons.NULL_TEST_ICON,
			QueryIcons.NULL_TEST_ICON_LARGE){

				@Override
				public boolean isValidFor(IQueryField queryField) {
					return true;
				}};	

	private boolean negated;
	
	public NullTest(Query query) {
		super(query);
	}
	
	public static ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(QueryIcons.NULL_TEST_ICON);
	}

	public static ImageDescriptor getLargeIcon() {
		return Activator.getImageDescriptor(QueryIcons.NULL_TEST_ICON_LARGE);
	}

	public void setNegated(boolean negated) {
		if ( this.negated != negated ) {
			this.negated = negated;
			firePropertyChange(PROPERTY_CHANGED, null, negated);
		}
	}

	public boolean isNegated() {
		return negated;
	}

	public String toString() {
		return toString(false);
	}

	@Override
	public String toSQL() {
		return toString(true);
	}

	private String toString(boolean strict) {
		if ( getField() != null ) {
			StringBuffer result = new StringBuffer();
			appendField(result, strict);
			result.append( " IS ");
			if ( negated ) {
				result.append("NOT ");
			}
			result.append( "NULL");
			return result.toString();
		} else {
			return "";
		}
	}
}
