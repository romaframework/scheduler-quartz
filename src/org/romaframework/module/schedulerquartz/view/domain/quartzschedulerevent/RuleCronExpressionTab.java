package org.romaframework.module.schedulerquartz.view.domain.quartzschedulerevent;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.core.Roma;

public class RuleCronExpressionTab {
  protected String rule;

  @ViewField(render = ViewConstants.RENDER_TEXTAREA, enabled = AnnotationConstants.FALSE)
  public String getRuleSyntaxHelp() {
    return "+------------------- seconds (0 - 59)\n" + "|  +---------------- minute (0 - 59)\n"
        + "|  |  +------------- hour (0 - 23)\n" + "|  |  |  +---------- day of month (1 - 31)\n"
        + "|  |  |  |  +------- month (1 - 12)\n" + "|  |  |  |  |  +---- day of week (0 - 6) (Sunday=0 or 7)\n"
        + "|  |  |  |  |  |\n" + "*  *  *  *  *  *\n\n" + "Field Name      Allowed Values    Allowed Special Characters\n"
        + "Seconds         0-59              , - * /\n" + "Minutes         0-59              , - * /\n"
        + "Hours           0-23              , - * /\n" + "Day-of-month    1-31              , - * ? / L W C\n"
        + "Month           1-12 or JAN-DEC   , - * /\n" + "Day-of-Week     1-7 or SUN-SAT    , - * ? / L C #\n"
        + "Year (Optional) empty, 1970-2099  , - * /\n" + "\nExamples:\n" + "0 15 10 ? * *      Fire at 10:15am every day\n"
        + "0 0/5 14,18 * * ?  Fire every 5 minutes starting at 2pm and ending at 2:55pm, AND fire every 5\n"
        + "                   minutes starting at 6pm and ending at 6:55pm, every day";
  }

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public void updateRule(String iRule) {
    setRule(iRule);
    Roma.fieldChanged(this, "rule");
  }
}
