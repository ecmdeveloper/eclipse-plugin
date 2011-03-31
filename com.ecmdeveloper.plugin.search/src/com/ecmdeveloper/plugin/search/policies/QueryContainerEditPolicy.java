/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.search.policies;

import java.util.Iterator;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.ecmdeveloper.plugin.search.commands.AddCommand;
import com.ecmdeveloper.plugin.search.commands.CloneCommand;
import com.ecmdeveloper.plugin.search.commands.CreateCommand;
import com.ecmdeveloper.plugin.search.commands.CreateComparisonCommand;
import com.ecmdeveloper.plugin.search.commands.CreateNullTestCommand;
import com.ecmdeveloper.plugin.search.commands.ReorderPartCommand;
import com.ecmdeveloper.plugin.search.commands.SetMainQueryCommand;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryContainer;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QueryElement;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;
import com.ecmdeveloper.plugin.search.parts.QueryContainerEditPart;

/**
 * @author ricardo.belfor
 *
 */
public class QueryContainerEditPolicy extends FlowLayoutEditPolicy {
//public class QueryContainerEditPolicy extends LayoutEditPolicy {

	private QueryCommandFactory queryCommandFactory;
	
	public QueryContainerEditPolicy(QueryCommandFactory queryCommandFactory) {
		this.queryCommandFactory = queryCommandFactory;
	}

	protected boolean isHorizontal() {
//		IFigure figure = ((GraphicalEditPart)getHost()).getContentPane();
//		return ((ToolbarLayout)figure.getLayoutManager()).isHorizontal();
		return false;
	}
	
	protected Command getCloneCommand(ChangeBoundsRequest request) {
		CloneCommand clone = new CloneCommand();
		clone.setParent((QueryDiagram)getHost().getModel());
		
	 	EditPart after = getInsertionReference(request);
		int index = getHost().getChildren().indexOf(after);
		
		Iterator<?> iterator = request.getEditParts().iterator();
		GraphicalEditPart currPart = null;
		
		while (iterator.hasNext()) {
			currPart = (GraphicalEditPart)iterator.next();
			clone.addPart((QuerySubpart)currPart.getModel(), index++);
		}
		
		return clone;
	}
		
	protected Command createAddCommand(EditPart child, EditPart after) {
		AddCommand command = new AddCommand();
		
		QueryContainer parentModel = (QueryContainer)getHost().getModel();
		QuerySubpart childModel = (QuerySubpart)child.getModel();
		command.setChild(childModel);
		command.setParent(parentModel);
		
		int index = getHost().getChildren().indexOf(after);
		command.setIndex(index);
		
		return chainSetMainQueryCommand(command, parentModel, childModel);
	}

	private Command chainSetMainQueryCommand(AddCommand command, QueryContainer parentModel, QuerySubpart childModel) {
		
		Query query = childModel.getQuery();
		if ( childModel.equals( query.getMainQuery() ) ) {
			QueryElement rootParent = parentModel;
			if ( ! rootParent.isRootDiagram() ) {
				while ( !rootParent.getParent().isRootDiagram() ) {
					rootParent = rootParent.getParent();
				}
				return command.chain( new SetMainQueryCommand(rootParent, query ) );
			}
		} 
		
		return command;
	}

	protected EditPolicy createChildEditPolicy(EditPart child) {
		QueryResizableEditPolicy policy = new QueryResizableEditPolicy();
		policy.setResizeDirections(0);
		return policy;
	}

	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		QuerySubpart childModel = (QuerySubpart)child.getModel();
		QueryDiagram parentModel = (QueryDiagram)getHost().getModel();
		
		int oldIndex = getHost().getChildren().indexOf(child);
		int newIndex = getHost().getChildren().indexOf(after);
		if (newIndex > oldIndex)
			newIndex--;
		ReorderPartCommand command = new ReorderPartCommand(childModel, parentModel, newIndex);

		return command;
	}

	protected Command getCreateCommand(CreateRequest request) {
		CreateCommand command = queryCommandFactory.getCreateCommand(request);
		EditPart after = getInsertionReference(request);
		command.setChild((QuerySubpart)request.getNewObject());
		QueryContainer parent = (QueryContainer)getHost().getModel();
		command.setParent(parent);
		int index = getHost().getChildren().indexOf(after);
		command.setIndex(index);
		return command;
	}
}
