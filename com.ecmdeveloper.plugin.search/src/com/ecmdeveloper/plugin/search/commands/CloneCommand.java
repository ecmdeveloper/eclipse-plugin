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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.search.model.AndContainer;
import com.ecmdeveloper.plugin.search.model.OrContainer;
import com.ecmdeveloper.plugin.search.model.QueryDiagram;
import com.ecmdeveloper.plugin.search.model.QueryContainer;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.QuerySubpart;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class CloneCommand extends Command
{

	private List parts, newTopLevelParts, newConnections;
	private QueryDiagram parent;
	private Map bounds, indices, connectionPartMap;
//	private ChangeGuideCommand vGuideCommand, hGuideCommand;
//	private LogicGuide hGuide, vGuide;
	private int hAlignment, vAlignment;

	public CloneCommand() {
		super("Clone");
		parts = new LinkedList();
	}

	public void addPart(QuerySubpart part, Rectangle newBounds) {
		parts.add(part);
		if (bounds == null) {
			bounds = new HashMap();
		}
		bounds.put(part, newBounds);
	}

	public void addPart(QuerySubpart part, int index) {
		parts.add(part);
		if (indices == null) {
			indices = new HashMap();
		}
		indices.put(part, new Integer(index));
	}

	protected void clonePart(QuerySubpart oldPart, QueryDiagram newParent, Rectangle newBounds,
			List newConnections, Map connectionPartMap, int index) {
		QuerySubpart newPart = null;

//		if (oldPart instanceof AndGate) {
//			newPart = new AndGate();
//		} else if (oldPart instanceof Circuit) {
//			newPart = new Circuit();
//		} else if (oldPart instanceof GroundOutput) {
//			newPart = new GroundOutput();
//		} else if (oldPart instanceof LED) {
//			newPart = new LED();
//			newPart.setPropertyValue(LED.P_VALUE, oldPart.getPropertyValue(LED.P_VALUE));
//		} else if (oldPart instanceof LiveOutput) {
//			newPart = new LiveOutput();
//		} else 
		if (oldPart instanceof Comparison) {
			newPart = new Comparison();
			//((QueryCondition)newPart).setLabelContents(((QueryCondition)oldPart).getLabelContents());
//		} else if (oldPart instanceof OrGate) {
//			newPart = new OrGate();
		} else 
		if (oldPart instanceof OrContainer) {
			newPart = new OrContainer();
		} else 
		if (oldPart instanceof AndContainer) {
			newPart = new AndContainer();
		} 
//		else if (oldPart instanceof XORGate) {
//			newPart = new XORGate();
//		}

		if (oldPart instanceof QueryDiagram) {
			Iterator i = ((QueryDiagram)oldPart).getChildren().iterator();
			while (i.hasNext()) {
				// for children they will not need new bounds
				clonePart((QuerySubpart)i.next(), (QueryDiagram)newPart, null, 
						newConnections, connectionPartMap, -1);
			}
		}

//		Iterator i = oldPart.getTargetConnections().iterator();
//		while (i.hasNext()) {
//			Wire connection = (Wire)i.next();
//			Wire newConnection = new Wire();
//			newConnection.setValue(connection.getValue());
//			newConnection.setTarget(newPart);
//			newConnection.setTargetTerminal(connection.getTargetTerminal());
//			newConnection.setSourceTerminal(connection.getSourceTerminal());
//			newConnection.setSource(connection.getSource());
//
//			Iterator b = connection.getBendpoints().iterator();
//			Vector newBendPoints = new Vector();
//
//			while (b.hasNext()) {
//				WireBendpoint bendPoint = (WireBendpoint)b.next();
//				WireBendpoint newBendPoint = new WireBendpoint();
//				newBendPoint.setRelativeDimensions(bendPoint.getFirstRelativeDimension(), 
//						bendPoint.getSecondRelativeDimension());
//				newBendPoint.setWeight(bendPoint.getWeight());
//				newBendPoints.add(newBendPoint);
//			}
//
//			newConnection.setBendpoints(newBendPoints);
//			newConnections.add(newConnection);
//		}


		if (index < 0) {
			newParent.addChild(newPart);
		} else {
			newParent.addChild(newPart, index);
		}

		newPart.setSize(oldPart.getSize());


		if (newBounds != null) {
			newPart.setLocation(newBounds.getTopLeft());
		} else {
			newPart.setLocation(oldPart.getLocation());
		}

		// keep track of the new parts so we can delete them in undo
		// keep track of the oldpart -> newpart map so that we can properly attach
		// all connections.
		if (newParent == parent)
			newTopLevelParts.add(newPart);
		connectionPartMap.put(oldPart, newPart);
	}

	public void execute() {
		connectionPartMap = new HashMap();
		newConnections = new LinkedList();
		newTopLevelParts = new LinkedList();

		Iterator i = parts.iterator();

		QuerySubpart part = null;
		while (i.hasNext()) {
			part = (QuerySubpart)i.next();
			if (bounds != null && bounds.containsKey(part)) {
				clonePart(part, parent, (Rectangle)bounds.get(part), 
						newConnections, connectionPartMap, -1);	
			} else if (indices != null && indices.containsKey(part)) {
				clonePart(part, parent, null, newConnections, 
						connectionPartMap, ((Integer)indices.get(part)).intValue());
			} else {
				clonePart(part, parent, null, newConnections, connectionPartMap, -1);
			}
		}

//		// go through and set the source of each connection to the proper source.
//		Iterator c = newConnections.iterator();
//
//		while (c.hasNext()) {
//			Wire conn = (Wire)c.next();
//			LogicSubpart source = conn.getSource();
//			if (connectionPartMap.containsKey(source)) {
//				conn.setSource((LogicSubpart)connectionPartMap.get(source));
//				conn.attachSource();
//				conn.attachTarget();
//			}
//		}
//
//		if (hGuide != null) {
//			hGuideCommand = new ChangeGuideCommand(
//					(LogicSubpart)connectionPartMap.get(parts.get(0)), true);
//			hGuideCommand.setNewGuide(hGuide, hAlignment);
//			hGuideCommand.execute();
//		}
//
//		if (vGuide != null) {
//			vGuideCommand = new ChangeGuideCommand(
//					(LogicSubpart)connectionPartMap.get(parts.get(0)), false);
//			vGuideCommand.setNewGuide(vGuide, vAlignment);
//			vGuideCommand.execute();
//		}
	}

	public void setParent(QueryDiagram parent) {
		this.parent = parent;
	}

	public void redo() {
		for (Iterator iter = newTopLevelParts.iterator(); iter.hasNext();)
			parent.addChild((QuerySubpart)iter.next());
//		for (Iterator iter = newConnections.iterator(); iter.hasNext();) {
//			Wire conn = (Wire) iter.next();
//			LogicSubpart source = conn.getSource();
//			if (connectionPartMap.containsKey(source)) {
//				conn.setSource((LogicSubpart)connectionPartMap.get(source));
//				conn.attachSource();
//				conn.attachTarget();
//			}
//		}
//		if (hGuideCommand != null)
//			hGuideCommand.redo();
//		if (vGuideCommand != null)
//			vGuideCommand.redo();
	}

//	public void setGuide(LogicGuide guide, int alignment, boolean isHorizontal) {
//		if (isHorizontal) {
//			hGuide = guide;
//			hAlignment = alignment;
//		} else {
//			vGuide = guide;
//			vAlignment = alignment;
//		}
//	}

	public void undo() {
//		if (hGuideCommand != null)
//			hGuideCommand.undo();
//		if (vGuideCommand != null)
//			vGuideCommand.undo();
		for (Iterator iter = newTopLevelParts.iterator(); iter.hasNext();)
			parent.removeChild((QuerySubpart)iter.next());
	}
}
