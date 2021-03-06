/**
 * Copyright 2012, Ricardo Belfor
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

package com.ecmdeveloper.plugin.content.handlers;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;

import com.ecmdeveloper.plugin.content.wizard.UpdateDocumentContentWizard;
import com.ecmdeveloper.plugin.core.model.IDocument;

/**
 * @author ricardo.belfor
 *
 */
public class UpdateDocumentContentHandler extends AbstractDocumentHandler implements IHandler {

	@Override
	protected void handleDocument(IDocument document) {
		openUpdateDocumentContentWizard( document );
	}

	private void openUpdateDocumentContentWizard(IDocument document) {
		UpdateDocumentContentWizard wizard = new UpdateDocumentContentWizard( document );
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		dialog.create();
		dialog.open();
	}
}
