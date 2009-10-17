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
	public static String HandlerName;
	public static String MoveObjectStoreItemHandler_ChooseDestinationMessage;
	public static String MoveObjectStoreItemHandler_HandlerName;
	public static String MoveObjectStoreItemHandler_MovingAcrossObjectStoresError;
	public static String NewNameMessage;
	public static String ObjectStore_NotConnectedMessage;
	public static String RefreshObjectStoreItemHandler_HandlerName;
	public static String RenameObjectStoreItemHandler_0;
	public static String RenameObjectStoreItemHandler_1;
	public static String RenameObjectStoreItemHandler_InvalidCharsMessage;
	public static String RenameObjectStoreItemHandler_InvalidLengthMessage;
}
