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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.search.model.QuerySubpart;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class SetConstraintCommand extends Command {

	private Point newPos;
	private Dimension newSize;
	private Point oldPos;
	private Dimension oldSize;
	private QuerySubpart part;

	public void execute() {
		oldSize = part.getSize();
		oldPos = part.getLocation();
		redo();
	}

	public String getLabel() {
		if (oldSize.equals(newSize))
			return "Set Location";
		return "Resize";
	}

	public void redo() {
		part.setSize(newSize);
		part.setLocation(newPos);
	}

	public void setLocation(Rectangle r) {
		setLocation(r.getLocation());
		setSize(r.getSize());
	}

	public void setLocation(Point p) {
		newPos = p;
	}

	public void setPart(QuerySubpart part) {
		this.part = part;
	}

	public void setSize(Dimension p) {
		newSize = p;
	}

	public void undo() {
		part.setSize(oldSize);
		part.setLocation(oldPos);
	}
}
