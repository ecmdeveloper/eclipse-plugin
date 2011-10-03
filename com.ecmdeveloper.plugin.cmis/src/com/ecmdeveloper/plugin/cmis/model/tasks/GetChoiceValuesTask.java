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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ecmdeveloper.plugin.core.model.IChoice;
import com.ecmdeveloper.plugin.core.model.tasks.AbstractTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetChoiceValuesTask;
import com.ecmdeveloper.plugin.cmis.model.Choice;
import com.ecmdeveloper.plugin.cmis.model.ObjectStore;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetChoiceValuesTask extends AbstractTask implements IGetChoiceValuesTask{

	private Choice parentChoice;
	public List<?> choiceList;
	private ArrayList<IChoice> choices;
	private final ObjectStore objectStore;
	
	public GetChoiceValuesTask(Choice parentChoice, ObjectStore objectStore) {
		this.parentChoice = parentChoice;
		this.objectStore = objectStore;
	}

	public GetChoiceValuesTask(List<?> choiceList, ObjectStore objectStore) {
		this.choiceList = choiceList;
		this.objectStore = objectStore;
	}
	
	@Override
	public Object call() throws Exception {

		Iterator<?> iterator = getChoiceValuesIterator();
		choices = getChoices(iterator);
		fireTaskCompleteEvent( TaskResult.COMPLETED );
		return choices;
	}

	@Override
	public ArrayList<IChoice> getChoices() {
		return choices;
	}

	private ArrayList<IChoice> getChoices(Iterator<?> iterator) {

		ArrayList<IChoice> choices = new ArrayList<IChoice>();

		while (iterator.hasNext()) {
			Object internalChildChoice = iterator.next();
			choices.add( new Choice( internalChildChoice, parentChoice, objectStore ) );
		}
		return choices;
	}

	private Iterator<?> getChoiceValuesIterator() {
		Iterator<?> iterator;
		
		if ( parentChoice != null ) {
			
			org.apache.chemistry.opencmis.commons.definitions.Choice internalChoice = parentChoice.getObjectStoreObject();
			iterator = internalChoice.getChoice().iterator();
		} else if ( choiceList != null ) {
			iterator = choiceList.iterator();
		} else {
			throw new UnsupportedOperationException();
		}
		return iterator;
	}
}
