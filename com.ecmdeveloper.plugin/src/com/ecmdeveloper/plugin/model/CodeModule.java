package com.ecmdeveloper.plugin.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.filenet.api.collection.ActionSet;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Factory;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class CodeModule extends Document {

	public CodeModule(Object document, IObjectStoreItem parent) {
		super(document, parent);
	}

	public Collection<Action> getActions() {
	
		document.fetchProperties( new String[] { PropertyNames.REFERENCING_ACTIONS } );
		ActionSet actionSet = ((com.filenet.api.admin.CodeModule)document).get_ReferencingActions();

		Iterator<?> iterator = actionSet.iterator();
		Set<Action> actions = new HashSet<Action>();

		while (iterator.hasNext()) {
			actions.add( new Action( iterator.next(), null) );
		}
		
		return actions;
	}
	
	@SuppressWarnings("unchecked")
	public void update(CodeModuleFile codeModuleFile) {
		
		document.checkout(ReservationType.EXCLUSIVE, null, document.getClassName(), null);
		document.save(RefreshMode.REFRESH);
		com.filenet.api.core.Document reservation = (com.filenet.api.core.Document) document.get_Reservation();
		
		ContentElementList contentElementList = Factory.ContentElement.createList();

		ContentTransfer content = Factory.ContentTransfer.createInstance();

		for ( File file : codeModuleFile.getFiles() ) {
			
			if ( ! file.exists() ) {
				continue;
			}
			
			content.set_RetrievalName( file.getName() );
			try {
				content.setCaptureSource( new FileInputStream( file ) );
			} catch (FileNotFoundException e) {
				// Should not happen as only existing files are added...
			}
			
			if ( file.getName().endsWith(".jar") ) {
				content.set_ContentType( "application/x-zip-compressed" );
			} else if ( file.getName().endsWith( ".class" ) ) {
				content.set_ContentType( "application/java" );
			}
				
			contentElementList.add(content);
		}
		
		reservation.set_ContentElements(contentElementList);
		reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION );
		reservation.save(RefreshMode.REFRESH);
	}
}
