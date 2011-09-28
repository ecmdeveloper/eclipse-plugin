/**
 * Copyright 2009,2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.io.InputStream;

import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetContentTask;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetContentTask extends AbstractTask implements IGetContentTask {

	private Document document;
	private int index;
	private InputStream contentStream;
	
	public GetContentTask(Document document, int index ) {
		super();
		this.document = document;
		this.index = index;
	}

	public GetContentTask(Document document ) {
		this(document, 0 );
	}
	
	@Override
	public InputStream getContentStream() {
		return contentStream;
	}

	@Override
	public Object call() throws Exception {
		
		org.apache.chemistry.opencmis.client.api.Document internalDocument = document.getInternalDocument();
	
		if ( index >= 1 ) {
			return null;
		}
		
		contentStream = internalDocument.getContentStream().getStream(); 
		return contentStream;
	}
}
