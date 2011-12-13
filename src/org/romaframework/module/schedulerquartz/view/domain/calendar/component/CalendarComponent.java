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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewAction;
import org.romaframework.aspect.view.annotation.ViewField;

/**
 * Acts as a grid, but in the back-end handles values dynamically using a List of List.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 * 
 */
public class CalendarComponent {
  @ViewField(render = ViewConstants.RENDER_TABLEEDIT, label = "", enabled = AnnotationConstants.FALSE)
  private List<CalendarRow>                rows;

  private int                              maxColumns = 0;

  @ViewField(visible = AnnotationConstants.FALSE)
  private Class<? extends CalendarCellDay> cellClass;

  private CalendarComponentListener        listener;

  public CalendarComponent() {
    this(CalendarCellDay.class);
  }

  public CalendarComponent(Class<? extends CalendarCellDay> iCellClass) {
    cellClass = iCellClass;
    rows = new ArrayList<CalendarRow>();
  }

  public List<CalendarRow> getRows() {
    return rows;
  }

  public void setValue(int x, int y, Calendar iDay, Object iValue) {
    assureRowSpace(x);

    assureEqualColumnForEachRows(y);

    CalendarRow row = rows.get(x);
    row.setValue(y, iDay, iValue);
  }

  private void assureEqualColumnForEachRows(int y) {
    if (y >= maxColumns) {
      // EXPAND ALL ROWS BY ONE COLUMN
      for (CalendarRow r : rows) {
        r.setValue(y, null, null);
      }
      maxColumns = y;
    }
  }

  private void assureRowSpace(int x) {
    if (x >= rows.size() - 1) {
      addRows(x - rows.size() + 1);
    }
  }

  public void setHeader(String[] iRowValues) {
    assureRowSpace(0);
    assureEqualColumnForEachRows(iRowValues.length);

    CalendarRow row = rows.get(0);
    row.setHeader(iRowValues);
  }

  public void addRows(int iRows) {
    for (int i = 0; i < iRows; ++i) {
      rows.add(new CalendarRow(this, maxColumns));
    }
  }

  @ViewAction(visible = AnnotationConstants.FALSE)
  public void clear() {
    rows.clear();
  }

  @Override
  public String toString() {
    return rows.toString();
  }

  public Class<? extends CalendarCellDay> getCellClass() {
    return cellClass;
  }

  public void onSelection(CalendarCellDay iDay) {
    listener.onSelection(iDay);
  }

  public void addListener(CalendarComponentListener iListener) {
    listener = iListener;
  }
}
