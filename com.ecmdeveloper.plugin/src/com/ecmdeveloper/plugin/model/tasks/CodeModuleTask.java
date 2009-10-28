/**
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
