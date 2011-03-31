/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ecmdeveloper.plugin.search.policies;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

import com.ecmdeveloper.plugin.search.commands.OrphanChildCommand;
import com.ecmdeveloper.plugin.search.commands.SetMainQueryCommand;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QueryElement;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;

public class AbstractContainerEditPolicy extends ContainerEditPolicy {

	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}

	public Command getOrphanChildrenCommand(GroupRequest request) {
		List parts = request.getEditParts();
		CompoundCommand result = new CompoundCommand("Orphan");
		for (int i = 0; i < parts.size(); i++) {
			OrphanChildCommand orphan = new OrphanChildCommand();
			QuerySubpart childModel = (QuerySubpart) ((EditPart) parts.get(i)).getModel();
			QueryDiagram parentModel = (QueryDiagram) getHost().getModel();
			orphan.setChild(childModel);
			orphan.setParent(parentModel);
			orphan.setLabel("Orphan");
			result.add(orphan);
		}
		return result.unwrap();
	}
}
