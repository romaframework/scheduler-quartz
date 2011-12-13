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
package org.romaframework.module.schedulerquartz.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.core.annotation.CoreField;
import org.romaframework.aspect.scheduler.SchedulerEvent;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;

@CoreClass(orderFields = "name rule startTime implementation")
public class QuartzSchedulerEvent implements SchedulerEvent {

  protected String              name;

  protected String              rule;

  protected Date                startTime;

  @ViewField(visible = AnnotationConstants.FALSE)
  protected String              implementation;

  @CoreField(useRuntimeType = AnnotationConstants.TRUE)
  @ViewField(render = ViewConstants.RENDER_TABLEEDIT)
  protected Map<String, Object> context = new HashMap<String, Object>();

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append(name);
    if (startTime != null) {
      buffer.append(" starting from: ");
      buffer.append(startTime);
    }
    if (rule != null) {
      buffer.append(" rule: ");
      buffer.append(rule);
    }
    if (implementation != null) {
      buffer.append(" implementation: ");
      buffer.append(implementation);
    }
    return buffer.toString();
  }

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public String getImplementation() {
    return implementation;
  }

  public void setImplementation(String implementation) {
    this.implementation = implementation;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  public void addContextParameter(String key, Object value) {
    context.put(key, value);
  }
}
