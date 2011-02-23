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

import org.eclipse.gef.requests.CreateRequest;

import com.ecmdeveloper.plugin.search.commands.CreateCommand;
import com.ecmdeveloper.plugin.search.commands.CreateComparisonCommand;
import com.ecmdeveloper.plugin.search.commands.CreateInFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateInSubFolderTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateNullTestCommand;
import com.ecmdeveloper.plugin.search.commands.CreateWildcardTestCommand;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.WildcardTest;

/**
 * @author ricardo.belfor
 *
 */
public class QueryCommandFactory {

	public CreateCommand getCreateCommand(CreateRequest request) {
		
		CreateCommand command;
		if ( request.getNewObjectType() == Comparison.class ) {
			command = new CreateComparisonCommand();
		} else if ( request.getNewObjectType() == NullTest.class ) {
			command = new CreateNullTestCommand();
		} else if ( request.getNewObjectType() == WildcardTest.class ) {
			command = new CreateWildcardTestCommand();
		} else if ( request.getNewObjectType() == InFolderTest.class ) {
			command = new CreateInFolderTestCommand();
		} else if ( request.getNewObjectType() == InSubFolderTest.class ) {
			command = new CreateInSubFolderTestCommand();
		} else {			
			command = new CreateCommand();
		}
		
		return command;
	}
}
