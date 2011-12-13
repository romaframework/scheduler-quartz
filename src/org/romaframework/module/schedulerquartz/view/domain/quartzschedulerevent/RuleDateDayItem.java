package org.romaframework.module.schedulerquartz.view.domain.quartzschedulerevent;

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;

@CoreClass(orderFields = "month selected")
public class RuleDateDayItem {
  @ViewField(render = ViewConstants.RENDER_LABEL)
  private String      day;

  private boolean     selected;

  private RuleDateTab ruleDateTab;

  public RuleDateDayItem(RuleDateTab ruleDateTab, String month, boolean selected) {
    this.ruleDateTab = ruleDateTab;
    day = month;
    this.selected = selected;
  }

  public String getDay() {
    return day;
  }

  public void setDay(String iDay) {
    day = iDay;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    ruleDateTab.updateCronRule();
  }
}
