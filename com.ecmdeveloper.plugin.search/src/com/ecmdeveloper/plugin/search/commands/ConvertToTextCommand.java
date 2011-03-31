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

import com.ecmdeveloper.plugin.search.model.FreeText;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;

/**
 * @author ricardo.belfor
 *
 */
public class ConvertToTextCommand extends Command {

	private final QuerySubpart querySubpart;
	private final Query query;
	private FreeText freeText;
	private int index = -1;

	public ConvertToTextCommand(QuerySubpart querySubpart, Query query) {
		super("Convert to Text");
		this.querySubpart = querySubpart;
		this.query = query;
		freeText = createFreeText();
	}

	public FreeText getFreeText() {
		return freeText;
	}

	@Override
	public void execute() {
		redo();
	}

	private FreeText createFreeText() {
		FreeText freeText = new FreeText(query);
		freeText.setText( querySubpart.toSQL() );
		freeText.setLocation( querySubpart.getLocation() );
		freeText.setSize( querySubpart.getSize() );
		return freeText;
	}

	@Override
	public void redo() {
		QueryDiagram parent = (QueryDiagram) querySubpart.getParent();
		index = parent.getChildren().indexOf(querySubpart);
		parent.removeChild(querySubpart);
		parent.addChild(freeText, index);
	}

	@Override
	public void undo() {
		QueryDiagram parent = (QueryDiagram) freeText.getParent();
		parent.removeChild(freeText);
		parent.addChild(querySubpart, index);
	}
}
