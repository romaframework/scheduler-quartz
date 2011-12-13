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

import java.util.Date;
import java.util.List;

import javax.security.auth.RefreshFailedException;
import javax.security.auth.Refreshable;

import org.romaframework.aspect.core.annotation.CoreClass;
import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.scheduler.SchedulerAspect;
import org.romaframework.aspect.validation.CustomValidation;
import org.romaframework.aspect.view.ViewConstants;
import org.romaframework.aspect.view.annotation.ViewField;
import org.romaframework.aspect.view.feature.ViewFieldFeatures;
import org.romaframework.core.Roma;
import org.romaframework.core.schema.SchemaHelper;
import org.romaframework.frontend.domain.crud.CRUDInstance;
import org.romaframework.frontend.domain.crud.CRUDWorkingMode;
import org.romaframework.frontend.domain.page.ContainerPage;
import org.romaframework.module.schedulerquartz.domain.QuartzSchedulerEvent;

@CoreClass(orderFields = "entity implementations")
public class QuartzSchedulerEventInstance extends CRUDInstance<QuartzSchedulerEvent> implements CustomValidation {

	private static final String	DATE_RULE_TAB	= "Date rule";
	private static final String	CRON_RULE_TAB	= "Cron expression";

	@ViewField(render = ViewConstants.RENDER_SELECT, label = "Implementations", selectionField = "entity.implementation")
	protected List<String>			implementations;

	@ViewField(render = ViewConstants.RENDER_OBJECTEMBEDDED)
	protected ContainerPage			innerPages;

	public QuartzSchedulerEventInstance() {
		implementations = Roma.aspect(SchedulerAspect.class).getImplementationsList();

		innerPages = new ContainerPage();
		RuleCronExpressionTab ruleTab = new RuleCronExpressionTab();
		innerPages.addPage(DATE_RULE_TAB, new RuleDateTab(ruleTab));
		innerPages.addPage(CRON_RULE_TAB, ruleTab);
	}

	public List<String> getImplementations() {
		return implementations;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		getEntity().setStartTime(new Date());

		if (implementations != null && implementations.size() == 1) {
			// AUTO-SELECT THE UNIQUE ENTRY BY DEFAULT
			getEntity().setImplementation(implementations.get(0));
			Roma.fieldChanged(this, "entity.implementation");
		}
	}

	@Override
	public void onShow() {
		if (entity.getRule() != null) {
			((RuleCronExpressionTab) innerPages.getPage(CRON_RULE_TAB)).updateRule(entity.getRule());
			((RuleDateTab) innerPages.getPage(DATE_RULE_TAB)).loadFieldsFromRule();
		}

		super.onShow();

		Roma.setFeature(entity, "name", ViewFieldFeatures.ENABLED, getMode() == MODE_CREATE);
	}

	@Override
	public void save() {
		entity.setRule(((RuleCronExpressionTab) innerPages.getPage(CRON_RULE_TAB)).getRule());

		switch (getMode()) {
		case CRUDWorkingMode.MODE_CREATE:
			Roma.aspect(SchedulerAspect.class).schedule(getEntity());
			break;

		case CRUDWorkingMode.MODE_UPDATE:
			Roma.aspect(SchedulerAspect.class).unSchedule(getEntity());
			Roma.aspect(SchedulerAspect.class).schedule(getEntity());
			break;
		}

		// MAKE THE BIND
		super.save();
	}

	public void validate() {
	}

	public ContainerPage getInnerPages() {
		return innerPages;
	}

	protected void saveEntity() {
		switch (getMode()) {
		case MODE_CREATE:
			setEntity(repository.create(getEntity(), PersistenceAspect.STRATEGY_DETACHING));
			if (getSourceObject() != null) {
				SchemaHelper.insertElements(getSourceField(), getSourceObject(), new Object[] { entity });
				if (getSourceObject() instanceof Refreshable) {
					try {
						((Refreshable) getSourceObject()).refresh();
					} catch (RefreshFailedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			break;

		case MODE_UPDATE:
			setEntity(repository.update(getEntity()));
			break;

		case MODE_READ:
			return;
		}
	}
}
