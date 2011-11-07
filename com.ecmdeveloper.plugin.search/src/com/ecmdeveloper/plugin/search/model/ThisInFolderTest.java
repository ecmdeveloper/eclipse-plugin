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

import com.ecmdeveloper.plugin.search.editor.QueryIcons;
import com.ecmdeveloper.plugin.search.model.constants.QueryComponentType;

/**
 * @author ricardo.belfor
 *
 */
public class ThisInFolderTest extends QueryComponent {

	private static final long serialVersionUID = 1L;

	private String folder;

	public static final QueryElementDescription DESCRIPTION = new QueryElementDescription(
			ThisInFolderTest.class, "In Folder Test 2", "Query Field In Folder Test",
			QueryIcons.INFOLDER_TEST_ICON, QueryIcons.INFOLDER_TEST_ICON_LARGE){

				@Override
				public boolean isValidFor(IQueryField queryField) {
					// FIXME check on This?
					return false;
				}};	

	public ThisInFolderTest(Query query) {
		super(query);
	}

	@Override
	public QueryComponentType getType() {
		return QueryComponentType.THIS_IN_FOLDER_TEST;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		String oldValue = this.folder;
		this.folder = folder;
		firePropertyChange(FIELD_CHANGED, oldValue, folder);
	}
	
	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public String toSQL() {
		return toString(true);
	}

	private String toString(boolean strict) {
		if ( folder != null ) { 
			StringBuffer result = new StringBuffer();
			result.append( " IN_FOLDER('");
			result.append(folder);
			result.append( "')");
			return result.toString();
		} else {
			return "";
		}
	}
}
