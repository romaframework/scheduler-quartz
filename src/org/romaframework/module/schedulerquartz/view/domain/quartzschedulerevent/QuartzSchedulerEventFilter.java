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

import org.romaframework.aspect.view.annotation.ViewClass;
import org.romaframework.frontend.domain.entity.ComposedEntityInstance;
import org.romaframework.module.schedulerquartz.domain.QuartzSchedulerEvent;

@ViewClass(label = "")
public class QuartzSchedulerEventFilter extends ComposedEntityInstance<QuartzSchedulerEvent> {
  public QuartzSchedulerEventFilter() {
    this(new QuartzSchedulerEvent());
  }

  public QuartzSchedulerEventFilter(QuartzSchedulerEvent iQuartzSchedulerTrigger) {
    super(iQuartzSchedulerTrigger);
  }
}
