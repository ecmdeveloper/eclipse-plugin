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

package com.ecmdeveloper.plugin.search.editor;

import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.util.IconFiles;

/**
 * @author ricardo.belfor
 *
 */
public class QueryEditorInput implements IEditorInput {

	private static final String DEFAULT_SEARCH_NAME = "Search {0}";

	private static int newSearchIndex = 0;

	private String name;

	public QueryEditorInput() {
		name = MessageFormat.format(DEFAULT_SEARCH_NAME, ++newSearchIndex);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getToolTipText() {
		return getName();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getImageDescriptor( IconFiles.SEARCH_EDITOR );
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}
}
