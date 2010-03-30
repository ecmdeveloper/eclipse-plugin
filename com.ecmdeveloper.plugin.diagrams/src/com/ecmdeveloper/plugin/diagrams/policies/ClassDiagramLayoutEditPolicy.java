package com.ecmdeveloper.plugin.diagrams.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import com.ecmdeveloper.plugin.diagrams.actions.AddClassDiagramClassAction;
import com.ecmdeveloper.plugin.diagrams.commands.AdjustConstraintCommand;
import com.ecmdeveloper.plugin.diagrams.commands.ClassDiagramClassCreateCommand;
import com.ecmdeveloper.plugin.diagrams.commands.ClassDiagramNoteCreateCommand;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagram;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramNote;
import com.ecmdeveloper.plugin.diagrams.parts.AbstractClassesGraphicalEditPart;

public class ClassDiagramLayoutEditPolicy extends XYLayoutEditPolicy
{
	@Override
	public Command getCommand(Request request) {
		
		if (request instanceof CreateRequest
				&& AddClassDiagramClassAction.REQUEST_TYPE.equals(request.getType())) {
			return createClassDiagramClassCreateCommand((CreateRequest) request);
		}

		return super.getCommand(request);
	}

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
			return createClassDiagramNoteCreateCommand(request);
		} else if ( request.getNewObjectType() == ClassDiagramClass.class ) {
			return createClassDiagramClassCreateCommand(request);
		}
		return UnexecutableCommand.INSTANCE;
	}

	private Command createClassDiagramClassCreateCommand(CreateRequest request) {
		ClassDiagram classDiagram = (ClassDiagram) getHost().getModel();
		ClassDiagramClass classDiagramClass = (ClassDiagramClass) request.getNewObject();
		classDiagramClass.setLocation( request.getLocation() );
		return new ClassDiagramClassCreateCommand(classDiagram, classDiagramClass );
	}

	private Command createClassDiagramNoteCreateCommand(CreateRequest request) {
		ClassDiagram classDiagram = (ClassDiagram) getHost().getModel();
		ClassDiagramNote classDiagramNote = (ClassDiagramNote) request.getNewObject();
		classDiagramNote.setLocation( request.getLocation() );
		return new ClassDiagramNoteCreateCommand(classDiagram, classDiagramNote );
	}
}
