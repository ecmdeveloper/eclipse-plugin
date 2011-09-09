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

package com.ecmdeveloper.plugin.content.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.content.Activator;
import com.ecmdeveloper.plugin.content.util.PluginLog;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.tasks.GetContentTask;

/**
 * @author Ricardo.Belfor
 *
 */
public class DocumentCompareItem implements IStreamContentAccessor, ITypedElement, IModificationDate {

	protected Document document;
	protected int contentIndex;
	
	public DocumentCompareItem(Document document, int contentIndex ) {
		this.document = document;
		this.contentIndex = contentIndex;
	}

	@Override
	public InputStream getContents() throws CoreException {

		if ( contentIndex >= 0 ) {
			GetContentTask task = new GetContentTask(document,contentIndex);
			try {
				Activator.getDefault().getTaskManager().executeTaskSync(task);
				return task.getContentStream();
			} catch (ExecutionException e) {
				throw new CoreException( PluginLog.createStatus(Status.ERROR, Status.ERROR, e.getLocalizedMessage(), e) );
			}
		} else {
			return new ByteArrayInputStream( "".getBytes() );
		}
	}

	@Override
	public Image getImage() {
		String imageKey = ISharedImages.IMG_OBJ_FILE;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

	@Override
	public String getName() {
		return document.getName();
	}

	@Override
	public String getType() {
		return ITypedElement.TEXT_TYPE;
	}

	@Override
	public long getModificationDate() {
		return 1;
	}
}
