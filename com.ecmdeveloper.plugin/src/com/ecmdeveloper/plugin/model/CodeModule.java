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
import com.filenet.api.collection.VersionableSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.IndependentlyPersistableObject;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class CodeModule extends ObjectStoreItem{

	protected VersionSeries versionSeries;
	
	public CodeModule(Object versionSeries, IObjectStoreItem parent, ObjectStore objectStore ) {
		
		super(parent, objectStore);
		this.versionSeries = (VersionSeries) versionSeries;
		refresh();
	}

	public Collection<Action> getActions() {

		PropertyFilter propertyFilter = new PropertyFilter();
		propertyFilter.addIncludeProperty( new FilterElement(null, null, null, PropertyNames.VERSIONS, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(null, null, null, PropertyNames.REFERENCING_ACTIONS, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.MAJOR_VERSION_NUMBER, null ) );
		propertyFilter.addIncludeProperty( new FilterElement(1, null, null, PropertyNames.MINOR_VERSION_NUMBER, null ) );

		versionSeries.fetchProperties(propertyFilter);
		
		VersionableSet versions = versionSeries.get_Versions();
		Iterator<?> versionsIterator = versions.iterator();
		Set<Action> actions = new HashSet<Action>();
		
		while ( versionsIterator.hasNext() ) {
			
			Document document = (Document) versionsIterator.next();
			ActionSet actionSet = ((com.filenet.api.admin.CodeModule)document).get_ReferencingActions();
			Iterator<?> iterator = actionSet.iterator();
	
			while (iterator.hasNext()) {
				Action action = new Action( iterator.next(), null, objectStore );
				String codeModuleVersion = document.get_MajorVersionNumber() + "." +
				document.get_MinorVersionNumber();
				action.setCodeModuleVersion(codeModuleVersion);
				actions.add( action );
			}
		}
		
		return actions;
	}
	
	@SuppressWarnings("unchecked")
	public void update(CodeModuleFile codeModuleFile) {
		
		versionSeries.fetchProperties( new String[]  { PropertyNames.CURRENT_VERSION } );
		Document document = (Document) versionSeries.get_CurrentVersion();
		
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

	@Override
	public IndependentlyPersistableObject getObjectStoreObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IObjectStoreItem> getChildren() {
		return null;
	}

	@Override
	public void refresh() {
		this.versionSeries.fetchProperties( new String[] { PropertyNames.NAME, PropertyNames.ID, PropertyNames.CURRENT_VERSION } );
		this.name = ((Document)this.versionSeries.get_CurrentVersion()).get_Name();
		this.id = this.versionSeries.get_Id().toString();
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		
		if ( adapter.equals( Document.class ) ) {
			versionSeries.fetchProperties( new String[]  { PropertyNames.CURRENT_VERSION } );
			return versionSeries.get_CurrentVersion();
		}
		return super.getAdapter(adapter);
	}
}
