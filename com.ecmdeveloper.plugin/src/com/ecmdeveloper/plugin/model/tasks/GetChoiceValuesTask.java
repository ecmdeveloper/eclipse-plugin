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

import java.util.ArrayList;
import java.util.Iterator;

import com.ecmdeveloper.plugin.core.model.IChoice;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.core.model.tasks.classes.IGetChoiceValuesTask;
import com.ecmdeveloper.plugin.model.Choice;
import com.ecmdeveloper.plugin.model.ContentEngineConnection;
import com.ecmdeveloper.plugin.model.ObjectStore;
import com.filenet.api.admin.ChoiceList;

/**
 * @author Ricardo.Belfor
 *
 */
public class GetChoiceValuesTask extends BaseTask implements IGetChoiceValuesTask{

	private Choice parentChoice;
	public ChoiceList choiceList;
	private ArrayList<IChoice> choices;
	private final ObjectStore objectStore;
	
	public GetChoiceValuesTask(Choice parentChoice, ObjectStore objectStore) {
		this.parentChoice = parentChoice;
		this.objectStore = objectStore;
	}

	public GetChoiceValuesTask(ChoiceList choiceList, ObjectStore objectStore) {
		this.choiceList = choiceList;
		this.objectStore = objectStore;
	}
	
	@Override
	protected Object execute() throws Exception {

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
			com.filenet.api.admin.Choice internalChildChoice = (com.filenet.api.admin.Choice) iterator.next();
			choices.add( new Choice( internalChildChoice, parentChoice, objectStore ) );
		}
		return choices;
	}

	private Iterator<?> getChoiceValuesIterator() {
		Iterator<?> iterator;
		
		if ( parentChoice != null ) {
			com.filenet.api.admin.Choice internalChoice = (com.filenet.api.admin.Choice) parentChoice
					.getObjectStoreObject();
			iterator = internalChoice.get_ChoiceValues().iterator();
		} else if ( choiceList != null ) {
			iterator = choiceList.get_ChoiceValues().iterator();
		} else {
			throw new UnsupportedOperationException();
		}
		return iterator;
	}

	@Override
	protected ContentEngineConnection getContentEngineConnection() {
		return objectStore.getConnection();
	}
}
