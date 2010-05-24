/**
 * Copyright 2009, Ricardo Belfor
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ecmdeveloper.plugin.model.Document;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.ContentTransfer;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetContentAsFileTask extends BaseTask {

	private Document document;
	private int index;
	private String outputPath;
	
	public GetContentAsFileTask(Document document, String outputPath, int index ) {
		super();
		this.document = document;
		this.index = index;
		this.outputPath = outputPath;
	}

	public GetContentAsFileTask(Document document, String outputPath ) {
		this(document, outputPath, 0 );
	}
	
	@Override
	public String call() throws Exception {
		
		com.filenet.api.core.Document internalDocument = (com.filenet.api.core.Document) document.getObjectStoreObject();
	
		internalDocument.fetchProperties( new String[] { PropertyNames.CONTENT_ELEMENTS, PropertyNames.RETRIEVAL_NAME } );
		ContentElementList contentElementList = internalDocument.get_ContentElements();

		if ( index >= contentElementList.size() ) {
			return null;
		}
		
		Object contentElement = contentElementList.get(index);

		if ( contentElement instanceof ContentTransfer ) {

			return copyContentElementToFile(contentElement);
		}

		return null;
	}

	private String copyContentElementToFile(Object contentElement)
			throws FileNotFoundException, IOException {
		
		ContentTransfer contentTransfer = (ContentTransfer) contentElement;
		InputStream inputStream = contentTransfer.accessContentStream();
		String outputFilename = outputPath + File.separator + contentTransfer.get_RetrievalName();
		copyInputStreamToOutputFile(inputStream, outputFilename);
		
		return outputFilename;
	}

	private void copyInputStreamToOutputFile(InputStream contentStream,
			String outputFilename) throws FileNotFoundException, IOException {

		byte buffer[] = new byte[1000];
		int bytesRead;
		
		OutputStream out = new FileOutputStream( getWritableFile( outputFilename ) );
		while ( (bytesRead = contentStream.read(buffer) ) > 0 ) {
		    out.write(buffer, 0, bytesRead );
		}
		out.close();
		contentStream.close();
	}
	
	private File getWritableFile(String filename) {
		File file = new File( filename );
		if ( file.exists() ) {
			file.setWritable(true);
		}
		return file;
	}
}
