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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.module.schedulerquartz.domain.QuartzSchedulerEvent;
import org.romaframework.module.schedulerquartz.view.domain.calendar.component.CalendarCellDay;

/**
 * Calendar Cell extension to supports Event fields.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 * 
 */
@CoreClass(orderFields = "label textEvents")
public class EventCalendarCell extends CalendarCellDay {
  protected Set<QuartzSchedulerEvent> events;

  @Override
  @ViewField(visible = AnnotationConstants.FALSE)
  public Object getValue() {
    return events;
  }

  @SuppressWarnings("unchecked")
	@Override
  public void setValue(Object iValue) {
    events = iValue != null ? new HashSet<QuartzSchedulerEvent>((Collection<QuartzSchedulerEvent>) iValue)
        : new HashSet<QuartzSchedulerEvent>();
  }

  @ViewField(render = ViewConstants.RENDER_LABEL, label = "", enabled = AnnotationConstants.FALSE)
  public String getTextEvents() {
    if (events == null || events.size() == 0)
      return "";

    return "Scadenze: " + events.size();
  }

  @ViewField(visible = AnnotationConstants.FALSE)
  public Set<QuartzSchedulerEvent> getEvents() {
    return events;
  }
}
