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
import org.eclipse.core.runtime.IProgressMonitor;

import com.ecmdeveloper.plugin.core.model.IDocument;

/**
 * @author Ricardo.Belfor
 *
 */
public class DocumentVersionCompareEditorInput extends CompareEditorInput {

	private IDocument document1;
	private IDocument document2;
	private int contentIndex1;
	private int contentIndex2;
	private boolean showVersionLabels;
	
	public DocumentVersionCompareEditorInput(CompareConfiguration configuration,
			IDocument document1, int contentIndex1, IDocument document2, int contentIndex2, boolean showVersionLabels) {
		super(configuration);
		this.document1 = document1;
		this.document2 = document2;
		this.contentIndex1 = contentIndex1;
		this.contentIndex2 = contentIndex2;
		this.showVersionLabels = showVersionLabels;
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		
		Differencer d = new Differencer();
		DocumentCompareItem compareItem1;
		DocumentCompareItem compareItem2;
		
		if ( showVersionLabels ) {
			compareItem1 = new DocumentVersionCompareItem(document1, contentIndex1 );
			compareItem2 = new DocumentVersionCompareItem(document2, contentIndex2 );
		} else {
			
			compareItem1 = new DocumentCompareItem(document1, contentIndex1 );
			compareItem2 = new DocumentCompareItem(document2, contentIndex2 );
		}
		
		Object diff = d.findDifferences(false, monitor, null, null, compareItem1, compareItem2 );
		return diff;	
	}
}
