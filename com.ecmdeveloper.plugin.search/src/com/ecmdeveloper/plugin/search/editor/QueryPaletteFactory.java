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

package com.ecmdeveloper.plugin.search.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.tools.MarqueeSelectionTool;
import org.eclipse.jface.resource.ImageDescriptor;

import com.ecmdeveloper.plugin.search.model.AndContainer;
import com.ecmdeveloper.plugin.search.model.OrContainer;
import com.ecmdeveloper.plugin.search.model.QueryContainer;
import com.ecmdeveloper.plugin.search.model.Comparison;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class QueryPaletteFactory extends org.eclipse.ui.plugin.AbstractUIPlugin {

	private static QueryPaletteFactory singleton;

	@SuppressWarnings("unchecked")
	static private List createCategories(PaletteRoot root) {
		List categories = new ArrayList();

		categories.add(createControlGroup(root));
		categories.add(createContainersDrawer());
		categories.add(createQueryPartsDrawer());

		return categories;
	}

	@SuppressWarnings("unchecked")
	static private PaletteContainer createQueryPartsDrawer() {
		
		PaletteDrawer drawer = new PaletteDrawer(
				"Query Parts", ImageDescriptor.createFromFile(QueryContainer.class, "icons/can.gif")); //$NON-NLS-1$

		List<Object> entries = new ArrayList();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry("Comparison", "Query Field Comparison",
				new SimpleFactory(Comparison.class), 
				ImageDescriptor.createFromFile(Comparison.class, "icons/halfadder16.gif"), //$NON-NLS-1$
				ImageDescriptor.createFromFile(Comparison.class, "icons/halfadder24.gif")//$NON-NLS-1$
		);
		entries.add(combined);

		//
		// CombinedTemplateCreationEntry combined = new
		// CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_HalfAdder_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_HalfAdder_Description,
		// LogicDiagramFactory.getHalfAdderFactory(),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/halfadder16.gif"), //$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/halfadder24.gif") //$NON-NLS-1$
		// );
		// entries.add(combined);
		//
		// combined = new CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_FullAdder_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_FullAdder_Description,
		// LogicDiagramFactory.getFullAdderFactory(),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/fulladder16.gif"), //$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/fulladder24.gif") //$NON-NLS-1$
		// );
		// entries.add(combined);

		drawer.addAll(entries);
		return drawer;
	}

	@SuppressWarnings("unchecked")
	static private PaletteContainer createContainersDrawer() {

		PaletteDrawer drawer = new PaletteDrawer("Containers", ImageDescriptor.createFromFile(
				QueryContainer.class, "icons/comp.gif"));//$NON-NLS-1$

		List entries = new ArrayList();

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry("AND",
				"Logical AND container", new SimpleFactory(AndContainer.class),
						ImageDescriptor.createFromFile(AndContainer.class, "icons/and16.gif"),//$NON-NLS-1$
						ImageDescriptor.createFromFile(AndContainer.class, "icons/and24.gif")//$NON-NLS-1$
		);
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry("OR",
				"Logical OR container", new SimpleFactory(OrContainer.class),
						ImageDescriptor.createFromFile(OrContainer.class, "icons/or16.gif"),//$NON-NLS-1$
						ImageDescriptor.createFromFile(OrContainer.class, "icons/or24.gif")//$NON-NLS-1$
		);
		entries.add(combined);
		
		// combined = new CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_Circuit_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_Circuit_Description,
		// new SimpleFactory(Circuit.class),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/circuit16.gif"),//$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/circuit24.gif")//$NON-NLS-1$
		// );
		// entries.add(combined);
		//	
		// entries.add(new PaletteSeparator());
		//
		//
		// combined = new CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_LED_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_LED_Description,
		// new SimpleFactory(LED.class),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/ledicon16.gif"), //$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/ledicon24.gif")//$NON-NLS-1$
		// );
		// entries.add(combined);
		//
		// combined = new CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_ORGate_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_ORGate_Description,
		// new SimpleFactory(OrGate.class),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/or16.gif"),//$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/or24.gif")//$NON-NLS-1$
		// );
		// entries.add(combined);
		//
		// combined = new CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_XORGate_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_XORGate_Description,
		// new SimpleFactory(XORGate.class),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/xor16.gif"),//$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/xor24.gif")//$NON-NLS-1$
		// );
		// entries.add(combined);
		//
		// combined = new CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_ANDGate_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_ANDGate_Description,
		// new SimpleFactory(AndGate.class),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/and16.gif"),//$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/and24.gif")//$NON-NLS-1$
		// );
		// entries.add(combined);
		//
		// PaletteStack liveGroundStack = new PaletteStack(
		// LogicMessages.LogicPlugin_Tool_CreationTool_LiveGroundStack_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_LiveGroundStack_Description,
		// null);
		//	
		// combined = new CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_LiveOutput_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_LiveOutput_Description,
		// new SimpleFactory(LiveOutput.class),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/live16.gif"),//$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/live24.gif")//$NON-NLS-1$
		// );
		// liveGroundStack.add(combined);
		//
		// combined = new CombinedTemplateCreationEntry(
		// LogicMessages.LogicPlugin_Tool_CreationTool_Ground_Label,
		// LogicMessages.LogicPlugin_Tool_CreationTool_Ground_Description,
		// new SimpleFactory(GroundOutput.class),
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/ground16.gif"),//$NON-NLS-1$
		//		ImageDescriptor.createFromFile(Circuit.class, "icons/ground24.gif")//$NON-NLS-1$
		// );
		// liveGroundStack.add(combined);
		//
		// entries.add(liveGroundStack);
		//	
		drawer.addAll(entries);
		return drawer;
	}

	@SuppressWarnings("unchecked")
	static private PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("Control");

		List entries = new ArrayList();

		ToolEntry tool = new PanningSelectionToolEntry();
		entries.add(tool);
		root.setDefaultEntry(tool);

		PaletteStack marqueeStack = new PaletteStack("Marquee_Stack", "", null); //$NON-NLS-1$
		marqueeStack.add(new MarqueeToolEntry());
		MarqueeToolEntry marquee = new MarqueeToolEntry();
		marquee.setToolProperty(MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR, new Integer(
				MarqueeSelectionTool.BEHAVIOR_CONNECTIONS_TOUCHED));
		marqueeStack.add(marquee);
		marquee = new MarqueeToolEntry();
		marquee.setToolProperty(MarqueeSelectionTool.PROPERTY_MARQUEE_BEHAVIOR, new Integer(
				MarqueeSelectionTool.BEHAVIOR_CONNECTIONS_TOUCHED
						| MarqueeSelectionTool.BEHAVIOR_NODES_CONTAINED));
		marqueeStack.add(marquee);
		marqueeStack.setUserModificationPermission(PaletteEntry.PERMISSION_NO_MODIFICATION);
		entries.add(marqueeStack);

		controlGroup.addAll(entries);
		return controlGroup;
	}

	static PaletteRoot createPalette() {
		PaletteRoot logicPalette = new PaletteRoot();
		logicPalette.addAll(createCategories(logicPalette));
		return logicPalette;
	}

	public static QueryPaletteFactory getDefault() {
		return singleton;
	}

	public QueryPaletteFactory() {
		if (singleton == null) {
			singleton = this;
		}
	}
}
