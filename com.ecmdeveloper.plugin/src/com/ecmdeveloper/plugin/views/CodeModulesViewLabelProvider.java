/**
 * 
 */
package com.ecmdeveloper.plugin.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.CodeModuleFile;
import com.ecmdeveloper.plugin.util.IconFiles;

/**
 * @author Ricardo Belfor
 *
 */
public class CodeModulesViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final int TYPE_COLUMN = 0;
	private static final int NAME_COLUMN = 1;
	private static final int LOCATION_COLUMN = 2;

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		
		switch ( columnIndex ) {
		case TYPE_COLUMN:
			return Activator.getImage( IconFiles.ICON_CODEMODULE );
		case LOCATION_COLUMN:
			return Activator.getImage( IconFiles.ICON_OBJECTSTORE );
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
			return ((CodeModuleFile) element).getConnectionName() + ":"
					+ ((CodeModuleFile) element).getObjectStoreName();
		default:
			return "";
		}
	}
}
