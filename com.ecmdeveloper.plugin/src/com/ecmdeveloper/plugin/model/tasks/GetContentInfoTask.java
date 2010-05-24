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

package com.ecmdeveloper.plugin.model.tasks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ecmdeveloper.plugin.model.Document;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.ContentReference;
import com.filenet.api.core.ContentTransfer;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetContentInfoTask  extends BaseTask {

	private Document document;
	private Map<String,Integer> contentElementsMap;
	
	public GetContentInfoTask(Document document) {
		this.document = document;
		contentElementsMap = new HashMap<String,Integer>();
	}

	@Override
	public Object call() throws Exception {

		com.filenet.api.core.Document internalDocument = (com.filenet.api.core.Document) document.getObjectStoreObject();
		contentElementsMap.clear();
		
		internalDocument.fetchProperties( new String[] { PropertyNames.CONTENT_ELEMENTS, PropertyNames.RETRIEVAL_NAME } );
		ContentElementList contentElementList = internalDocument.get_ContentElements();
		
		for ( int index = 0; index < contentElementList.size(); ++index ) {
			addContentElement(index, contentElementList.get(index) );
		}

		return null;
	}

	private void addContentElement(int index, Object contentElement) {
		if ( contentElement instanceof ContentTransfer ) {
			ContentTransfer contentTransfer = (ContentTransfer) contentElement;
			String key = Integer.toString(index+1) + " " + contentTransfer.get_RetrievalName();
			contentElementsMap.put(key, new Integer(index) );
		}
	}

	public Map<String,Integer> getContentElementsMap() {
		return contentElementsMap;
	}
}
