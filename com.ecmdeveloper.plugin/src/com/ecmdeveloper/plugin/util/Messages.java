/**
 * Copyright 2009, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */
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
	public static String DeleteObjectStoreItemHandler_FailedMessage;
	public static String DeleteObjectStoreItemHandler_HandlerName;
	public static String DeleteObjectStoreItemHandler_MonitorMessage;
	public static String DeleteObjectStoreItemHandler_ProgressMessage;
	public static String HandlerName;
	public static String MoveObjectStoreItemHandler_ChooseDestinationMessage;
	public static String MoveObjectStoreItemHandler_HandlerName;
	public static String MoveObjectStoreItemHandler_MovingAcrossObjectStoresError;
	public static String NewNameMessage;
	public static String ObjectStore_NotConnectedMessage;
	public static String RefreshObjectStoreItemHandler_HandlerName;
	public static String RenameObjectStoreItemHandler_0;
	public static String RenameObjectStoreItemHandler_1;
	public static String RenameObjectStoreItemHandler_FailedMessage;
	public static String RenameObjectStoreItemHandler_InvalidCharsMessage;
	public static String RenameObjectStoreItemHandler_InvalidLengthMessage;
	public static String RenameObjectStoreItemHandler_MonitorMessage;
	public static String RenameObjectStoreItemHandler_ProgressMessage;
}
