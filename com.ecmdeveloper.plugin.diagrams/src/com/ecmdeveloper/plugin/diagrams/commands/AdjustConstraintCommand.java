package com.ecmdeveloper.plugin.diagrams.commands;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;

import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramElement;
import com.ecmdeveloper.plugin.diagrams.parts.ClassDiagramEditPart;

public class AdjustConstraintCommand extends Command
{
   private ClassDiagramEditPart manager;
   private ClassDiagramElement model;
   private Rectangle newBounds, oldBounds;
   
   public AdjustConstraintCommand(GraphicalEditPart editPart, Rectangle contraint) {
      this.manager = (ClassDiagramEditPart) editPart.getParent();
      this.model = (ClassDiagramElement) editPart.getModel();
      this.newBounds = contraint;
      System.out.println( contraint.toString() );
      this.oldBounds = new Rectangle(editPart.getFigure().getBounds());
      setLabel(getOp(oldBounds, newBounds) + " " + getName(editPart));
//      
//      if ( this.model instanceof ClassDiagramClass ) {
//    	  if ( ! newBounds.getSize().equals( oldBounds.getSize() ) ) {
//    		  ((ClassDiagramClass)model).setSize( newBounds.getSize() );
//    	  }
//    	  if ( ! newBounds.getLocation().equals( oldBounds.getLocation() ) ) {
//    		  ((ClassDiagramClass)model).setLocation( newBounds.getLocation() );
//    	  }
//      }
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
      return "Favorites Element";
   }

   public void execute() {
      redo();
   }

   public void redo() {
      GraphicalEditPart editPart = getEditPart();
      if (editPart == null)
         return;
	  	model.setSize(newBounds.getSize());
		model.setLocation(newBounds.getLocation());
      //manager.setLayoutConstraint(editPart, editPart.getFigure(), newBounds);
   }

   public void undo() {
      GraphicalEditPart editPart = getEditPart();
      if (editPart == null)
         return;
//      manager.setLayoutConstraint(editPart, editPart.getFigure(), oldBounds);
		model.setSize(oldBounds.getSize());
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