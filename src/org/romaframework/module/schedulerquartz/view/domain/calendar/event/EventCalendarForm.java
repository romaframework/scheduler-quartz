/*
 * Copyright 2006-2008 Luca Garulli (luca.garulli--at--assetdata.it)
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
package org.romaframework.module.schedulerquartz.view.domain.calendar.event;

import java.util.Calendar;
import java.util.List;

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.Roma;
import org.romaframework.module.schedulerquartz.domain.QuartzSchedulerEvent;
import org.romaframework.module.schedulerquartz.view.domain.calendar.component.CalendarCellDay;
import org.romaframework.module.schedulerquartz.view.domain.calendar.component.CalendarForm;

/**
 * Form containing the Event Calendar.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 * 
 */
@CoreClass(orderActions = "saveCalendar rescheduleAllEvents")
public class EventCalendarForm extends CalendarForm {

  @ViewField(render = ViewConstants.RENDER_OBJECTEMBEDDED, label = "")
  protected EventCalendarDetailForm detail = null;

  public EventCalendarForm() {
    super(EventCalendarCell.class);
  }

  @Override
  protected Object getCellValue(Calendar iCalendar) {
    Calendar startCal = (Calendar) iCalendar.clone();
    startCal.set(Calendar.HOUR_OF_DAY, 0);

    Calendar endCal = (Calendar) iCalendar.clone();
    endCal.set(Calendar.HOUR_OF_DAY, 23);
    endCal.set(Calendar.MINUTE, 59);

    QueryByFilter query = new QueryByFilter(QuartzSchedulerEvent.class);
    query.addItem("startTime", QueryByFilter.FIELD_MAJOR_EQUALS, startCal.getTime());
    query.addItem("startTime", QueryByFilter.FIELD_MINOR_EQUALS, endCal.getTime());
    query.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
    List<QuartzSchedulerEvent> events = Roma.context().persistence().query(query);
    return events;
  }

  @Override
  public void onSelection(CalendarCellDay iCalendar) {
    if (detail == null)
      detail = new EventCalendarDetailForm(this);
    detail.setCalendar((EventCalendarCell) iCalendar);
    lastSelection = iCalendar;
    Roma.fieldChanged(this, "detail");
  }

  public EventCalendarDetailForm getDetail() {
    return detail;
  }
}
