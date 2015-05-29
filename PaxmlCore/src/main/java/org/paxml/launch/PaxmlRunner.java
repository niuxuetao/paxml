/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.launch;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.PaxmlResource;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.ITagLibrary;
import org.paxml.tag.plan.PlanEntityFactory.Plan;
import org.paxml.tag.plan.PlanTagLibrary;
import org.paxml.util.PaxmlUtils;

/**
 * This is a convenience tool for running paxml stuff.
 * 
 * @author Xuetao Niu
 * 
 */
public class PaxmlRunner {

	private static final Log log = LogFactory.getLog(PaxmlRunner.class);

	public static final int DEFAULT_CONCURRENCY = 4;

	public static Object run(String paxmlOrPlanFileName, Map<String, Object> params, String baseDir, Class<? extends ITagLibrary>... tagLibs) {
		StaticConfig config = new StaticConfig();
		for (Class<? extends ITagLibrary> lib : tagLibs) {
			config.getTagLibs().add(lib);
		}
		config.getResources().addAll(LaunchModelBuilder.findResources(baseDir, null, null));
		return run(paxmlOrPlanFileName, params, config);
	}

	/**
	 * Run either a plan file or a paxml file from the given resource set.
	 * 
	 * @param paxmlOrPlanFileName
	 *            resource name with file extension to run
	 * @param params
	 *            the parameters, can be null
	 * 
	 * @param config
	 *            the config containing resource set
	 * 
	 */
	public static Object run(String paxmlOrPlanFileName, Map<String, Object> params, StaticConfig config) {

		Context.cleanCurrentThreadContext();

		PaxmlResource r = null;
		for (PaxmlResource res : config.getResources()) {
			if (paxmlOrPlanFileName.equals(res.getName())) {
				r = res;
				break;
			}
		}
		if (r == null) {
			throw new PaxmlRuntimeException("Paxml file not found: " + paxmlOrPlanFileName);
		}

		Properties properties = new Properties();
		properties.putAll(System.getProperties());
		final long execId = PaxmlUtils.getNextExecutionId();
		Paxml paxml = new Paxml(LaunchModel.generateNextPid(), execId);
		paxml.addStaticConfig(config);
		paxml.getParser().addTagLibrary(new PlanTagLibrary(), false);

		Context context = new Context(new Context(properties, paxml.getProcessId()));
		if (params != null) {
			context.setConsts(params, null, false);
		}
		IEntity entity = paxml.getParser().parse(r, true, null);
		if (entity instanceof Plan) {
			// a plan file
			if (log.isInfoEnabled()) {
				log.info("Starting plan file execution: " + entity.getResource().getPath());
			}
			LaunchModel model = new LaunchModel();
			model.getConfig().add(config);

			model.setName(((Plan) entity).getTagName());
			model.setPlanEntity((Plan) entity);
			model.setResource(r.getSpringResource());

			Properties props = new Properties();
			props.put(LaunchModel.class, model);
			Object res = paxml.execute(entity, System.getProperties(), props);
			run(model, execId);
			if (log.isInfoEnabled()) {
				log.info("Finished executing plan file: " + entity.getResource().getPath());
			}
			return res;
		} else {
			// a paxml file
			logExecution(r, true);
			try {
				return paxml.execute(entity, context, true, true);
			} finally {
				logExecution(r, false);
			}
		}

	}

	private final static void logExecution(PaxmlResource res, boolean trueStartFalseEnd) {
		if (log.isInfoEnabled()) {
			log.info((trueStartFalseEnd ? "Starting" : "Finished") + " Paxml execution: " + res.getPath());
		}

	}

	/**
	 * Run the computed launch model with thread pool. It will run with default
	 * 4 threads if not specifically specified in the launch model.
	 * 
	 * @param model
	 *            the model containing the launch points
	 */
	public static void run(LaunchModel model, long executionId) {

		List<LaunchPoint> points = model.getLaunchPoints(false, executionId);
		if (log.isInfoEnabled()) {
			log.info("Found " + points.size() + " Paxml files to execute based on plan file: " + model.getPlanEntity().getResource().getPath());
		}
		if (points.isEmpty()) {
			return;
		}
		final int poolSize = model.getConcurrency() <= 0 ? Math.min(DEFAULT_CONCURRENCY, points.size()) : model.getConcurrency();
		ExecutorService pool = Executors.newFixedThreadPool(poolSize);
		for (final LaunchPoint point : points) {
			pool.execute(new Runnable() {

				@Override
				public void run() {
					try {
						Context.cleanCurrentThreadContext();

						logExecution(point.getResource(), true);

						point.execute();
					} catch (Throwable t) {
						if (log.isErrorEnabled()) {
							log.error(findMessage(t), t);
						}
					} finally {
						logExecution(point.getResource(), false);
					}
				}

			});
		}

		try {
			pool.shutdown();
			// wait forever in a loop
			while (!pool.awaitTermination(1, TimeUnit.MINUTES)) {
				if (log.isDebugEnabled()) {
					log.debug("Waiting for all executors to finish...");
				}
			}

		} catch (InterruptedException e) {
			throw new PaxmlRuntimeException("Cannot wait for all executors to finish", e);
		} finally {
			pool.shutdownNow();
		}

	}

	private static String findMessage(Throwable t) {
		String msg = t.getMessage();
		for (; t != null && StringUtils.isBlank(msg); t = t.getCause()) {

		}
		return msg;
	}
}
