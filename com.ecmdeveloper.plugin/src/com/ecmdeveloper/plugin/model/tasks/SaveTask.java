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

package com.ecmdeveloper.plugin.model.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;

import com.ecmdeveloper.plugin.model.Document;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Factory;
import com.filenet.api.core.VersionSeries;

/**
 * @author Ricardo.Belfor
 *
 */
public class SaveTask extends DocumentTask {

	private Collection<Object> contents;
	private String mimeType;

	public SaveTask(Document document, Collection<Object> contents, String mimeType ) {
		super(document);
		this.contents = contents;
		this.mimeType = mimeType;
	}

	@Override
	public Object call() throws Exception {

		com.filenet.api.core.Document reservation = getReservation();
		reservation.set_ContentElements( createContent() );
		reservation.set_MimeType( mimeType );
		reservation.save( RefreshMode.REFRESH );
		
		return null;
	}

	private com.filenet.api.core.Document getReservation() {
		
		com.filenet.api.core.Document internalDocument = getInternalDocument();
		internalDocument.fetchProperties( new String[] { PropertyNames.VERSION_SERIES } );
		VersionSeries versionSeries = internalDocument.get_VersionSeries();
		versionSeries.fetchProperties( new String[] { PropertyNames.RESERVATION} );
		com.filenet.api.core.Document currentVersion = (com.filenet.api.core.Document) versionSeries
				.get_Reservation();

		return currentVersion;
	}
	
	@SuppressWarnings("unchecked")
	private ContentElementList createContent() throws Exception {
		
		ContentElementList contentElementList = Factory.ContentElement.createList();

		for ( Object content : contents ) {
			if ( content instanceof File ) {
				contentElementList.add( createFileContent((File) content));
			} else if ( content instanceof IFile ) {
				contentElementList.add( createFileContent( (IFile) content) );
			}
		}

		return contentElementList;
	}

	private ContentTransfer createFileContent(File file) throws FileNotFoundException {

		ContentTransfer content = Factory.ContentTransfer.createInstance();
		content.set_RetrievalName( file.getName() );
		content.setCaptureSource( new FileInputStream( file ) );
		content.set_ContentType( getContentType(file.getName()) );

		return content;
	}

	private ContentTransfer createFileContent(IFile file) throws CoreException {

		ContentTransfer content = Factory.ContentTransfer.createInstance();
		content.set_RetrievalName( file.getName() );
		content.setCaptureSource( file.getContents() );
		content.set_ContentType( getContentType(file.getName()) );

		return content;
	}
	
	private String getContentType(String filename) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		return fileNameMap.getContentTypeFor( filename );
	}
}
