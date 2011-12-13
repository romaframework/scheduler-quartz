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

import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewAction;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.Roma;

/**
 * Form display the calendar.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 * 
 */
public class CalendarForm implements ViewCallback, CalendarComponentListener {
  protected int               year;

  protected int               month;

  @ViewField(render = ViewConstants.RENDER_OBJECTEMBEDDED, label = "")
  protected CalendarComponent calendar;

  protected CalendarCellDay   lastSelection;

  public CalendarForm(Class<? extends CalendarCellDay> iCalendarClass) {
    calendar = new CalendarComponent(iCalendarClass);
    calendar.addListener(this);
  }

  public void onShow() {
    year = Calendar.getInstance().get(Calendar.YEAR);
    Roma.fieldChanged(this, "year");

    month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    Roma.fieldChanged(this, "month");

    refreshCalendar();
  }

  public void reset() {
    onShow();
  }

  @ViewAction(label = "")
  public void nextYear() {
    year += 1;
    Roma.fieldChanged(this, "year");
    refreshCalendar();
  }

  @ViewAction(label = "")
  public void prevYear() {
    if (year <= 2000)
      return;

    year -= 1;
    Roma.fieldChanged(this, "year");
    refreshCalendar();
  }

  @ViewAction(label = "")
  public void nextMonth() {
    if (month >= 12)
      return;

    month += 1;
    Roma.fieldChanged(this, "month");
    refreshCalendar();
  }

  @ViewAction(label = "")
  public void prevMonth() {
    if (month <= 1)
      return;

    month -= 1;
    Roma.fieldChanged(this, "month");
    refreshCalendar();
  }

  public void refreshCalendar() {
    calendar.clear();
    calendar.setHeader(new String[] { "LUN", "MAR", "MER", "GIOV", "VEN", "SAB", "DOM" });

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);

    int y = 1;
    int x;
    for (int day = 1; day <= cal.getMaximum(Calendar.DAY_OF_MONTH); ++day) {
      cal = (Calendar) cal.clone();
      cal.set(Calendar.DAY_OF_MONTH, day);

      if (cal.get(Calendar.MONTH) != month - 1)
        break;

      x = cal.get(Calendar.DAY_OF_WEEK) - 2;
      if (x < 0)
        x += 7;

      calendar.setValue(y, x, cal, getCellValue(cal));

      if (x >= 6)
        ++y;
    }

    Roma.fieldChanged(this, "calendar");
  }

  public void onSelection(CalendarCellDay calendar) {
  }

  protected Object getCellValue(Calendar cal) {
    return null;
  }

  public CalendarComponent getCalendar() {
    return calendar;
  }

  public void onDispose() {
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }
}
