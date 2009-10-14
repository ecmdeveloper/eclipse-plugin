/**
 * 
 */
package com.ecmdeveloper.plugin.codemodule.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.codemodule.Activator;
import com.ecmdeveloper.plugin.codemodule.model.CodeModuleFile;
import com.ecmdeveloper.plugin.codemodule.util.IconFiles;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.model.ObjectStoresManager;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;

/**
 * @author Ricardo Belfor
 *
 */
public class CodeModulesViewLabelProvider extends ObjectStoreItemLabelProvider implements ITableLabelProvider {

	private static final int TYPE_COLUMN = 0;
	private static final int NAME_COLUMN = 1;
	private static final int LOCATION_COLUMN = 2;
//	private ObjectStoreItemLabelProvider objectStoreItemLabelProvider;
	private ObjectStoresManager objectStoresManager;
	
	public CodeModulesViewLabelProvider() {
		super();
//		objectStoreItemLabelProvider = new ObjectStoreItemLabelProvider();
		objectStoresManager = ObjectStoresManager.getManager();
	}

//	public void addListener(ILabelProviderListener listener) {
//		objectStoreItemLabelProvider.addListener(listener);
//		super.addListener(listener);
//	}
//
//	public void removeListener(ILabelProviderListener listener) {
//		objectStoreItemLabelProvider.removeListener(listener);
//		super.removeListener(listener);
//	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		
		switch ( columnIndex ) {
		case TYPE_COLUMN:
			return Activator.getImage( IconFiles.ICON_CODEMODULE );
		case LOCATION_COLUMN:
			return super.getImage( getObjectStore(element) );
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		
		switch (columnIndex) {
		case TYPE_COLUMN:
			return "";
		case NAME_COLUMN:
			return ((CodeModuleFile) element).getName();
		case LOCATION_COLUMN:
			return super.getText( getObjectStore(element) );
		default:
			return "";
		}
	}

	private ObjectStore getObjectStore(Object element) {
		CodeModuleFile codeModuleFile = (CodeModuleFile) element; 
		ObjectStore objectStore = objectStoresManager.getObjectStore(codeModuleFile.getConnectionName(), codeModuleFile.getObjectStoreName() );
		return objectStore;
	}
}
