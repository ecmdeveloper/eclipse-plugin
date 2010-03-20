/**
 * Copyright 2010, Ricardo Belfor
 * 
 * This file is part of the ECM Developer plug-in. The ECM Developer plug-in
 * is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * The ECM Developer plug-in is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ECM Developer plug-in. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 */

package com.ecmdeveloper.plugin.diagrams.editors;

import org.eclipse.gef.requests.CreationFactory;

import com.ecmdeveloper.plugin.classes.model.ClassDescription;
import com.ecmdeveloper.plugin.diagrams.model.ClassDiagramClass;

/**
 * @author Ricardo.Belfor
 *
 */
public class ClassDiagramClassFactory implements CreationFactory {

	private ClassDescription classDescription;

	public ClassDiagramClassFactory( ClassDescription classDiagramClass ) {
		this.classDescription = classDiagramClass;
	}

	@Override
	public Object getNewObject() {
		return classDescription.getAdapter( ClassDiagramClass.class );
	}

	@Override
	public Object getObjectType() {
		return ClassDiagramClass.class;
	}
}
