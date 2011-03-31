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

import javax.swing.text.StyleContext.SmallAttributeSet;

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
import org.eclipse.gef.tools.MarqueeSelectionTool;
import org.eclipse.jface.resource.ImageDescriptor;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.model.AndContainer;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.FreeText;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.NotContainer;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.OrContainer;
import com.ecmdeveloper.plugin.search.model.Query;
import com.ecmdeveloper.plugin.search.model.QueryContainer;
import com.ecmdeveloper.plugin.search.model.QueryElement;
import com.ecmdeveloper.plugin.search.model.WildcardTest;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class QueryPaletteFactory extends org.eclipse.ui.plugin.AbstractUIPlugin {

	private static QueryPaletteFactory singleton;

	@SuppressWarnings("unchecked")
	static private List createCategories(PaletteRoot root, Query query) {
		List categories = new ArrayList();

		//categories.add(createControlGroup(root));
		categories.add(createContainersDrawer(query));
		categories.add(createQueryPartsDrawer(query));

		return categories;
	}

	@SuppressWarnings("unchecked")
	static private PaletteContainer createQueryPartsDrawer(Query query) {
		
		PaletteDrawer drawer = new PaletteDrawer(
				"Query Parts", ImageDescriptor.createFromFile(QueryContainer.class, "icons/can.gif")); //$NON-NLS-1$

		List<Object> entries = new ArrayList();

		CombinedTemplateCreationEntry combined;
		
		combined = createEntry("Comparison", "Query Field Comparison", "halfadder", Comparison.class, query);
		entries.add(combined);

		combined = createEntry("Null Test", "Query Field Null Test", "ledicon", NullTest.class, query);
		entries.add(combined);

		combined = createEntry("Like Test", "Query Field Wildcard Test",
				QueryIcons.WILDCARD_TEST_ICON, QueryIcons.WILDCARD_TEST_ICON_LARGE,
				WildcardTest.class, query);
		entries.add(combined);

		combined = createEntry("In Folder Test", "Query Field In Folder Test", "connection", InFolderTest.class, query);
		entries.add(combined);

		combined = createEntry("In Subfolder Test", "Query Field In Subfolder Test", "arrow", InSubFolderTest.class, query);
		entries.add(combined);
		
		combined = createEntry("Free Text", "Free Text condition", "label", FreeText.class, query);
		entries.add(combined);

		drawer.addAll(entries);
		return drawer;
	}

	@SuppressWarnings("unchecked")
	static private PaletteContainer createContainersDrawer(Query query) {

		PaletteDrawer drawer = new PaletteDrawer("Containers", ImageDescriptor.createFromFile(
				QueryContainer.class, "icons/comp.gif"));//$NON-NLS-1$

		List entries = new ArrayList();

		CombinedTemplateCreationEntry combined;
		
		combined = createEntry("AND", "Logical AND container", "icons/and_container16.png", "icons/or_container24.png", AndContainer.class, query);
		entries.add(combined);

		combined = createEntry("OR", "Logical OR container", "icons/or_container16.png","icons/or_container16.png" , OrContainer.class, query);
		entries.add(combined);

		combined = createEntry("NOT", "Logical NOT container", "icons/not_container16.png", "icons/not_container24.png", NotContainer.class, query);
		entries.add(combined);
		
		drawer.addAll(entries);
		return drawer;
	}

	@Deprecated
	private static CombinedTemplateCreationEntry createEntry(String label, String description, String iconRoot, Class<? extends QueryElement> type, Query query ) {
		
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(label,
				description, new QueryCreationFactory( query, type),
						ImageDescriptor.createFromFile(type, "icons/" + iconRoot + "16.gif"),//$NON-NLS-1$
						ImageDescriptor.createFromFile(type, "icons/" + iconRoot + "24.gif")//$NON-NLS-1$
		);
		return combined;
	}

	private static CombinedTemplateCreationEntry createEntry(String label, String description, String normalIcon, String largeIcon, Class<? extends QueryElement> type, Query query ) {
		
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(label,
				description, new QueryCreationFactory( query, type), Activator.getImageDescriptor(normalIcon),
					Activator.getImageDescriptor(largeIcon)
		);
		return combined;
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

	static PaletteRoot createPalette(Query query) {
		PaletteRoot logicPalette = new PaletteRoot();
		logicPalette.addAll(createCategories(logicPalette, query));
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
