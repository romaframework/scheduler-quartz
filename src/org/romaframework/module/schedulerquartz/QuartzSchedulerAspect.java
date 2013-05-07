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

package org.romaframework.module.schedulerquartz;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.romaframework.aspect.persistence.PersistenceAspect;
import org.romaframework.aspect.persistence.QueryByFilter;
import org.romaframework.aspect.scheduler.SchedulerAspect;
import org.romaframework.aspect.scheduler.SchedulerAspectAbstract;
import org.romaframework.aspect.scheduler.SchedulerEvent;
import org.romaframework.aspect.view.ViewAspect;
import org.romaframework.core.Roma;
import org.romaframework.core.Utility;
import org.romaframework.core.command.Command;
import org.romaframework.core.command.CommandContext;
import org.romaframework.core.schema.SchemaClassResolver;
import org.romaframework.module.schedulerquartz.command.CommandJobDelegate;
import org.romaframework.module.schedulerquartz.domain.QuartzSchedulerEvent;

public class QuartzSchedulerAspect extends SchedulerAspectAbstract implements JobListener {

	private static final String			THREAD_POOL_THREAD_COUNT_DEFAULT	= "3";
	private static final String			THREAD_POOL_THREAD_COUNT_PROPERTY	= "org.quartz.threadPool.threadCount";
	protected static final Log			log																= LogFactory.getLog(QuartzSchedulerAspect.class);
	protected org.quartz.Scheduler	scheduler;
	private Map<String, Command>		jobsImplementation								= null;

	public QuartzSchedulerAspect() {
	}

