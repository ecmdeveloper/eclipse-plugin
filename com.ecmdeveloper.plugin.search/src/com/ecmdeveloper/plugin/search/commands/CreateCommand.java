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

import java.util.ArrayList;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QueryField;
import com.ecmdeveloper.plugin.search.model.QueryFieldType;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class CreateCommand extends Command {

	protected QuerySubpart child;
	private Rectangle rect;
	private QueryDiagram parent;
	private int index = -1;

	public CreateCommand() {
		super("Create");
	}

	public boolean canExecute() {
		return child != null && parent != null;
	}

	public void execute() {
		if (rect != null) {
			Insets expansion = getInsets();
			if (!rect.isEmpty())
				rect.expand(expansion);
			else {
				rect.x -= expansion.left;
				rect.y -= expansion.top;
			}
			child.setLocation(rect.getLocation());
			if (!rect.isEmpty())
				child.setSize(rect.getSize());
		}
		redo();
	}

	private Insets getInsets() {
		return new Insets();
	}

	public void redo() {
		parent.addChild(child, index);
	}

	public void setChild(QuerySubpart subpart) {
		child = subpart;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setLocation(Rectangle r) {
		rect = r;
	}

	public void setParent(QueryDiagram newParent) {
		parent = newParent;
	}

	public void undo() {
		parent.removeChild(child);
	}
}
