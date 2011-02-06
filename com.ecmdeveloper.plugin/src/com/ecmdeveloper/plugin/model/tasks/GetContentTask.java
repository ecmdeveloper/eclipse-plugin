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

package com.ecmdeveloper.plugin.model.tasks;

import java.io.InputStream;

import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.Document;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.ContentTransfer;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetContentTask extends BaseTask {

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
	
	public InputStream getContentStream() {
		return contentStream;
	}

	@Override
	protected Object execute() throws Exception {
		
		com.filenet.api.core.Document internalDocument = (com.filenet.api.core.Document) document.getObjectStoreObject();
	
		internalDocument.fetchProperties( new String[] { PropertyNames.CONTENT_ELEMENTS } );
		ContentElementList contentElementList = internalDocument.get_ContentElements();
		
		Object contentElement = contentElementList.get(index);

		if ( contentElement instanceof ContentTransfer ) {

			ContentTransfer contentTransfer = (ContentTransfer) contentElement;
			contentStream = contentTransfer.accessContentStream();
			return contentStream;
			
//			byte buffer[] = new byte[1000];
//			int bytesRead;
//			
//	        OutputStream out = new FileOutputStream( new File(outputFilename ) );
//			while ( (bytesRead = contentStream.read(buffer) ) != 0 ) {
//	            out.write(buffer, 0, bytesRead );
//			}
//	        out.close();
		}

		return contentStream;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return document.getObjectStore().getConnection();
	}

}
