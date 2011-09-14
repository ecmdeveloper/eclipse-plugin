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

import com.ecmdeveloper.plugin.core.model.tasks.ICheckinTask;
import com.ecmdeveloper.plugin.core.model.tasks.TaskResult;
import com.ecmdeveloper.plugin.model.Document;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.RefreshMode;

/**
 * @author Ricardo.Belfor
 *
 */
public class CheckinTask extends DocumentTask implements ICheckinTask {

	private boolean majorVersion;
	private boolean autoClassify;

	public CheckinTask(Document document, boolean majorVersion, boolean autoClassify ) {
		super(document);
		this.majorVersion = majorVersion;
		this.autoClassify = autoClassify;
	}

	protected Object execute() throws Exception {
		
		com.filenet.api.core.Document reservation = getReservation();
		CheckinType checkinType = majorVersion ? CheckinType.MAJOR_VERSION
				: CheckinType.MINOR_VERSION;
		AutoClassify autoClassifyFlag = autoClassify ? AutoClassify.AUTO_CLASSIFY
				: AutoClassify.DO_NOT_AUTO_CLASSIFY; 
		reservation.checkin( autoClassifyFlag, checkinType );
		reservation.save( RefreshMode.REFRESH );
		getDocument().refresh( reservation );
		
		fireTaskCompleteEvent( TaskResult.COMPLETED );

		return null;
	}

}
