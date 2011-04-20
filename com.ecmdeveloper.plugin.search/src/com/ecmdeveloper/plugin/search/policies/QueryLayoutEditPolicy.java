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

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import com.ecmdeveloper.plugin.search.commands.AddCommand;
import com.ecmdeveloper.plugin.search.commands.CloneCommand;
import com.ecmdeveloper.plugin.search.commands.CreateCommand;
import com.ecmdeveloper.plugin.search.commands.SetConstraintCommand;
import com.ecmdeveloper.plugin.search.commands.SetMainQueryCommand;
import com.ecmdeveloper.plugin.search.figures.QueryColorConstants;
import com.ecmdeveloper.plugin.search.figures.QueryContainerFeedbackFigure;
import com.ecmdeveloper.plugin.search.model.IQueryField;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryContainer;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;

/**
 * 
 * @author ricardo.belfor
 * 
 */
public class QueryLayoutEditPolicy extends org.eclipse.gef.editpolicies.XYLayoutEditPolicy {

	private QueryCommandFactory queryCommandFactory;
	
	public QueryLayoutEditPolicy(XYLayout layout, QueryCommandFactory queryCommandFactory) {
		super();
		setXyLayout(layout);
		this.queryCommandFactory = queryCommandFactory;
	}

	protected Command createAddCommand(Request request, EditPart childEditPart, Object constraint) {
		QuerySubpart part = (QuerySubpart) childEditPart.getModel();
		Rectangle rect = (Rectangle) constraint;

		AddCommand add = new AddCommand();
		add.setParent((QueryDiagram) getHost().getModel());
		add.setChild(part);
		add.setLabel("Add");

		SetConstraintCommand setConstraint = new SetConstraintCommand();
		setConstraint.setLocation(rect);
		setConstraint.setPart(part);
		setConstraint.setLabel("Add");

		Command cmd = add.chain(setConstraint);
		return cmd;
	}

	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		return null;
	}

	protected Command createChangeConstraintCommand(ChangeBoundsRequest request, EditPart child,
			Object constraint) {
		SetConstraintCommand cmd = new SetConstraintCommand();
		QuerySubpart part = (QuerySubpart) child.getModel();
		cmd.setPart(part);
		cmd.setLocation((Rectangle) constraint);
		Command result = cmd;


		return result;
	}

	protected EditPolicy createChildEditPolicy(EditPart child) {
		// if (child instanceof LEDEditPart
		// || child instanceof OutputEditPart) {
		// ResizableEditPolicy policy = new LogicResizableEditPolicy();
		// policy.setResizeDirections(0);
		// return policy;
		// } else if (child instanceof LogicLabelEditPart) {
		// ResizableEditPolicy policy = new LogicResizableEditPolicy();
		// policy.setResizeDirections(PositionConstants.EAST |
		// PositionConstants.WEST);
		// return policy;
		// }

		return new QueryResizableEditPolicy();
	}

	protected IFigure createSizeOnDropFeedback(CreateRequest createRequest) {
		IFigure figure;

		if (createRequest.getNewObject() instanceof QueryContainer)
			figure = new QueryContainerFeedbackFigure();
		else {
			figure = new RectangleFigure();
			((RectangleFigure) figure).setXOR(true);
			((RectangleFigure) figure).setFill(true);
			figure.setBackgroundColor(QueryColorConstants.ghostFillColor);
			figure.setForegroundColor(ColorConstants.white);
		}

		addFeedback(figure);

		return figure;
	}

	@SuppressWarnings("unchecked")
	protected Command getAddCommand(Request generic) {
		ChangeBoundsRequest request = (ChangeBoundsRequest) generic;
		List editParts = request.getEditParts();
		CompoundCommand command = new CompoundCommand();
		GraphicalEditPart childPart;
		Rectangle r;
		Object constraint;

		for (int i = 0; i < editParts.size(); i++) {
			childPart = (GraphicalEditPart) editParts.get(i);
			r = childPart.getFigure().getBounds().getCopy();
			// convert r to absolute from childpart figure
			childPart.getFigure().translateToAbsolute(r);
			r = request.getTransformedRectangle(r);
			// convert this figure to relative
			getLayoutContainer().translateToRelative(r);
			getLayoutContainer().translateFromParent(r);
			r.translate(getLayoutOrigin().getNegated());
			constraint = getConstraintFor(r);
			command
					.add(createAddCommand(generic, childPart,
							translateToModelConstraint(constraint)));
		}
		return command.unwrap();
	}

	/**
	 * Override to return the <code>Command</code> to perform an
	 * {@link RequestConstants#REQ_CLONE CLONE}. By default, <code>null</code>
	 * is returned.
	 * 
	 * @param request
	 *            the Clone Request
	 * @return A command to perform the Clone.
	 */
	protected Command getCloneCommand(ChangeBoundsRequest request) {
		CloneCommand clone = new CloneCommand();

		clone.setParent((QueryDiagram) getHost().getModel());

		Iterator i = request.getEditParts().iterator();
		GraphicalEditPart currPart = null;

		while (i.hasNext()) {
			currPart = (GraphicalEditPart) i.next();
			clone.addPart((QuerySubpart) currPart.getModel(), (Rectangle) getConstraintForClone(
					currPart, request));
		}

		return clone;
	}

	protected Command getCreateCommand(CreateRequest request) {

		CreateCommand createCommand;
		QueryDiagram parent = (QueryDiagram) getHost().getModel();
		Object newObject = request.getNewObject();
		QuerySubpart newPart;
		if ( newObject instanceof IQueryField ) {
			createCommand = queryCommandFactory.getCreateCommand((IQueryField) newObject, parent.getQuery() );
			if ( createCommand == null ) {
				return null;
			}
			newPart = createCommand.getChild();
		} else {
			createCommand = queryCommandFactory.getCreateCommand(request);
			newPart = (QuerySubpart) newObject;
			createCommand.setChild(newPart);
		}
		createCommand.setParent(parent);

		Rectangle constraint = (Rectangle) getConstraintFor(request);
		createCommand.setLocation(constraint);
		createCommand.setLabel("Create");
		
		if (parent.isRootDiagram() ) {
			Query query = parent.getQuery();
			if ( query.getMainQuery() == null ) {
				return createCommand.chain( new SetMainQueryCommand(newPart, query) );
			}
		}		
		return createCommand;
	}


	protected Insets getCreationFeedbackOffset(CreateRequest request) {
		return new Insets();
	}

	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}
}
