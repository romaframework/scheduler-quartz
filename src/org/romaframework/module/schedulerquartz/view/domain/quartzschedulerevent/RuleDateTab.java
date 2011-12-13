package org.romaframework.module.schedulerquartz.view.domain.quartzschedulerevent;

import java.util.ArrayList;
import java.util.List;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.view.ViewCallback;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewAction;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.Roma;

public class RuleDateTab implements ViewCallback {
  protected final static String[] DAY_OF_THE_WEEK = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
  protected final static Short[]  HOURS;
  protected final static Short[]  MINUTES;
  protected final static Short[]  SECONDS;

  protected List<RuleDateDayItem> days;

  @ViewField(visible = AnnotationConstants.FALSE)
  protected short                 hour;
  @ViewField(visible = AnnotationConstants.FALSE)
  protected short                 minute;
  @ViewField(visible = AnnotationConstants.FALSE)
  protected short                 second;

  private RuleCronExpressionTab   ruleTab;

  static {
    HOURS = new Short[24];
    for (short i = 0; i < 24; ++i)
      HOURS[i] = i;

    MINUTES = new Short[60];
    for (short i = 0; i < 60; ++i)
      MINUTES[i] = i;

    SECONDS = new Short[60];
    for (short i = 0; i < 60; ++i)
      SECONDS[i] = i;
  }

  public RuleDateTab(RuleCronExpressionTab iRuleTab) {
    this(iRuleTab, (short) 0, (short) 0, (short) 0);
  }

  public void onShow() {
    loadFieldsFromRule();
  }

  public void onDispose() {
  }

  @ViewAction(visible = AnnotationConstants.FALSE)
  public void loadFieldsFromRule() {
    if (ruleTab.getRule() != null && ruleTab.getRule().length() > 0) {
      String fields[] = ruleTab.getRule().split(" ");
      try {
        second = Short.parseShort(fields[0]);
        Roma.fieldChanged(this, "seconds");
      } catch (Exception e) {
      }
      try {
        minute = Short.parseShort(fields[1]);
        Roma.fieldChanged(this, "minutes");
      } catch (Exception e) {
      }
      try {
        hour = Short.parseShort(fields[2]);
        Roma.fieldChanged(this, "hours");
      } catch (Exception e) {
      }

      if (fields.length > 5) {
        if (fields[5].equals("*")) {
          // SELECT ALL DAYS
          for (RuleDateDayItem d : days) {
            d.setSelected(Boolean.TRUE);
          }
          Roma.fieldChanged(this, "days");
        }
      }
    }
  }

  public RuleDateTab(RuleCronExpressionTab iRuleTab, short hour, short minute, short second) {
    ruleTab = iRuleTab;

    days = new ArrayList<RuleDateDayItem>();
    for (short i = 0; i < DAY_OF_THE_WEEK.length; ++i) {
      days.add(new RuleDateDayItem(this, DAY_OF_THE_WEEK[i], Boolean.FALSE));
    }

    this.hour = hour;
    this.minute = minute;
    this.second = second;
  }

  @ViewField(render = ViewConstants.RENDER_TABLEEDIT, enabled = AnnotationConstants.FALSE)
  public List<RuleDateDayItem> getDays() {
    return days;
  }

  @ViewField(render = ViewConstants.RENDER_SELECT, label = "Time", selectionField = "hour")
  public Short[] getHours() {
    return HOURS;
  }

  @ViewField(render = ViewConstants.RENDER_SELECT, label = ":", selectionField = "minute")
  public Short[] getMinutes() {
    return MINUTES;
  }

  @ViewField(render = ViewConstants.RENDER_SELECT, label = ":", selectionField = "second")
  public Short[] getSeconds() {
    return SECONDS;
  }

  @ViewAction(visible = AnnotationConstants.FALSE)
  public void updateCronRule() {
    StringBuilder rule = new StringBuilder();

    rule.append(second);
    rule.append(' ');
    rule.append(minute);
    rule.append(' ');
    rule.append(hour);
    rule.append(' ');

    rule.append("? * ");

    // APPEND DAYS IF ANY
    StringBuilder dayRule = new StringBuilder();
    int selectedDays = 0;
    for (RuleDateDayItem d : days) {
      if (d.isSelected()) {
        ++selectedDays;
        if (dayRule.length() > 0)
          dayRule.append(',');

        dayRule.append(d.getDay().substring(0, 3).toUpperCase());
      }
    }
    if (selectedDays == 7)
      // ALL DAYS SELECTED: WRITE *
      rule.append("*");
    else
      // WRITE INDIVIDUAL FIELDS
      rule.append(dayRule);

    ruleTab.setRule(rule.toString());
    Roma.fieldChanged(ruleTab, "rule");
  }

  public short getHour() {
    return hour;
  }

  public void setHour(short hour) {
    this.hour = hour;
    updateCronRule();
  }

  public short getMinute() {
    return minute;
  }

  public void setMinute(short minute) {
    this.minute = minute;
    updateCronRule();
  }

  public short getSecond() {
    return second;
  }

  public void setSecond(short second) {
    this.second = second;
    updateCronRule();
  }
}
