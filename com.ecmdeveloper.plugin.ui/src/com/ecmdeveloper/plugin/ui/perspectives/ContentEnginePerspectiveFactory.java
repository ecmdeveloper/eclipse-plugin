/**
 * Copyright 2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.ui.perspectives;

import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author ricardo.belfor
 *
 */
public class ContentEnginePerspectiveFactory implements IPerspectiveFactory {

	private static final String CLASS_DIAGRAM_PERSPECTIVE_ID = "com.ecmdeveloper.plugin.diagrams.classDiagramPerspective";
	
	private static final String TRACKED_FILES_VIEW_ID = "com.ecmdeveloper.plugin.views.TrackedFiles";
	private static final String CODE_MODULES_VIEW_ID = "com.ecmdeveloper.plugin.views.codeModulesView";
	private static final String OBJECT_STORE_FAVORITES_VIEW_ID = "com.ecmdeveloper.plugin.views.ObjectStoreFavorites";
	private static final String OBJECT_STORES_VIEW_ID = "com.ecmdeveloper.plugin.views.ObjectStoresView";
	private static final String CLASSES_VIEW_ID = "com.ecmdeveloper.plugin.classes.views.ClassesView";
	
	private static final String ID_PACKAGES = "org.eclipse.jdt.ui.PackageExplorer";
	private static final String ID_PROJECT_EXPLORER = "org.eclipse.ui.navigator.ProjectExplorer";

	private static final String NEW_CLASS_DIAGRAM_COMMAND_ID = "com.ecmdeveloper.plugin.diagrams.wizards.NewClassDiagramWizard";
	private static final String NEW_CODE_MODULE_COMMAND_ID = "com.ecmdeveloper.plugin.wizard.newCodeModule";
	private static final String NEW_FOLDER_COMMAND_ID = "com.ecmdeveloper.plugin.newFolder";
	private static final String NEW_DOCUMENT_COMMAND_ID = "com.ecmdeveloper.plugin.newDocument";
	private static final String NEW_CUSTOM_OBJECT_COMMAND_ID = "com.ecmdeveloper.plugin.newCustomObject";
	private static final String NEW_EVENT_ACTION_COMMAND_ID = "com.ecmdeveloper.plugin.wizard.newEventAction";
	private static final String NEW_DOCUMENT_CLASSIFIER_COMMAND_ID = "com.ecmdeveloper.plugin.wizard.newDocumentClassifier";
	private static final String NEW_DOCUMENT_LIFECYCLE_ACTION_HANDLER_COMMAND_ID = "com.ecmdeveloper.plugin.wizard.newDocumentLifecycleActionHandler";

	@Override
	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();
		
		addLeftViews(layout, editorArea);
		addRightViews(layout, editorArea);
		addBottomViews(layout, editorArea);
		addShortcuts(layout);
	}

	private void addBottomViews(IPageLayout layout, String editorArea) {
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.66f, editorArea );
		
		bottom.addView(CODE_MODULES_VIEW_ID );
		bottom.addView(TRACKED_FILES_VIEW_ID);
		
		bottom.addPlaceholder(IPageLayout.ID_PROP_SHEET);
		bottom.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
		bottom.addPlaceholder(IPageLayout.ID_PROP_SHEET);
	}

	private void addRightViews(IPageLayout layout, String editorArea) {
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.66f, editorArea );
		right.addView( IPageLayout.ID_RES_NAV );
		right.addPlaceholder(ID_PACKAGES);
		right.addPlaceholder(ID_PROJECT_EXPLORER);
	}

	private void addLeftViews(IPageLayout layout, String editorArea) {
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea );
		left.addView( OBJECT_STORES_VIEW_ID);
		left.addView( OBJECT_STORE_FAVORITES_VIEW_ID );
		left.addPlaceholder( CLASSES_VIEW_ID );
	}

	private void addShortcuts(IPageLayout layout) {
		addPerspectiveShortcuts(layout);
		addShowViewShortcuts(layout);
		addNewWizardShortcuts(layout);
		layout.addActionSet("com.ecmdeveloper.plugin.search.actionSet");
	}

	private void addPerspectiveShortcuts(IPageLayout layout) {
		layout.addPerspectiveShortcut(CLASS_DIAGRAM_PERSPECTIVE_ID);
	}

	private void addShowViewShortcuts(IPageLayout layout) {
		layout.addShowViewShortcut(OBJECT_STORES_VIEW_ID);
		layout.addShowViewShortcut(OBJECT_STORE_FAVORITES_VIEW_ID);
		layout.addShowViewShortcut(CODE_MODULES_VIEW_ID );
		layout.addShowViewShortcut(TRACKED_FILES_VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(CLASSES_VIEW_ID);
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);
	}

	private void addNewWizardShortcuts(IPageLayout layout) {
		layout.addNewWizardShortcut(NEW_CLASS_DIAGRAM_COMMAND_ID);
		layout.addNewWizardShortcut(NEW_CODE_MODULE_COMMAND_ID);
		layout.addNewWizardShortcut(NEW_FOLDER_COMMAND_ID);
		layout.addNewWizardShortcut(NEW_DOCUMENT_COMMAND_ID);
		layout.addNewWizardShortcut(NEW_CUSTOM_OBJECT_COMMAND_ID);
		layout.addNewWizardShortcut(NEW_EVENT_ACTION_COMMAND_ID);
		layout.addNewWizardShortcut(NEW_DOCUMENT_LIFECYCLE_ACTION_HANDLER_COMMAND_ID);
		layout.addNewWizardShortcut(NEW_DOCUMENT_CLASSIFIER_COMMAND_ID);
	}
}
