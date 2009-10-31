package com.ecmdeveloper.plugin.codemodule.handlers.util;

import java.text.MessageFormat;

import com.ecmdeveloper.plugin.model.Action;
import com.ecmdeveloper.plugin.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.views.ObjectStoreItemLabelProvider;

public class CodeModuleActionLabelProvider extends ObjectStoreItemLabelProvider {

	private static final String ACTION_LABEL = "{0} (version: {1})";

	public String getText(Object object) {
		if ( object instanceof Action ) {
			String name = ((IObjectStoreItem) object).getName();
			String codeModuleVersion = ((Action) object).getCodeModuleVersion();
			return  MessageFormat.format( ACTION_LABEL, name, codeModuleVersion ); 
		}
		
		return super.getText(object);
	}
}
