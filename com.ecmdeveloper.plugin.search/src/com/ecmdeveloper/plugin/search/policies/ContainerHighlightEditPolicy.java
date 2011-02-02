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

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.swt.graphics.Color;

/**
 * @author ricardo.belfor
 *
 */
public class ContainerHighlightEditPolicy extends GraphicalEditPolicy {

	public final static Color logicBackgroundBlue = new Color(null, 200, 200, 240);

	private Color revertColor;

	public void eraseTargetFeedback(Request request){
		if (revertColor != null){
			setContainerBackground(revertColor);
			revertColor = null;
		}
	}

	private Color getContainerBackground(){
		return getContainerFigure().getBackgroundColor();
	}

	private IFigure getContainerFigure(){
		return ((GraphicalEditPart)getHost()).getFigure();
	}

	public EditPart getTargetEditPart(Request request){
		return request.getType().equals(RequestConstants.REQ_SELECTION_HOVER) ?
				getHost() : null;
	}

	private void setContainerBackground(Color c){
		getContainerFigure().setBackgroundColor(c);
	}

	protected void showHighlight(){
		if (revertColor == null){
			revertColor = getContainerBackground();
			setContainerBackground(logicBackgroundBlue);
		}
	}	
}
