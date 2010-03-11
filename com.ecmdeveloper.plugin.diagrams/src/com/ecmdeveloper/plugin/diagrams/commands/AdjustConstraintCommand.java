/**
 * Copyright 2009, Ricardo Belfor
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
package com.ecmdeveloper.plugin.diagrams.commands;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElement;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElementWithResize;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramNote;
import com.ecmdeveloper.plugin.diagrams.parts.ClassDiagramEditPart;

/**
 * 
 * @author Ricardo Belfor
 *
 */
public class AdjustConstraintCommand extends Command
{
   private ClassDiagramEditPart manager;
   private ClassDiagramElement model;
   private Rectangle newBounds, oldBounds;
   
   public AdjustConstraintCommand(GraphicalEditPart editPart, Rectangle contraint) {
      this.manager = (ClassDiagramEditPart) editPart.getParent();
      this.model = (ClassDiagramElement) editPart.getModel();
      this.newBounds = contraint;
      this.oldBounds = new Rectangle(editPart.getFigure().getBounds());
      setLabel(getOp(oldBounds, newBounds) + " " + getName(editPart));
   }

   private String getOp(Rectangle oldBounds, Rectangle newBounds) {
      if (oldBounds.getSize().equals(newBounds.getSize()))
         return "Move";
      return "Resize ";
   }

   private static String getName(EditPart editPart) {
      Object model = editPart.getModel();
      if (model instanceof ClassDiagramClass)
         return ((ClassDiagramClass) model).getName();
      
      if ( model instanceof ClassDiagramNote)
    	  return "Note";
      
      return "Unknown Item";
   }

   public void execute() {
	   redo();
   }

   public void redo() {
		GraphicalEditPart editPart = getEditPart();
		if (editPart == null)
			return;

		if (model instanceof ClassDiagramElementWithResize) {
			((ClassDiagramElementWithResize) model).setSize(newBounds.getSize());
		}
		model.setLocation(newBounds.getLocation());
	}

   public void undo() {
      GraphicalEditPart editPart = getEditPart();
      if (editPart == null)
         return;
		if (model instanceof ClassDiagramElementWithResize) {
			((ClassDiagramElementWithResize) model).setSize(newBounds.getSize());
		}
		model.setLocation(oldBounds.getLocation());

   }

   private GraphicalEditPart getEditPart() {
      for (Iterator<?> iter = manager.getChildren().iterator(); iter.hasNext();) {
         GraphicalEditPart editPart = (GraphicalEditPart) iter.next();
         if (model.equals(editPart.getModel()))
            return editPart;
      }
      return null;
   }
}