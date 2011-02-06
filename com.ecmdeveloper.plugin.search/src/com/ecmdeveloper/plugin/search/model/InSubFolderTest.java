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

import org.eclipse.swt.graphics.Image;

/**
 * @author ricardo.belfor
 *
 */
public class InSubFolderTest extends QueryComponent {

	private static final long serialVersionUID = 1L;

	private static Image INSUBFOLDER_TEST_ICON = createImage(InSubFolderTest.class, "icons/arrow16.gif"); //$NON-NLS-1$
	private String folder;
	
	public InSubFolderTest(Query query) {
		super(query);
	}
	
	@Override
	public Image getIconImage() {
		return INSUBFOLDER_TEST_ICON;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	@Override
	public String toString() {
		if ( getField() != null && folder != null ) { 
			StringBuffer result = new StringBuffer();
			result.append(getField());
			result.append( " INSUBFOLDER('");
			result.append(folder);
			result.append( "')");
			return result.toString();
		} else {
			return "";
		}
	}
}
