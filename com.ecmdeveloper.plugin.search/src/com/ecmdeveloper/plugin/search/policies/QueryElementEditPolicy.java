/**
 * Copyright 2011, Ricardo Belfor
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
package com.ecmdeveloper.plugin.search.policies;

import java.util.Map;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.ecmdeveloper.plugin.search.actions.ConvertToTextAction;
import com.ecmdeveloper.plugin.search.actions.EditQueryComponentAction;
import com.ecmdeveloper.plugin.search.actions.SetMainQueryAction;
import com.ecmdeveloper.plugin.search.commands.ConvertToTextCommand;
import com.ecmdeveloper.plugin.search.commands.DeleteCommand;
import com.ecmdeveloper.plugin.search.commands.EditClassTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditComparisonCommand;
import com.ecmdeveloper.plugin.search.commands.EditFreeTextCommand;
import com.ecmdeveloper.plugin.search.commands.EditFullTextQueryCommand;
import com.ecmdeveloper.plugin.search.commands.EditInFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditInSubFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditNullTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditThisInFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditThisInTreeTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditWildcardTestCommand;
import com.ecmdeveloper.plugin.search.commands.SetMainQueryCommand;
import com.ecmdeveloper.plugin.search.model.ClassTest;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.FreeText;
import com.ecmdeveloper.plugin.search.model.FullTextQuery;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QueryElement;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;
import com.ecmdeveloper.plugin.search.model.ThisInFolderTest;
import com.ecmdeveloper.plugin.search.model.ThisInTreeTest;
import com.ecmdeveloper.plugin.search.model.WildcardTest;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class QueryElementEditPolicy extends ComponentEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if ( EditQueryComponentAction.REQUEST_TYPE.equals(request.getType())) {
			return createEditQueryComponentCommand(request);
		} else if ( SetMainQueryAction.REQUEST_TYPE.equals(request.getType())) {
			return createSetMainQueryCommand(request);
		} else if ( ConvertToTextAction.REQUEST_TYPE.equals(request.getType())) {
			return createConvertToTextCommand(request);
		}
		return super.getCommand(request);
	}
	
	@SuppressWarnings("unchecked")
	private Command createSetMainQueryCommand(Request request) {
		Map<String, Object> extendedData = request.getExtendedData();
		QueryElement queryElement = (QueryElement) extendedData.get(SetMainQueryAction.QUERY_COMPONENT_KEY);
		return new SetMainQueryCommand(queryElement, queryElement.getQuery() );
	}

	@SuppressWarnings("unchecked")
	private Command createConvertToTextCommand(Request request) {
		Map<String, Object> extendedData = request.getExtendedData();
		QuerySubpart querySubpart = (QuerySubpart) extendedData.get(SetMainQueryAction.QUERY_COMPONENT_KEY);
		ConvertToTextCommand command = new ConvertToTextCommand(querySubpart, querySubpart.getQuery() );
		if ( querySubpart.isMainQuery() ) {
			return command.chain( new SetMainQueryCommand(command.getFreeText(), querySubpart.getQuery() ) );
		}
		return command;
	}

	private Command createEditQueryComponentCommand(Request request) {
		QueryComponent queryComponent = getQueryComponent(request);
		if ( queryComponent instanceof Comparison ) {
			return new EditComparisonCommand(queryComponent);
		} else if ( queryComponent instanceof NullTest ) {
			return new EditNullTestCommand(queryComponent);
		} else if ( queryComponent instanceof WildcardTest ) {
			return new EditWildcardTestCommand(queryComponent);
		} else if ( queryComponent instanceof InFolderTest ) {
			return new EditInFolderTestCommand(queryComponent);
		} else if ( queryComponent instanceof ThisInFolderTest ) {
			return new EditThisInFolderTestCommand(queryComponent);
		} else if ( queryComponent instanceof ThisInTreeTest ) {
			return new EditThisInTreeTestCommand(queryComponent);
		} else if ( queryComponent instanceof InSubFolderTest ) {
			return new EditInSubFolderTestCommand(queryComponent);
		} else if ( queryComponent instanceof ClassTest ) {
			return new EditClassTestCommand(queryComponent);
		} else if ( queryComponent instanceof FreeText ) {
			return new EditFreeTextCommand(queryComponent);
		} else if ( queryComponent instanceof FullTextQuery ) {
			return new EditFullTextQueryCommand(queryComponent);
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	private QueryComponent getQueryComponent(Request request) {
		Map<String, Object> extendedData = request.getExtendedData();
		return (QueryComponent) extendedData
				.get(EditQueryComponentAction.QUERY_COMPONENT_KEY);
	}
	
	protected Command createDeleteCommand(GroupRequest request) {
		QueryDiagram parent = (QueryDiagram) getHost().getParent().getModel();
		QuerySubpart querySubpart = (QuerySubpart) getHost().getModel();
		
		DeleteCommand deleteCmd = new DeleteCommand();
		deleteCmd.setParent(parent);
		deleteCmd.setChild(querySubpart);

		return chainSetMainQueryCommand(parent, querySubpart, deleteCmd);
	}

	private Command chainSetMainQueryCommand(QueryDiagram parent, QuerySubpart querySubpart, Command command) {
		Query query = parent.getQuery();
		if ( parent.isRootDiagram() && querySubpart.equals( query.getMainQuery() ) ) {
			for (QueryElement child : parent.getChildren() )
			{
				if ( !child.equals(querySubpart) ) {
					return command.chain( new SetMainQueryCommand(child, query) );
				}
			}
			return command.chain( new SetMainQueryCommand(null, query) );
		}
		return command;
	}
}
