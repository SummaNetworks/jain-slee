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
package org.mobicents.slee.runtime.eventrouter.mapping;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mobicents.slee.container.activity.ActivityContextHandle;
import org.mobicents.slee.container.eventrouter.EventRouterExecutor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Round Robin {@link EventRouterExecutor} to {@link ActivityContextHandle} mapping.
 * @author martins
 *
 */
public class BlockingQueueEventRouterExecutorMapper extends AbstractEventRouterExecutorMapper {
	private static final Logger logger = LogManager.getLogger(BlockingQueueEventRouterExecutorMapper.class);

	protected Map<Integer, EventRouterExecutor> allExecutors = new HashMap<Integer, EventRouterExecutor>();
	protected BlockingQueue<Integer> freeExecutors = new LinkedBlockingQueue<Integer>();

	/**
	 * index use to iterate the executor's array
	 */
	protected AtomicInteger index = null;
	protected Integer executorsSize = 0;
	
	/* (non-Javadoc)
	 * @see org.mobicents.slee.runtime.eventrouter.mapping.AbstractEventRouterExecutorMapper#setExecutors(org.mobicents.slee.runtime.eventrouter.EventRouterExecutor[])
	 */
	@Override
	public void setExecutors(EventRouterExecutor[] executors) {
		super.setExecutors(executors);

		for (EventRouterExecutor executor : executors) {
			allExecutors.put(executor.getNumber(), executor);
			//Adding numbers of the executors, the idea is to add and remove numbers from this array to see if they are free or not
			freeExecutors.offer(executor.getNumber());
		}

		executorsSize = allExecutors.size();
		index = new AtomicInteger(0);
	}

	/* (non-Javadoc)
	 * @see org.mobicents.slee.runtime.eventrouter.mapping.AbstractEventRouterExecutorMapper#getExecutor(org.mobicents.slee.runtime.activity.ActivityContextHandle)
	 */
	@Override
	public EventRouterExecutor getExecutor(
			ActivityContextHandle activityContextHandle) {
		try {
			EventRouterExecutor.executorLogger.debug("BlockingQueueEventRouterExecutorMapper::getExecutor::ach:: "
					+ activityContextHandle.getActivityHandle() + "::TAKE::" + freeExecutors.size());

			Integer numberOfFreeExecutor = freeExecutors.poll(150, TimeUnit.MILLISECONDS);
			EventRouterExecutor headOfQueue = null;

			if (numberOfFreeExecutor == null){
				Integer value = index.incrementAndGet() % executorsSize;
				headOfQueue = allExecutors.get(value);
				logger.trace("getExecutor(): there are no FREE executors, reusing number " + value);
			} else {
				logger.trace("getExecutor(): using FREE executor " + numberOfFreeExecutor);
				headOfQueue = allExecutors.get(numberOfFreeExecutor);
			}

			EventRouterExecutor.executorLogger.debug("BlockingQueueEventRouterExecutorMapper::getExecutor::" +
					headOfQueue.getNumber() + "::ach::" + activityContextHandle.getActivityHandle());
			headOfQueue.setAssignationDate(new Date());
			return headOfQueue;
		} catch (InterruptedException e){
			logger.error("BlockingQueueEventRouterExecutorMapper::getExecutor::InterruptedException", e);

			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.mobicents.slee.runtime.eventrouter.mapping.AbstractEventRouterExecutorMapper#getExecutor(org.mobicents.slee.runtime.activity.ActivityContextHandle)
	 */
	@Override
	public EventRouterExecutor getMapExecutor(
			ActivityContextHandle activityContextHandle) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mobicents.slee.runtime.eventrouter.mapping.AbstractEventRouterExecutorMapper#getExecutor(org.mobicents.slee.runtime.activity.ActivityContextHandle)
	 */
	@Override
	public EventRouterExecutor getDiameterExecutor(
			ActivityContextHandle activityContextHandle) {
		return null;
	}

	public void returnExecutor(Integer executorNumber, Date assignationDate, ActivityContextHandle ach){
		EventRouterExecutor.executorLogger.debug("BlockingQueueEventRouterExecutorMapper::returnExecutor::"
				+ executorNumber + "::ENTER::ach::" + ach.getActivityHandle());
		long totalTime = new Date().getTime() - assignationDate.getTime();
		if (!freeExecutors.contains(executorNumber)) {
			Boolean offerExecutor = freeExecutors.offer(executorNumber);
			EventRouterExecutor.executorLogger.debug("BlockingQueueEventRouterExecutorMapper::returnExecutor::" +
					executorNumber + "::offerResult::" + offerExecutor + "::useTime::" + totalTime + "::ach::" + ach.getActivityHandle());
		} else {
			logger.trace("returnExecutor(): EXECUTOR already in LIST, nothing to do.");
		}
	}
}