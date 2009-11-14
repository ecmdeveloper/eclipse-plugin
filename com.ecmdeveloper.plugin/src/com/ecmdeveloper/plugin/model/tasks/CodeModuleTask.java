/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
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
import java.util.Collection;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Factory;

/**
 * @author Ricardo.Belfor
 *
 */
public abstract class CodeModuleTask extends BaseTask {

	@SuppressWarnings("unchecked")
	protected ContentElementList createContent(Collection<File> files) {
		
		ContentElementList contentElementList = Factory.ContentElement.createList();
		ContentTransfer content = Factory.ContentTransfer.createInstance();

		for ( File file : files ) {
			
			if ( ! file.exists() ) {
				continue;
			}
			
			content.set_RetrievalName( file.getName() );
			try {
				content.setCaptureSource( new FileInputStream( file ) );
			} catch (FileNotFoundException e) {
				// Should not happen as only existing files are added...
			}
			
			if ( file.getName().toLowerCase().endsWith(".jar") ||
				 file.getName().toLowerCase().endsWith(".zip") ) {
				content.set_ContentType( "application/x-zip-compressed" );
			} else if ( file.getName().endsWith( ".class" ) ) {
				content.set_ContentType( "application/java" );
			}
				
			contentElementList.add(content);
		}
		return contentElementList;
	}
}
