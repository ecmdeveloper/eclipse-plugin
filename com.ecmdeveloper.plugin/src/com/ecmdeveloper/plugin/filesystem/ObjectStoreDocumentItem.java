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

package com.ecmdeveloper.plugin.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.tasks.GetContentTask;
import com.ecmdeveloper.plugin.util.PluginLog;

/**
 * @author Ricardo.Belfor
 *
 */
public class ObjectStoreDocumentItem extends ObjectStoreFileStore {

	protected Document document;

	public ObjectStoreDocumentItem(Document document) {
		super(document);
		this.document = document;
	}

	@Override
	public String getName() {
		return document.getContainmentName();
	}
	
	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor)
			throws CoreException {
		try {
			GetContentTask task = new GetContentTask( document );
			InputStream contentStream = (InputStream) Activator.getDefault().getTaskManager().executeTaskSync(task);
			String filename = "c:/temp/content";
			copyStreamToFile(contentStream, filename );
			return new FileInputStream( filename);
		} catch (ExecutionException e) {
			throw new CoreException(PluginLog.createStatus( IStatus.ERROR, IStatus.ERROR, "Getting childs failed", e ) );
		} catch (IOException e) {
			throw new CoreException(PluginLog.createStatus( IStatus.ERROR, IStatus.ERROR, "Getting childs failed", e ) );
		}
//		String filename = "c:/temp/content";
//		try {
//			return new FileInputStream( filename);
//		} catch (FileNotFoundException e) {
//			throw new CoreException(PluginLog.createStatus( IStatus.ERROR, IStatus.ERROR, "Getting childs failed", e ) );
//		}
	}
	
	private void copyStreamToFile(InputStream contentStream, String outputFilename) throws IOException {
		byte buffer[] = new byte[1000];
		int bytesRead;
		
        OutputStream out = new FileOutputStream( new File(outputFilename ) );
		while ( (bytesRead = contentStream.read(buffer) ) > 0 ) {
            out.write(buffer, 0, bytesRead );
		}
		contentStream.close();
        out.close();

	}
	@Override
	public String[] childNames(int options, IProgressMonitor monitor)
			throws CoreException {
		return new String[0];
	}

	@Override
	public URI toURI() {
		try {
			return ObjectStoreFileSystem.toURI(document);
		} catch (URISyntaxException e) {
			throw new RuntimeException( e );
		}
	}
	
	@Override
	public IFileStore getChild(String name) {
		return null;
	}
}
