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

package com.ecmdeveloper.plugin.search.commands;

import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryElement;

/**
 * @author ricardo.belfor
 *
 */
public class SetMainQueryCommand extends Command {

	private final QueryElement mainQuery;
	private QueryElement previousMainQuery;
	private final Query query;

	public SetMainQueryCommand(QueryElement mainQuery, Query query) {
		super("Set Main Query");
		this.mainQuery = mainQuery;
		this.query = query;
		this.previousMainQuery = query.getMainQuery();
	}

	@Override
	public void execute() {
		redo();
	}

	@Override
	public void redo() {
		query.setMainQuery(mainQuery);
	}

	@Override
	public void undo() {
		query.setMainQuery(previousMainQuery);
	}
}
