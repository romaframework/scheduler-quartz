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

import java.util.Set;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.flow.annotation.FlowAction;
import org.romaframework.aspect.scheduler.SchedulerAspect;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewAction;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.Roma;
import org.romaframework.core.config.Refreshable;
import org.romaframework.frontend.domain.crud.CRUDHelper;
import org.romaframework.frontend.domain.crud.CRUDWorkingMode;
import org.romaframework.module.schedulerquartz.domain.QuartzSchedulerEvent;
import org.romaframework.module.schedulerquartz.view.domain.quartzschedulerevent.QuartzSchedulerEventInstance;

/**
 * Form containing the detail of the day selected.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 * 
 */
@CoreClass(orderFields = "day events")
public class EventCalendarDetailForm implements Refreshable{

  protected EventCalendarCell    calendar;

  @ViewField(visible = AnnotationConstants.FALSE)
  protected QuartzSchedulerEvent selection;

  @ViewField(visible = AnnotationConstants.FALSE)
  private EventCalendarForm      callerForm;

  public EventCalendarDetailForm(EventCalendarForm eventCalendarForm) {
    callerForm = eventCalendarForm;
  }

  @ViewField(render = ViewConstants.RENDER_LABEL)
  public String getDay() {
    return calendar != null ? calendar.getLabel() : "?";
  }

  @ViewField(render = ViewConstants.RENDER_TABLE, selectionField = "selection")
  public Set<QuartzSchedulerEvent> getEvents() {
    return calendar != null ? calendar.getEvents() : null;
  }

  public void onEventsAdd() {
    QuartzSchedulerEventInstance form = new QuartzSchedulerEventInstance();
    form.setEntity(new QuartzSchedulerEvent());
    form.setMode(CRUDWorkingMode.MODE_CREATE);

    // FORCE THE DATE
    form.getEntity().setStartTime(calendar.getDay().getTime());

    CRUDHelper.show(form, this, "events");
  }
  

  
  @FlowAction(confirmRequired=AnnotationConstants.TRUE)
  public void onEventsRemove() {
    QuartzSchedulerEvent selected = getSelection();
    this.getEvents().remove(selected);
  	Roma.component(SchedulerAspect.class).unSchedule(selected);
  	Roma.context().persistence().deleteObject(selected);
  	Roma.fieldChanged(this, "events");
  	refresh();
  }

  public void setCalendar(EventCalendarCell iCalendar) {
    calendar = iCalendar;
  }

  public QuartzSchedulerEvent getSelection() {
    return selection;
  }

  public void setSelection(QuartzSchedulerEvent selection) {
    this.selection = selection;
  }

  public EventCalendarForm getCallerForm() {
    return callerForm;
  }

  @ViewAction(visible= AnnotationConstants.FALSE)
	public void refresh() {
		this.callerForm.refreshCalendar();
		Roma.fieldChanged(this, "events");
	}
}
