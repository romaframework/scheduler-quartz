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

import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewClass;

/**
 * Define a row of objects rendered inside a CalendarComponent.
 * 
 * @author Luca Garulli (luca.garulli--at--assetdata.it)
 * 
 */
@ViewClass(render = ViewConstants.RENDER_OBJECTEMBEDDED)
public class CalendarRow extends ArrayList<CalendarCellDay> {
  private static final long   serialVersionUID = 1L;

  protected CalendarComponent owner;

  public CalendarRow(CalendarComponent iOwner, int maxColumns) {
    owner = iOwner;

    // INITIALIZE LIST
    for (int i = 0; i < maxColumns; ++i) {
      add(createCell(null, null));
    }
  }

  public void setValue(int y, Calendar iDay, Object iValue) {
    if (y >= size() - 1) {
      addCells(y - size() + 1);
    }

    set(y, createCell(iDay, iValue));
  }

  public void setHeader(String[] rowValues) {
    clear();

    List<CalendarCellDay> cells = new ArrayList<CalendarCellDay>(rowValues.length);
    for (int i = 0; i < rowValues.length; ++i)
      cells.add(createHeaderCell(rowValues[i]));

    addAll(cells);
  }

  public void setValues(CalendarCellDay[] iRowValues) {
    clear();
    for (int i = 0; i < iRowValues.length; ++i) {
      add(iRowValues[i]);
    }
  }

  public void addCells(int iCells) {
    for (int i = 0; i < iCells; ++i) {
      add(null);
    }
  }

  protected CalendarCellDay createCell(Calendar iDay, Object iValue) {
    try {
      CalendarCellDay cell = owner.getCellClass().newInstance();
      cell.setup(owner, iDay, iValue);
      return cell;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  protected CalendarCellDay createHeaderCell(String iColumnName) {
    try {
      CalendarCellDay cell = owner.getCellClass().newInstance();
      cell.setLabel(iColumnName);
      return cell;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
