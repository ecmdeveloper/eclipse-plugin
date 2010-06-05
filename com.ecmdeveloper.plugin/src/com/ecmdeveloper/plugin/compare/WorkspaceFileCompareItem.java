/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.compare;

import java.io.InputStream;

import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ricardo.Belfor
 *
 */
public class WorkspaceFileCompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate {

	private IFile file;
	
	public WorkspaceFileCompareItem(IFile file) {
		this.file = file;
	}

	@Override
	public InputStream getContents() throws CoreException {
		return file.getContents();
	}

	@Override
	public Image getImage() {
		String imageKey = ISharedImages.IMG_OBJ_FILE;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public String getType() {
		return ITypedElement.TEXT_TYPE;
	}

	@Override
	public long getModificationDate() {
		return 0;
	}
}
