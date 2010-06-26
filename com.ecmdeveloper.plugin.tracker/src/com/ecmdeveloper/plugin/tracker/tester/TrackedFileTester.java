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

package com.ecmdeveloper.plugin.tracker.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;

import com.ecmdeveloper.plugin.tracker.model.FilesTracker;

/**
 * @author Ricardo.Belfor
 *
 */
public class TrackedFileTester extends PropertyTester {

	private static final String IS_TRACKED_PROPERTY = "isTracked";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		
		if ( ! ( receiver instanceof IFile ) )
		{
			return false;
		}

		if ( IS_TRACKED_PROPERTY.equals(property) ) {
			String filename = ((IFile) receiver).getFullPath().toString();
			System.out.println( filename );
			return FilesTracker.getInstance().isFileTracked(filename);
		}
		
		return false;
	}
}
