package com.ecmdeveloper.plugin.codemodule.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.ecmdeveloper.plugin.codemodule.util.messages"; //$NON-NLS-1$

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

	public static String CodeModuleEditorForm_AddLabel;
	public static String CodeModuleEditorForm_CodeModuleActionsDescription;
	public static String CodeModuleEditorForm_CodeModuleActionsTitle;
	public static String CodeModuleEditorForm_CodeModuleFilesDescription;
	public static String CodeModuleEditorForm_CodeModuleFilesText;
	public static String CodeModuleEditorForm_CodeModuleNamePrefix;
	public static String CodeModuleEditorForm_CodeModulePropertiesDescription;
	public static String CodeModuleEditorForm_CodeModulePropertiesTitle;
	public static String CodeModuleEditorForm_EmptyNameMessage;
	public static String CodeModuleEditorForm_FormTitle;
	public static String CodeModuleEditorForm_LastModifiedColumnLabel;
	public static String CodeModuleEditorForm_NameColumnLabel;
	public static String CodeModuleEditorForm_NameLabel;
	public static String CodeModuleEditorForm_NoFilesMessage;
	public static String CodeModuleEditorForm_ObjectStoreLabel;
	public static String CodeModuleEditorForm_PathColumnLabel;
	public static String CodeModuleEditorForm_RemoveLabel;
	public static String CodeModuleEditorForm_UpdateCodeModuleLinkText;
	public static String EditCodeModuleHandler_HandlerName;
	public static String EditCodeModuleHandler_OpenCodeModuleEditorError;
	public static String NewCodeModuleWizard_OpenEditorFailedMessage;
	public static String NewCodeModuleWizard_WizardName;
	public static String NewCodeModuleWizardPage_ConnectLabel;
	public static String NewCodeModuleWizardPage_Description;
	public static String NewCodeModuleWizardPage_NameLabel;
	public static String NewCodeModuleWizardPage_PageName;
	public static String NewCodeModuleWizardPage_SelectObjectStoreLabel;
	public static String NewCodeModuleWizardPage_Title;
	public static String RemoveCodeModuleHandler_HandlerName;
	public static String RemoveCodeModuleHandler_RemoveMessage;
	public static String UpdateCodeModuleHandler_ActionLabel;
	public static String UpdateCodeModuleHandler_ActionSelectionDialogTitle;
	public static String UpdateCodeModuleHandler_HandlerName;
	public static String UpdateCodeModuleHandler_SelectActionsMessage;
	public static String UpdateCodeModuleHandler_UpdateMessage;
}
