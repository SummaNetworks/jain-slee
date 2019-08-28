/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * 
 */
package org.mobicents.slee.container.eventrouter;

import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.container.activity.ActivityContextHandle;

import java.util.Date;

/**
 * Maps {@link EventRouterExecutor} to Activity Contexts.
 * 
 * @author martins
 * 
 */
public interface EventRouterExecutorMapper {

	/**
	 * 
	 * @param executors
	 */
	void setExecutors(EventRouterExecutor[] executors);


	/**
	 *
	 * @param executors
	 */
	void setMapExecutors(EventRouterExecutor[] executors);

	/**
	 *
	 * @param executors
	 */
	void setDiameterExecutors(EventRouterExecutor[] executors);

	/**
	 * Retrieves the executor for the activity context with the specified
	 * handle.
	 * 
	 * @param activityContextHandle
	 * @return
	 */
	EventRouterExecutor getExecutor(ActivityContextHandle activityContextHandle);


	/**
	 * Retrieves the executor for the activity context with the specified
	 * handle.
	 *
	 * @param activityContextHandle
	 * @return
	 */
	EventRouterExecutor getMapExecutor(ActivityContextHandle activityContextHandle);

	/**
	 * Retrieves the executor for the activity context with the specified
	 * handle.
	 *
	 * @param activityContextHandle
	 * @return
	 */
	EventRouterExecutor getDiameterExecutor(ActivityContextHandle activityContextHandle);

	/**
	 * Returns an executor to the EventRouterExecutor
	 * @param executorNumber the number of the returned executor
	 * @param assignationDate the date in which the executor was assigned to the task
	 * @param  activityContextHandle the activity that is handling the event
	 */
	void returnExecutor(Integer executorNumber, Date assignationDate, ActivityContextHandle activityContextHandle);

}