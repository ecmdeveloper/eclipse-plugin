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

package com.ecmdeveloper.plugin.search.commands;

import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;

public class DeleteCommand extends Command {

	private QuerySubpart child;
	private QueryDiagram parent;
	private int index = -1;

	public DeleteCommand() {
		super("Delete");
	}

	public void execute() {
		primExecute();
	}

	protected void primExecute() {
		index = parent.getChildren().indexOf(child);
		parent.removeChild(child);
	}

	public void redo() {
		primExecute();
	}

	public void setChild(QuerySubpart c) {
		child = c;
	}

	public void setParent(QueryDiagram p) {
		parent = p;
	}

	public void undo() {
		parent.addChild(child, index);
	}
}
