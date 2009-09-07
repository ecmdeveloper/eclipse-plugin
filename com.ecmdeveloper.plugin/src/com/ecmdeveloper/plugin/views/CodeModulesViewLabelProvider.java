/**
 * 
 */
package com.ecmdeveloper.plugin.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ecmdeveloper.plugin.model.CodeModuleFile;

/**
 * @author Ricardo Belfor
 *
 */
public class CodeModulesViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		
		switch (columnIndex) {
		case 0: // Type column
			return "";
		case 1: // Name column
			return ((CodeModuleFile) element).getName();
		case 2: // Location column
			return ((CodeModuleFile) element).getConnectionName() + ":"
					+ ((CodeModuleFile) element).getObjectStoreName();
		default:
			return "";
		}
	}
}
