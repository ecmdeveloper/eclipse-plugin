package com.ecmdeveloper.plugin.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;

public class ObjectStoreItemLabelProvider extends LabelProvider {

	public String getText(Object obj) {
		if ( obj instanceof IObjectStoreItem )
		{
			return ((IObjectStoreItem)obj).getName();
		}
		return obj.toString();
	}
	public Image getImage(Object obj) {

		String imageKey = ISharedImages.IMG_OBJS_INFO_TSK;
		
		if (obj instanceof ObjectStore) {
			//imageKey = ISharedImages.IMG_DEF_VIEW;
			imageKey = ISharedImages.IMG_TOOL_DELETE;
		} else if (obj instanceof Folder) {
		   imageKey = ISharedImages.IMG_OBJ_FOLDER;
		} else if (obj instanceof Document) {
		   imageKey = ISharedImages.IMG_OBJ_FILE;
		} else if (obj instanceof CustomObject) {
		   imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		}
		
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}

}
