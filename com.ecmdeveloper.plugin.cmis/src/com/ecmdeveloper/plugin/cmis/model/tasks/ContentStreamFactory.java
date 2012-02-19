/**
 * Copyright 2012, Ricardo Belfor
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
import java.util.Collection;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * @author ricardo.belfor
 *
 */
public class ContentStreamFactory {

	public static ContentStream createContent(Collection<Object> contents, String mimeType, Session session) throws Exception {

		for ( Object content : contents ) {
			
			if ( content instanceof File ) {
				return createFileContent((File) content, mimeType, session);
			} else if ( content instanceof IFile ) {
				return createFileContent( (IFile) content, mimeType, session);
			}
		}
		
		return null;
	}

	private static ContentStream createFileContent(File file, String mimeType, Session session) throws FileNotFoundException {

		ContentStream contentStream = session.getObjectFactory().createContentStream(
				file.getName(), 0, mimeType, new FileInputStream(file));

		return contentStream;
	}

	private static ContentStream createFileContent(IFile file, String mimeType, Session session) throws CoreException {

		ContentStream contentStream = session.getObjectFactory().createContentStream(
				file.getName(), 0, mimeType, file.getContents());

		return contentStream;
	}
}
