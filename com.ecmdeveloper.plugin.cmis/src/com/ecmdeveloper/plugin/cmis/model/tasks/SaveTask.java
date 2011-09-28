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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Collection;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import com.ecmdeveloper.plugin.core.model.tasks.ISaveTask;
import com.ecmdeveloper.plugin.cmis.model.Document;
import com.ecmdeveloper.plugin.cmis.model.ObjectStoreItemFactory;

/**
 * @author Ricardo.Belfor
 * @param <reservation>
 *
 */
public class SaveTask extends DocumentTask implements ISaveTask {

	private Collection<Object> contents;
	private String mimeType;
	private Document reservationDocument;

	public SaveTask(Document document, Collection<Object> contents, String mimeType ) {
		super(document);
		this.contents = contents;
		this.mimeType = mimeType;
	}

	@Override
	public Object call() throws Exception {

		org.apache.chemistry.opencmis.client.api.Document reservation = getReservation();
		ContentStream content = createContent();
		if ( content != null ) {
			reservation.setContentStream( content, true );
		}
		reservationDocument = ObjectStoreItemFactory.createDocument( reservation, getDocument().getParent(), getDocument().getObjectStore() );

		return null;
	}

	public Document getReservationDocument() {
		return reservationDocument;
	}

	private ContentStream createContent() throws Exception {

		Session session = getDocument().getObjectStore().getSession();
		
		for ( Object content : contents ) {
			
			if ( content instanceof File ) {
				return createFileContent((File) content, session);
			} else if ( content instanceof IFile ) {
				return createFileContent( (IFile) content, session);
			}
		}
		
		return null;
	}

	private ContentStream createFileContent(File file, Session session) throws FileNotFoundException {

		ContentStream contentStream = session.getObjectFactory().createContentStream(
				file.getName(), 0,
				/*getContentType(file.getName())*/ mimeType, new FileInputStream( file ) );

		return contentStream;
	}

	private ContentStream createFileContent(IFile file, Session session) throws CoreException {

		ContentStream contentStream = session.getObjectFactory().createContentStream(
		file.getName(), 0,
		/*getContentType(file.getName())*/ mimeType, file.getContents() );

		return contentStream;
	}
	
	private String getContentType(String filename) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		return fileNameMap.getContentTypeFor( filename );
	}

}
