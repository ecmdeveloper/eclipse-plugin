/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.WorkbenchException;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.model.Query;

/**
 * @author ricardo.belfor
 *
 */
public class QueryFileStore {

	private static final String QUERIES_FOLDER = "queries";
	private static final String QUERY_FILE_EXTENSION = "search";
	private static final String QUERY_FILE_FORMAT = "{0}.{1}";
	
	public void save(Query query) throws IOException {
		File file = getQueryFile(query.getName());
		QueryFile queryFile = new QueryFile(file);
		queryFile.save(query);
	}
	
	public Query load(String name) throws IOException, WorkbenchException {
		File file = getQueryFile(name);
		QueryFile queryFile = new QueryFile(file);
		return queryFile.read();
	}

	private File getQueryFile(String name) {
		String filename = MessageFormat.format(QUERY_FILE_FORMAT, name,
				QUERY_FILE_EXTENSION);
		
		return getQueryFileStorePath().append( filename ).toFile();
	}	

	private IPath getQueryFileStorePath() {
		IPath parentFolder = Activator.getDefault().getStateLocation().append(QUERIES_FOLDER);
		
		if ( ! parentFolder.toFile().exists() )
		{
			parentFolder.toFile().mkdir();
		}
		return parentFolder;
	}
	
	public Collection<QueryFileInfo> getQueryNames() {

		File[] files = getQueryFiles();
	    Collection<QueryFileInfo> queryFileInfos = new ArrayList<QueryFileInfo>();
		for ( File file : files ) {
			QueryFile queryFile = new QueryFile(file);
			try {
				queryFileInfos.add( queryFile.getInfo() );
			} catch (Exception e) {
				// TODO fix this!
				e.printStackTrace();
			}
	    }
	
	    return queryFileInfos;
	}

	private File[] getQueryFiles() {
		File queryFileStorePath = getQueryFileStorePath().toFile();

	    FilenameFilter fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File parent, String name) {
				return name.endsWith( "." + QUERY_FILE_EXTENSION );
			}
	    };
	    
	    return queryFileStorePath.listFiles(fileFilter);
	}
}
