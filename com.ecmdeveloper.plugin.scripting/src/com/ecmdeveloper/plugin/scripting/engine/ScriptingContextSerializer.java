/**
 * Copyright 2011, Ricardo Belfor
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

package com.ecmdeveloper.plugin.scripting.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.eclipse.core.runtime.IPath;

/**
 * @author ricardo.belfor
 *
 */
public class ScriptingContextSerializer {

	private static final String DATA_FOLDER = "data";
	
	private IPath dataPath;
	
	public ScriptingContextSerializer(IPath parentPath) {
		dataPath = parentPath.append(DATA_FOLDER);
		if ( ! dataPath.toFile().exists() ) {
			dataPath.toFile().mkdir();
		}
	}
	
	public String serialize(ScriptingContext scriptingContext) throws FileNotFoundException,
			IOException {

		ObjectOutputStream objectOutputStream = null;
		try {
			File file = dataPath.append( UUID.randomUUID().toString() ).toFile();
			FileOutputStream fos = new FileOutputStream(file);
			objectOutputStream = new ObjectOutputStream(fos);
			objectOutputStream.writeObject(scriptingContext);
			return file.getPath();
		} finally {
			if (objectOutputStream != null) {
				objectOutputStream.close();
			}
		}
	}
	
	public void clear() {
		for ( File file : dataPath.toFile().listFiles() ) {
			file.delete();
		}
	}
}
