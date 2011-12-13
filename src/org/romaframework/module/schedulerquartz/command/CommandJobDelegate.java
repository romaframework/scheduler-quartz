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

package org.romaframework.module.schedulerquartz.command;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.romaframework.core.Roma;
import org.romaframework.core.command.Command;
import org.romaframework.core.command.CommandContext;

/**
 * 
 * @author Paolo Spizzirri (paolo.spizzirri@assetdata.it)
 * 
 */
public class CommandJobDelegate implements Job {

	private static CommandJobDelegate	instance						= new CommandJobDelegate();

	public static final String				PAR_COMMAND					= "command";
	public static final String				PAR_CONTEXT					= "context";
	public static final String				PAR_QUARTZ_CONTEXT	= "quartzContext";

	/**
	 * Delegates the execution to the Command implementation.
	 */
	public void execute(JobExecutionContext iContext) throws JobExecutionException {
		try {
			Command command = (Command) iContext.getJobDetail().getJobDataMap().get(PAR_COMMAND);

			if (command == null)
				return;

			// SET QUARTZ CONTEXT AS PARAMETER TO BEING USED BY COMMAND
			CommandContext ctx = (CommandContext) iContext.getJobDetail().getJobDataMap().get(PAR_CONTEXT);
			if (ctx == null)
				ctx = new CommandContext();
			ctx.setParameter(PAR_QUARTZ_CONTEXT, iContext);
			try {
				Roma.context().create();
				command.execute(ctx);
			} finally {
				Roma.context().destroy();
			}
			ctx.setParameter(PAR_QUARTZ_CONTEXT, null);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

	/**
	 * Singleton
	 * 
	 * @return Always the same CommandJobDelegate instance
	 */
	public CommandJobDelegate getInstance() {
		return instance;
	}
}
