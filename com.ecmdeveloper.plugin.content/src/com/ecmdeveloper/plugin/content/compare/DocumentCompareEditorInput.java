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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class DocumentCompareEditorInput extends CompareEditorInput {

	private Document document;
	private int contentIndex;
	private IFile compareFile;
	
	public DocumentCompareEditorInput(CompareConfiguration configuration,
			Document document, int contentIndex, IFile compareFile ) {
		super(configuration);
		this.document = document;
		this.contentIndex = contentIndex;
		this.compareFile = compareFile;
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		
		Differencer d = new Differencer();
		DocumentCompareItem compareItem1 = new DocumentCompareItem(document, contentIndex );
		WorkspaceFileCompareItem compareItem2 = new WorkspaceFileCompareItem(compareFile);
		
		Object diff = d.findDifferences(false, monitor, null, null, compareItem1, compareItem2 );
		return diff;	
	}
}
