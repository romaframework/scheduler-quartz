/*
 * Copyright 2007 Paolo Spizzirri (paolo.spizzirri@assetdata.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.romaframework.module.schedulerquartz.view.domain.quartzschedulerevent;

import java.text.DateFormat;
import java.util.Date;

import org.romaframework.aspect.scheduler.SchedulerAspect;
import org.romaframework.core.Roma;
import org.romaframework.frontend.domain.entity.ComposedEntityInstance;
import org.romaframework.module.schedulerquartz.QuartzSchedulerAspect;
import org.romaframework.module.schedulerquartz.domain.QuartzSchedulerEvent;

public class QuartzSchedulerEventListable extends ComposedEntityInstance<QuartzSchedulerEvent> {

	public QuartzSchedulerEventListable(QuartzSchedulerEvent iEntity) {
		super(iEntity);
	}

	// INSERT HERE GETTER METHOD TO VIEW ADDITIONAL FIELDS
	public String getStatus() {
		QuartzSchedulerAspect schedulerAspect = (QuartzSchedulerAspect) Roma.aspect(SchedulerAspect.class);
		return schedulerAspect.getEventState(getEntity().getName());
	}

	public String getStartTime() {
		Date date = entity.getStartTime();
		if (date == null)
			return null;

		DateFormat dateFormatter = Roma.i18n().getDateTimeFormat();
		return dateFormatter.format(date);
	}

	public String getNextExecution() {
		SchedulerAspect schedulerAspect = Roma.aspect(SchedulerAspect.class);
		Date date = schedulerAspect.getNextEventExcecution(getEntity().getName());

		if (date == null)
			return null;

		DateFormat dateFormatter = Roma.i18n().getDateTimeFormat();
		return dateFormatter.format(date);
	}

	public String getLastExecution() {
		SchedulerAspect schedulerAspect = Roma.aspect(SchedulerAspect.class);
		return schedulerAspect.getLastEventExcecution(getEntity().getName());
	}
}
