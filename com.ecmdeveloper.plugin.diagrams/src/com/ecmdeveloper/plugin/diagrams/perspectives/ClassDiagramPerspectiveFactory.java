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

package com.ecmdeveloper.plugin.diagrams.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramPerspectiveFactory implements IPerspectiveFactory {

	private static final String CONTENT_ENGINE_PERSPECTIVE_ID = "com.ecmdeveloper.plugin.ui.contentEnginePerspective";
	private static final String CLASSES_VIEW_ID = "com.ecmdeveloper.plugin.classes.views.ClassesView";
	private static final String NEW_CLASS_DIAGRAM_COMMAND_ID = "com.ecmdeveloper.plugin.diagrams.wizards.NewClassDiagramWizard";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		addViews(layout);
		addShortcuts(layout);
	}

	private void addViews(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
		layout.addView(CLASSES_VIEW_ID, IPageLayout.LEFT, 0.25f, editorArea );
		layout.addView(IPageLayout.ID_RES_NAV , IPageLayout.BOTTOM, 0.5f, CLASSES_VIEW_ID );
		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.66f, editorArea );
		bottom.addView( IPageLayout.ID_PROP_SHEET );
	}

	private void addShortcuts(IPageLayout layout) {
		layout.addNewWizardShortcut(NEW_CLASS_DIAGRAM_COMMAND_ID);
		layout.addShowViewShortcut(CLASSES_VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addPerspectiveShortcut(CONTENT_ENGINE_PERSPECTIVE_ID);
	}
}