	@Override
	public void shutdown() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			log.error(e);
		}
	}

	public void run() {
		// REGISTER THE APPLICATION DOMAIN
		Roma.component(SchemaClassResolver.class).addDomainPackage(QuartzSchedulerAspect.class.getPackage().getName());

		// REGISTER THE APPLICATION DOMAIN + VIEW
		Roma.component(SchemaClassResolver.class).addDomainPackage(
				QuartzSchedulerAspect.class.getPackage().getName() + Utility.PACKAGE_SEPARATOR + ViewAspect.ASPECT_NAME);

		SchedulerFactory factory = new StdSchedulerFactory();
		try {
			String threadPoolSize = System.getProperty(THREAD_POOL_THREAD_COUNT_PROPERTY);
			if (threadPoolSize == null)
				// SET THE DEFAULT THREAD POOL SIZE
				System.setProperty(THREAD_POOL_THREAD_COUNT_PROPERTY, THREAD_POOL_THREAD_COUNT_DEFAULT);

			scheduler = factory.getScheduler();
			scheduler.getListenerManager().addJobListener(this);
			scheduler.start();
			log.info("Scheduler succesfully Started");
			registerCustomCalendars();
		} catch (SchedulerException sce) {
			throw new org.romaframework.aspect.scheduler.SchedulerException("Scheduler engene not started", sce);
		}

		List<QuartzSchedulerEvent> events = loadStoredEvents();
		for (QuartzSchedulerEvent event : events) {
			try {
				schedule(event);
				log.info("Scheduled Job: " + event.getName());
			} catch (org.romaframework.aspect.scheduler.SchedulerException rsce) {
				log.error("Error on scheduling job: " + event.getName(), rsce);
			}
		}
	}

	public void pauseJob(SchedulerEvent event) throws org.romaframework.aspect.scheduler.SchedulerException {
		String jobName = null;
		try {
			log.info("Pausing Job: " + event.getName());
			scheduler.pauseJob(new JobKey(event.getName()));
		} catch (SchedulerException sce) {
			log.error("Error on pausing Job: " + jobName);
			throw new org.romaframework.aspect.scheduler.SchedulerException(sce);
		}
	}

	public void unpauseJob(SchedulerEvent iEvent) throws org.romaframework.aspect.scheduler.SchedulerException {
		try {

			log.info("Pausing Job: " + iEvent.getName());
			scheduler.resumeJob(new JobKey(iEvent.getName()));
		} catch (SchedulerException sce) {
			log.error("Error on pausing Job: " + iEvent.getName());
			throw new org.romaframework.aspect.scheduler.SchedulerException(sce);
		}

	}

	public void executeNow(SchedulerEvent iEvent) throws org.romaframework.aspect.scheduler.SchedulerException {
		try {
			scheduler.triggerJob(new JobKey(iEvent.getName()));
		} catch (SchedulerException sce) {
			throw new org.romaframework.aspect.scheduler.SchedulerException("Errore nell'esecuzione del Job selezionato", sce);
		}
	}

	public SchedulerEvent createEvent() throws org.romaframework.aspect.scheduler.SchedulerException {
		return new QuartzSchedulerEvent();
	}

	public Date schedule(SchedulerEvent iEvent) throws org.romaframework.aspect.scheduler.SchedulerException {
		try {
			JobDetail quartzJob = JobBuilder.newJob(CommandJobDelegate.class).withIdentity(iEvent.getName()).build();

			// quartzJob.addJobListener(this.getClass().getSimpleName());
			Command command = getJobImplementation(iEvent.getImplementation());
			if (command == null)
				throw new org.romaframework.aspect.scheduler.SchedulerException("Command '" + iEvent.getImplementation()
						+ "' not registered.");

			quartzJob.getJobDataMap().put(CommandJobDelegate.PAR_COMMAND, command);
			quartzJob.getJobDataMap().put(CommandJobDelegate.PAR_CONTEXT, new CommandContext(iEvent.getContext()));

			Trigger quartzTrigger = createTrigger(iEvent);

			scheduler.scheduleJob(quartzJob, quartzTrigger);
			log.info("[QuartzSchedulerAspect.schedule] Scheduled event '" + quartzTrigger + "'. Next fire time: "
					+ quartzTrigger.getNextFireTime());

			return quartzTrigger.getNextFireTime();
		} catch (Exception ex) {
			throw new org.romaframework.aspect.scheduler.SchedulerException("Error on Job Scheduling: " + iEvent.getName(), ex);
		}
	}

	protected Trigger createTrigger(SchedulerEvent iEvent) throws ParseException {
		Trigger quartzTrigger;
		if (iEvent.getRule() == null || iEvent.getRule().equals(""))
			quartzTrigger = TriggerBuilder.newTrigger().withIdentity(iEvent.getName()).startAt(iEvent.getStartTime()).build();
		else
			quartzTrigger = TriggerBuilder.newTrigger().withIdentity(iEvent.getName()).startAt(iEvent.getStartTime())
					.withSchedule(CronScheduleBuilder.cronSchedule(iEvent.getRule())).build();
		return quartzTrigger;
	}

	public void unSchedule(SchedulerEvent iEvent) throws org.romaframework.aspect.scheduler.SchedulerException {
		try {
			scheduler.deleteJob(new JobKey(iEvent.getName(), null));
		} catch (Exception ex) {
			throw new org.romaframework.aspect.scheduler.SchedulerException("Errore durante l'eliminazione del Job: " + iEvent.getName(),
					ex);
		}
	}

	private static List<QuartzSchedulerEvent> loadStoredEvents() {
		PersistenceAspect db = Roma.component("NoTxPersistenceAspect");
		QueryByFilter filter = new QueryByFilter(QuartzSchedulerEvent.class);
		filter.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		return db.query(filter);
	}

	private static QuartzSchedulerEvent loadOneEvent(String name) {
		PersistenceAspect db = Roma.component("NoTxPersistenceAspect");
		QueryByFilter filter = new QueryByFilter(QuartzSchedulerEvent.class);
		filter.setStrategy(PersistenceAspect.STRATEGY_DETACHING);
		filter.addItem("name", QueryByFilter.FIELD_EQUALS, name);
		return db.queryOne(filter);
	}

	public Date getNextEventExcecution(String name) {
		try {
			Trigger trigger = scheduler.getTrigger(new TriggerKey(name));
			if (trigger != null)
				return trigger.getNextFireTime();
		} catch (Exception ex) {
			log.error("Error on retrieveng trigger status", ex);
		}

		return null;
	}

	public String getLastEventExcecution(String name) {
		String NOT_YET_EXECUTED = "Not yet exexcuted";
		String ERROR = SchedulerAspect.EVENT_STATUS_ERROR;

		String lastFire = NOT_YET_EXECUTED;
		DateFormat dateFormatter = Roma.i18n().getDateTimeFormat();
		try {
			Trigger trigger = scheduler.getTrigger(new TriggerKey(name));
			if (trigger != null) {
				Date date = trigger.getPreviousFireTime();
				if (date != null)
					lastFire = dateFormatter.format(date);
			}
		} catch (Exception ex) {
			log.error("Errror on retrieving trigger State", ex);
			lastFire = ERROR;
		}

		return lastFire;
	}

	public void setJobsImplementation(Map<String, Command> jobsImplementation) {
		this.jobsImplementation = jobsImplementation;
	}

	public Map<String, Command> getJobsImplementation() {
		return jobsImplementation;
	}

	public List<String> getImplementationsList() {
		if (jobsImplementation == null)
			return null;
		List<String> implementationsList = new ArrayList<String>();
		for (String jobImplemetation : jobsImplementation.keySet())
			implementationsList.add(jobImplemetation);
		return implementationsList;
	}

	public Command getJobImplementation(String implementation) {
		return jobsImplementation.get(implementation);
	}

	public String getEventState(String name) {
		String status = null;
		try {
			status = getEventStateTrigger(name);
			List<String> executingJobs = getCurrentlyExecutingJobs();
			if (executingJobs != null && executingJobs.size() > 0 && executingJobs.contains(name))
				status = SchedulerAspect.EVENT_STATUS_EXECUTING;

		} catch (Exception ex) {
		}

		return status;

	}

	private String getEventStateTrigger(String name) throws org.romaframework.aspect.scheduler.SchedulerException {
		String status = null;
		TriggerState state = null;
		try {
			state = scheduler.getTriggerState(new TriggerKey(name));
		} catch (SchedulerException sce) {
			log.error("Error on retrieving trigger status", sce);
		}
		switch (state) {
		case NONE:
			status = SchedulerAspect.EVENT_STATUS_NOT_SCHEDULED;
			break;

		case NORMAL:
			status = SchedulerAspect.EVENT_STATUS_NORMAL;
			break;

		case PAUSED:
			status = SchedulerAspect.EVENT_STATUS_PAUSED;
			break;

		case COMPLETE:
			status = SchedulerAspect.EVENT_STATUS_COMPLETED;
			break;

		case ERROR:
			status = SchedulerAspect.EVENT_STATUS_ERROR;
			break;

		case BLOCKED:
			status = SchedulerAspect.EVENT_STATUS_BLOCKED;
			break;

		default:
			break;
		}

		return status;

	}

	@SuppressWarnings("unchecked")
	private List<String> getCurrentlyExecutingJobs() {
		List<String> executingJobs = null;

		try {
			List<JobExecutionContext> executionContexts = scheduler.getCurrentlyExecutingJobs();
			if (executionContexts != null && executionContexts.size() > 0) {
				executingJobs = new ArrayList<String>();
				for (JobExecutionContext executionContext : executionContexts)
					executingJobs.add(executionContext.getJobDetail().getKey().getName());
			}
		} catch (SchedulerException sce) {
			log.error("Error on retrieving EVENT_STATUS_EXECUTING jobs from scheduler", sce);
		}

		return executingJobs;

	}

	public Object getUnderlyingComponent() {
		return scheduler;
	}

	/**
	 * Register custom calendar in quartz scheduler.
	 */
	protected void registerCustomCalendars() {
	}

	
	public String getName() {
		return this.getClass().getSimpleName();
	}

	public void jobExecutionVetoed(JobExecutionContext arg0) {
		// TODO Auto-generated method stub

	}

	public void jobToBeExecuted(JobExecutionContext arg0) {
		// TODO Auto-generated method stub

	}

	public void jobWasExecuted(JobExecutionContext arg0, JobExecutionException arg1) {
		QuartzSchedulerEvent evt = loadOneEvent(arg0.getJobDetail().getKey().getName());
		if (evt != null) {
			evt.setStartTime(arg0.getNextFireTime());
			Roma.persistence().updateObject(evt);
		}
	}
}
