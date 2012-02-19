package com.ecmdeveloper.plugin.codemodule.handlers.util;

import java.text.MessageFormat;

import com.ecmdeveloper.plugin.core.model.IAction;
import com.ecmdeveloper.plugin.core.model.IObjectStoreItem;
import com.ecmdeveloper.plugin.ui.views.ObjectStoreItemLabelProvider;

public class CodeModuleActionLabelProvider extends ObjectStoreItemLabelProvider {

	private static final String ACTION_LABEL = "{0} (version: {1})";

	public String getText(Object object) {
		if ( object instanceof IAction ) {
			String name = ((IObjectStoreItem) object).getName();
			String codeModuleVersion = ((IAction) object).getCodeModuleVersion();
			return  MessageFormat.format( ACTION_LABEL, name, codeModuleVersion ); 
		}
		
		return super.getText(object);
	}
}
