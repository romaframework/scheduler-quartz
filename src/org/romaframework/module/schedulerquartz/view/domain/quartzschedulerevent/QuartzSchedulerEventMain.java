package org.romaframework.module.schedulerquartz.view.domain.quartzschedulerevent;

import java.util.ArrayList;
import java.util.List;

import org.romaframework.aspect.core.annotation.AnnotationConstants;
import org.romaframework.aspect.core.annotation.CoreField;
import org.romaframework.aspect.scheduler.SchedulerAspect;
import org.romaframework.core.Roma;
import org.romaframework.frontend.domain.crud.CRUDMain;
import org.romaframework.frontend.domain.message.MessageOk;
import org.romaframework.frontend.view.domain.RomaControlPanelTab;

public class QuartzSchedulerEventMain extends CRUDMain<QuartzSchedulerEventListable> implements RomaControlPanelTab {
	public QuartzSchedulerEventMain() {
		super(QuartzSchedulerEventListable.class, QuartzSchedulerEventInstance.class, QuartzSchedulerEventInstance.class, QuartzSchedulerEventInstance.class);
		filter = new QuartzSchedulerEventFilter();
		result = new ArrayList<QuartzSchedulerEventListable>();
	}

	@Override
	public void delete() throws InstantiationException, IllegalAccessException {

		Object[] triggers = getSelection();
		for (Object trigger : triggers)
			Roma.aspect(SchedulerAspect.class).unSchedule(((QuartzSchedulerEventListable) trigger).getEntity());

		super.delete();
	}

	public void pause() throws InstantiationException, IllegalAccessException {

		Object[] triggers = getSelection();
		for (Object trigger : triggers)
			Roma.aspect(SchedulerAspect.class).pauseJob(((QuartzSchedulerEventListable) trigger).getEntity());
	}

	public void resume() throws InstantiationException, IllegalAccessException {

		Object[] triggers = getSelection();
		for (Object trigger : triggers) {
			// TODO check status
			Roma.aspect(SchedulerAspect.class).unpauseJob(((QuartzSchedulerEventListable) trigger).getEntity());
		}
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

	public void executeNow() {
		Object[] triggers = getSelection();

		if (triggers == null) {
			Roma.flow().popup(new MessageOk("1", "$Message.Warning", null, "$CRUDMain.selectAtLeastOne.error"));
			return;
		}

		for (Object trigger : triggers) {
			Roma.aspect(SchedulerAspect.class).executeNow(((QuartzSchedulerEventListable) trigger).getEntity());
		}
	}

	@CoreField(embedded = AnnotationConstants.TRUE)
	protected QuartzSchedulerEventFilter					filter;

	protected List<QuartzSchedulerEventListable>	result;
}
