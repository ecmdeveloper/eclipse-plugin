package com.ecmdeveloper.plugin.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.ecmdeveloper.plugin.util.messages"; //$NON-NLS-1$

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

	public static String ConnectObjectStoreHandler_HandlerName;
	public static String DeleteObjectStoreItemHandler_DeleteMessage;
	public static String DeleteObjectStoreItemHandler_HandlerName;
	public static String EditCodeModuleHandler_HandlerName;
	public static String EditCodeModuleHandler_OpenCodeModuleEditorError;
	public static String HandlerName;
	public static String MoveObjectStoreItemHandler_ChooseDestinationMessage;
	public static String MoveObjectStoreItemHandler_HandlerName;
	public static String MoveObjectStoreItemHandler_MovingAcrossObjectStoresError;
	public static String NewNameMessage;
	public static String RefreshObjectStoreItemHandler_HandlerName;
	public static String RemoveCodeModuleHandler_HandlerName;
	public static String RemoveCodeModuleHandler_RemoveMessage;
	public static String RenameObjectStoreItemHandler_0;
	public static String RenameObjectStoreItemHandler_1;
	public static String RenameObjectStoreItemHandler_InvalidCharsMessage;
	public static String RenameObjectStoreItemHandler_InvalidLengthMessage;
	public static String UpdateCodeModuleHandler_ActionLabel;
	public static String UpdateCodeModuleHandler_ActionSelectionDialogTitle;
	public static String UpdateCodeModuleHandler_HandlerName;
	public static String UpdateCodeModuleHandler_SelectActionsMessage;
	public static String UpdateCodeModuleHandler_UpdateMessage;
}
