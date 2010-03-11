/**
 * Copyright 2010, Ricardo Belfor
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

package com.ecmdeveloper.plugin.diagrams.editors;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramNote;
import com.ecmdeveloper.plugin.diagrams.util.IconFiles;
import com.ecmdeveloper.plugin.diagrams.Activator;
/**
 * 
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramEditorPaletteFactory {

   public static PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createControlGroup(palette));
		palette.add( createComponentsDrawer() );
		return palette;
	}
	
   static private PaletteContainer createControlGroup(PaletteRoot root) {

		PaletteGroup controlGroup = new PaletteGroup("Controls");

		ToolEntry tool = new PanningSelectionToolEntry();
		controlGroup.add(tool);
		root.setDefaultEntry(tool);

		controlGroup.add(new MarqueeToolEntry());
		// tool = new ConnectionCreationToolEntry(
		// LogicMessages.LogicPlugin_Tool_ConnectionCreationTool_ConnectionCreationTool_Label,
		// LogicMessages.LogicPlugin_Tool_ConnectionCreationTool_ConnectionCreationTool_Description,
		// null,
		//			ImageDescriptor.createFromFile(Circuit.class, "icons/connection16.gif"),//$NON-NLS-1$
		//			ImageDescriptor.createFromFile(Circuit.class, "icons/connection24.gif")//$NON-NLS-1$
		// );
		// entries.add(tool);
		// controlGroup.addAll(entries);
		return controlGroup;
	}
   
   	static private PaletteContainer createComponentsDrawer() {
   		
   		PaletteDrawer componentsDrawer = new PaletteDrawer( "Diagram Elements" );
   		
   		ImageDescriptor noteImageSmall = Activator.getImageDescriptor( IconFiles.NOTE );
   		ImageDescriptor noteImageLarge = Activator.getImageDescriptor( IconFiles.NOTE_LARGE );
   		
   		CombinedTemplateCreationEntry noteCreationEntry = new CombinedTemplateCreationEntry(
				"Diagram Note", "Note", new SimpleFactory(ClassDiagramNote.class), noteImageSmall,
				noteImageLarge);
   		componentsDrawer.add( noteCreationEntry );
        return componentsDrawer;

   	}

}
