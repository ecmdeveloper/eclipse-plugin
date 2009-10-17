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
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.DynamicReferentialContainmentRelationship;
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
	
	public void update(Collection<File> files) {
		
		versionSeries.fetchProperties( new String[]  { PropertyNames.CURRENT_VERSION } );
		Document document = (Document) versionSeries.get_CurrentVersion();
		
		document.checkout(ReservationType.EXCLUSIVE, null, document.getClassName(), null);
		document.save(RefreshMode.REFRESH);
		com.filenet.api.core.Document reservation = (com.filenet.api.core.Document) document.get_Reservation();
		
		ContentElementList contentElementList = createContent(files);
		
		reservation.set_ContentElements(contentElementList);
		reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION );
		reservation.getProperties().putValue( "DocumentTitle", name );
		reservation.save(RefreshMode.REFRESH);
	}

	@SuppressWarnings("unchecked")
	private static ContentElementList createContent(Collection<File> files) {
		
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
		this.name = name;
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

	public static CodeModule createInstance(String name, Collection<File> files, ObjectStore objectStore) {

		com.filenet.api.core.Folder folder = Factory.Folder.fetchInstance(
				(com.filenet.api.core.ObjectStore) objectStore
						.getObjectStoreObject(), "/CodeModules", null);

		com.filenet.api.admin.CodeModule codeModule = Factory.CodeModule
				.createInstance((com.filenet.api.core.ObjectStore) objectStore
						.getObjectStoreObject(), "CodeModule"); 

		codeModule.getProperties().putValue("DocumentTitle", name);
		codeModule.set_ContentElements( createContent(files) );
		codeModule.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		codeModule.save(RefreshMode.REFRESH);

		DynamicReferentialContainmentRelationship relation = (DynamicReferentialContainmentRelationship) folder
				.file(  codeModule,
						AutoUniqueName.AUTO_UNIQUE,
						name,
						DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE) ;
		relation.save(RefreshMode.NO_REFRESH);

		return new CodeModule( codeModule.get_VersionSeries(), objectStore, objectStore );
	}
}
