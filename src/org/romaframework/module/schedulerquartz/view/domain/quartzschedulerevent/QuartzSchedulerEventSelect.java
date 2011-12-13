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

import java.util.ArrayList;
import java.util.List;

import org.romaframework.frontend.domain.crud.CRUDSelect;

public class QuartzSchedulerEventSelect extends CRUDSelect<QuartzSchedulerEventListable> {
  public QuartzSchedulerEventSelect() {
    super(QuartzSchedulerEventListable.class, QuartzSchedulerEventInstance.class, QuartzSchedulerEventInstance.class,
        QuartzSchedulerEventInstance.class);
    filter = new QuartzSchedulerEventFilter();
    result = new ArrayList<QuartzSchedulerEventListable>();
  }

  @Override
  public QuartzSchedulerEventFilter getFilter() {
    return filter;
  }

  @Override
  public List<QuartzSchedulerEventListable> getResult() {
    return result;
  }

  @SuppressWarnings("unchecked")
	@Override
  public void setResult(Object iValue) {
    result = (List<QuartzSchedulerEventListable>) iValue;
  }

  protected QuartzSchedulerEventFilter         filter;

  protected List<QuartzSchedulerEventListable> result;
}
