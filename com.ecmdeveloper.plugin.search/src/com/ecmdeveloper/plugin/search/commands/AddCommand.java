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

/**
 * 
 * @author ricardo.belfor
 *
 */
public class AddCommand extends Command {

	private QuerySubpart child;
	private QueryDiagram parent;
	private int index = -1;

	public AddCommand() {
		super("Add");
	}

	public void execute() {
		if (index < 0)
			parent.addChild(child);
		else
			parent.addChild(child, index);
	}

	public QueryDiagram getParent() {
		return parent;
	}

	public void redo() {
		if (index < 0)
			parent.addChild(child);
		else
			parent.addChild(child, index);
	}

	public void setChild(QuerySubpart subpart) {
		child = subpart;
	}

	public void setIndex(int i) {
		index = i;
	}

	public void setParent(QueryDiagram newParent) {
		parent = newParent;
	}

	public void undo() {
		parent.removeChild(child);
	}
}
