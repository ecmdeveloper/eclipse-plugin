/**
 * Copyright 2009, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.editors;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.ui.actions.ActionFactory;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramEditorContributor extends ActionBarContributor {

	@Override
	protected void buildActions() {
	}

	@Override
	protected void declareGlobalActionKeys() {
	      addGlobalActionKey(ActionFactory.UNDO.getId());
	      addGlobalActionKey(ActionFactory.REDO.getId());
	      addGlobalActionKey(ActionFactory.PRINT.getId());
	      addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
	}
}
