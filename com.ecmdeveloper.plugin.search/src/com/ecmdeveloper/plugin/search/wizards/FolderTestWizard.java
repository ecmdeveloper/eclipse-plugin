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

import java.util.ArrayList;

import com.ecmdeveloper.plugin.search.model.IQueryField;

/**
 * @author ricardo.belfor
 *
 */
public class FolderTestWizard extends QueryComponentWizard {

	private String folder = "/Animals/Fish";
	
	public FolderTestWizard(ArrayList<IQueryField> fields) {
		super(fields);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFolder() {
		return folder;
	}
}
