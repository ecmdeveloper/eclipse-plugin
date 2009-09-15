package com.ecmdeveloper.plugin.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ecmdeveloper.plugin.Activator;
import com.ecmdeveloper.plugin.model.CustomObject;
import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.model.Folder;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.ecmdeveloper.plugin.util.ImageCache;

public class ObjectStoreItemLabelProvider extends LabelProvider {

	private final ImageCache imageCache = new ImageCache();
	
	@Override
	public void dispose() {
		super.dispose();
		imageCache.dispose();
	}

	public String getText(Object obj) {

		if ( obj instanceof IObjectStoreItem )
		{
			String name = ((IObjectStoreItem) obj).getName();
			return name;
		}
		return obj.toString();
	}
	
	public Image getImage(Object obj) {

		String imageKey = ISharedImages.IMG_OBJS_INFO_TSK;
		
		if (obj instanceof ObjectStore) {
			return imageCache.getImage( Activator.getImageDescriptor("icons/database.png") );
			//imageKey = ISharedImages.IMG_DEF_VIEW;
			//imageKey = ISharedImages.IMG_TOOL_DELETE;
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
