package com.ecmdeveloper.plugin.diagrams.policies;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import com.ecmdeveloper.plugin.diagrams.commands.AdjustConstraintCommand;
import com.ecmdeveloper.plugin.diagrams.commands.ClassDiagramNoteCreateCommand;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramNote;
import com.ecmdeveloper.plugin.diagrams.parts.AbstractClassesGraphicalEditPart;

public class ClassDiagramLayoutEditPolicy extends XYLayoutEditPolicy
{
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		   
	      if (child instanceof AbstractClassesGraphicalEditPart) {
	         if (constraint instanceof Rectangle) {
	            return new AdjustConstraintCommand((AbstractClassesGraphicalEditPart) child,
	                  (Rectangle) constraint);
	         }
	      }
	      return UnexecutableCommand.INSTANCE;
	   }

	protected Command getCreateCommand(CreateRequest request) {
		if (request.getNewObjectType() == ClassDiagramNote.class) {
			ClassDiagram classDiagram = (ClassDiagram) getHost().getModel();
			ClassDiagramNote classDiagramNote = (ClassDiagramNote) request.getNewObject();
			classDiagramNote.setLocation( request.getLocation() );
			return new ClassDiagramNoteCreateCommand(classDiagram, classDiagramNote );
		}
		return UnexecutableCommand.INSTANCE;
	}
}
