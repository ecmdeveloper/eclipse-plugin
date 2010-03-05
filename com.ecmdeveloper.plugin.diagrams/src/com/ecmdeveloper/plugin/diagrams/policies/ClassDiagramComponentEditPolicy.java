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

package com.ecmdeveloper.plugin.diagrams.policies;

import java.util.Iterator;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.ecmdeveloper.plugin.diagrams.commands.ClassDiagramElementDeleteCommand;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElement;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class ClassDiagramComponentEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {

		CompoundCommand delete = new CompoundCommand();
	      for (Iterator<?> iterator = deleteRequest.getEditParts().iterator(); iterator.hasNext();) {
	         Object item = ((EditPart) iterator.next()).getModel();
	         if (!(item instanceof ClassDiagramElement))
	            continue;
	         delete.add(new ClassDiagramElementDeleteCommand(
					(ClassDiagramElement) item, 
					((ClassDiagramElement) item).getParent()));
	      }
	      return delete;
	}
}
