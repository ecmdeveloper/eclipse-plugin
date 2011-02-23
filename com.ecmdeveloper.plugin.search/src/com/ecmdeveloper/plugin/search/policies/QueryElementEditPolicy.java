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

import com.ecmdeveloper.plugin.search.actions.EditQueryComponentAction;
import com.ecmdeveloper.plugin.search.commands.DeleteCommand;
import com.ecmdeveloper.plugin.search.commands.EditComparisonCommand;
import com.ecmdeveloper.plugin.search.commands.EditInFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditInSubFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditNullTestCommand;
import com.ecmdeveloper.plugin.search.commands.EditWildcardTestCommand;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.QueryComponent;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;
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
		}
		return super.getCommand(request);
	}
	
	private Command createEditQueryComponentCommand(Request request) {
		QueryComponent queryComponent = getQueryComponent(request);
		if ( queryComponent instanceof Comparison ) {
			return new EditComparisonCommand(queryComponent);
		} if ( queryComponent instanceof NullTest ) {
			return new EditNullTestCommand(queryComponent);
		} if ( queryComponent instanceof WildcardTest ) {
			return new EditWildcardTestCommand(queryComponent);
		} if ( queryComponent instanceof InFolderTest ) {
			return new EditInFolderTestCommand(queryComponent);
		} if ( queryComponent instanceof InSubFolderTest ) {
			return new EditInSubFolderTestCommand(queryComponent);
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
		Object parent = getHost().getParent().getModel();
		DeleteCommand deleteCmd = new DeleteCommand();
		deleteCmd.setParent((QueryDiagram) parent);
		deleteCmd.setChild((QuerySubpart) getHost().getModel());
		return deleteCmd;
	}
}
