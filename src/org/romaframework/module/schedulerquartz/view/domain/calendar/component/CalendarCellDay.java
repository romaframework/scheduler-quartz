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
package org.romaframework.module.schedulerquartz.view.domain.calendar.component;

import java.util.Calendar;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.aspect.view.feature.ViewFieldFeatures;
import org.romaframework.core.Roma;

/**
 * Grid cell displaying a single day.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 * 
 */
@CoreClass(orderFields = "label value")
public class CalendarCellDay extends CalendarCell implements ViewCallback {
  @ViewField(visible = AnnotationConstants.FALSE)
  protected Calendar          day;

  @ViewField(visible = AnnotationConstants.FALSE)
  protected CalendarComponent owner;

  public void setup(CalendarComponent iOwner, Calendar iDay, Object iValue) {
    owner = iOwner;
    day = iDay;

    if (day != null)
      setLabel(String.format("%02d", day.get(Calendar.DAY_OF_MONTH)));

    setValue(iValue);
  }

  public void onShow() {
    Roma.setFeature(this, "label", ViewFieldFeatures.VISIBLE, day != null);
  }

  public Calendar getDay() {
    return day;
  }

  public void onLabel() {
    owner.onSelection(this);
  }

  @ViewField(render = ViewConstants.RENDER_LABEL, label = "")
  public Object getValue() {
    return null;
  }

  public void setValue(Object iValue) {
  }

  public void onDispose() {
  }
}
