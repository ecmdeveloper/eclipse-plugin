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

package com.ecmdeveloper.plugin.content.handlers;

import org.eclipse.jface.wizard.WizardDialog;

import com.ecmdeveloper.plugin.model.Document;
import com.ecmdeveloper.plugin.content.wizard.CheckinWizard;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinDocumentHandler extends AbstractDocumentHandler {

	@Override
	protected void handleDocument(Document objectStoreItem) {
		openCheckinWizard((Document) objectStoreItem);
	}

	private void openCheckinWizard(Document document) {
		CheckinWizard wizard = new CheckinWizard( document );
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		dialog.create();
		dialog.open();
	}
}
