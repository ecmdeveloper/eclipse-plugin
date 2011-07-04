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
import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;

/**
 * @author ricardo.belfor
 *
 */
public class FreeText extends QueryComponent {

	private static final long serialVersionUID = 1L;
	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			FreeText.class, "Free Text", "Free Text condition", QueryIcons.FREE_TEXT_ICON,
			QueryIcons.FREE_TEXT_ICON_LARGE){

				@Override
				public boolean isValidFor(IQueryField queryField) {
					return queryField.isSearchable();
				}};	
	
	private String text;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		String oldField = this.text;
		this.text = text;
		firePropertyChange(FIELD_CHANGED, oldField, text);
	}

	public FreeText(Query query) {
		super(query);
	}
	
	public static ImageDescriptor getIcon() {
		return Activator.getImageDescriptor(QueryIcons.FREE_TEXT_ICON);
	}

	public static ImageDescriptor getLargeIcon() {
		return Activator.getImageDescriptor(QueryIcons.FREE_TEXT_ICON_LARGE);
	}

	@Override
	public String toString() {
		if ( text != null ) {
			return text;
		}
		return "";
	}

	@Override
	public String toSQL() {
		return toString();
	}

	@Override
	public QueryComponentType getType() {
		return QueryComponentType.FREE_TEXT;
	}
}
