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

package com.ecmdeveloper.plugin.cmis.model.tasks;

import java.util.HashMap;
import java.util.Map;

import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.IGetContentInfoTask;
import com.ecmdeveloper.plugin.cmis.model.Document;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetContentInfoTask  extends AbstractTask implements IGetContentInfoTask {

	private Document document;
	private Map<String,Integer> contentElementsMap;
	
	public GetContentInfoTask(Document document) {
		this.document = document;
		contentElementsMap = new HashMap<String,Integer>();
	}

	@Override
	public Object call() throws Exception {

		org.apache.chemistry.opencmis.client.api.Document d = document.getInternalDocument();
		String contentStreamFileName = d.getContentStreamFileName();
		contentElementsMap.clear();

		// TODO figure out what to do with the other streams
		if ( contentStreamFileName != null ) {
			String key = Integer.toString(1) + " " + contentStreamFileName;
			contentElementsMap.put(key, 0 );
		}

		return null;
	}

	@Override
	public Map<String,Integer> getContentElementsMap() {
		return contentElementsMap;
	}
}
