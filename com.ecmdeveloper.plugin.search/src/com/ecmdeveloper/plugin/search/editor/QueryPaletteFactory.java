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
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;

import com.ecmdeveloper.plugin.search.Activator;
import com.ecmdeveloper.plugin.search.model.AndContainer;
import com.ecmdeveloper.plugin.search.model.ClassTest;
import com.ecmdeveloper.plugin.search.model.Comparison;
import com.ecmdeveloper.plugin.search.model.FreeText;
import com.ecmdeveloper.plugin.search.model.InFolderTest;
import com.ecmdeveloper.plugin.search.model.InSubFolderTest;
import com.ecmdeveloper.plugin.search.model.NotContainer;
import com.ecmdeveloper.plugin.search.model.NullTest;
import com.ecmdeveloper.plugin.search.model.OrContainer;
import com.ecmdeveloper.plugin.search.model.QueryElement;
import com.ecmdeveloper.plugin.search.model.QueryElementDescription;
import com.ecmdeveloper.plugin.search.model.WildcardTest;

/**
 * 
 * @author ricardo.belfor
 *
 */
public class QueryPaletteFactory extends org.eclipse.ui.plugin.AbstractUIPlugin {

	private static QueryPaletteFactory singleton;

	@SuppressWarnings("unchecked")
	static private List createCategories(PaletteRoot root, QueryProxy queryProxy) {

		List categories = new ArrayList();
		categories.add(createContainersDrawer(queryProxy));
		categories.add(createQueryPartsDrawer(queryProxy));

		return categories;
	}

	@SuppressWarnings("unchecked")
	static private PaletteContainer createContainersDrawer(QueryProxy queryProxy) {
	
		PaletteDrawer drawer = new PaletteDrawer(
				"Containers", Activator.getImageDescriptor(QueryIcons.CONTAINER));
	
		List entries = new ArrayList();
	
		CombinedTemplateCreationEntry combined;
		
		combined = createEntry("AND", "Logical AND container", QueryIcons.AND_CONTAINER,
				QueryIcons.AND_CONTAINER_LARGE, AndContainer.class, queryProxy);
		entries.add(combined);
	
		combined = createEntry("OR", "Logical OR container", QueryIcons.OR_CONTAINER,
				QueryIcons.OR_CONTAINER_LARGE, OrContainer.class, queryProxy);
		entries.add(combined);
	
		combined = createEntry("NOT", "Logical NOT container", QueryIcons.NOT_CONTAINER,
				QueryIcons.NOT_CONTAINER_LARGE, NotContainer.class, queryProxy);
		entries.add(combined);
		
		drawer.addAll(entries);
		return drawer;
	}

	@SuppressWarnings("unchecked")
	static private PaletteContainer createQueryPartsDrawer(QueryProxy queryProxy) {
		
		PaletteDrawer drawer = new PaletteDrawer(
				"Query Components", Activator.getImageDescriptor( QueryIcons.QUERY_COMPONENT_ICON) ); //$NON-NLS-1$

		List<Object> entries = new ArrayList();

		CombinedTemplateCreationEntry combined;
		
		combined = createEntry(Comparison.class, Comparison.DESCRIPTION, queryProxy);
		entries.add(combined);

		combined = createEntry(NullTest.class, NullTest.DESCRIPTION, queryProxy);
		entries.add(combined);

		combined = createEntry(WildcardTest.class, WildcardTest.DESCRIPTION, queryProxy);
		entries.add(combined);

		combined = createEntry( InFolderTest.class, InFolderTest.DESCRIPTION, queryProxy);
		entries.add(combined);

		combined = createEntry(InSubFolderTest.class, InSubFolderTest.DESCRIPTION, queryProxy);
		entries.add(combined);
		
		combined = createEntry(InSubFolderTest.class, ClassTest.DESCRIPTION, queryProxy);
		entries.add(combined);

		combined = createEntry(FreeText.class, FreeText.DESCRIPTION, queryProxy);
		entries.add(combined);

		drawer.addAll(entries);
		return drawer;
	}

	private static CombinedTemplateCreationEntry createEntry(String label, String description, String normalIcon, String largeIcon, Class<? extends QueryElement> type, QueryProxy queryProxy ) {
		
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(label,
				description, new QueryCreationFactory( queryProxy, type), Activator.getImageDescriptor(normalIcon),
					Activator.getImageDescriptor(largeIcon)
		);
		return combined;
	}

	private static CombinedTemplateCreationEntry createEntry(Class<? extends QueryElement> type,
			QueryElementDescription description, QueryProxy queryProxy) {

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(description
				.getLabel(), description.getDescription(), new QueryCreationFactory(queryProxy, type),
				description.getIcon(), description.getLargeIcon());

		return combined;
	}
	
	static PaletteRoot createPalette(QueryProxy queryProxy) {
		PaletteRoot logicPalette = new PaletteRoot();
		logicPalette.addAll(createCategories(logicPalette, queryProxy));
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
