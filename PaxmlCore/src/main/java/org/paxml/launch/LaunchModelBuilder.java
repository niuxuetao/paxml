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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.paxml.core.FileSystemResource;
import org.paxml.core.IEntity;
import org.paxml.core.IEntityExecutionListener;
import org.paxml.core.IExecutionListener;
import org.paxml.core.ITagExecutionListener;
import org.paxml.core.PaxmlParseException;
import org.paxml.core.PaxmlResource;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.core.ResourceLocator;
import org.paxml.tag.ITagLibrary;
import org.paxml.tag.plan.PlanEntityFactory.Plan;
import org.paxml.tag.plan.PlanTagLibrary;
import org.paxml.util.AxiomUtils;
import org.paxml.util.Elements;
import org.paxml.util.PaxmlUtils;
import org.paxml.util.ReflectUtils;
import org.springframework.core.io.Resource;

/**
 * Build a launch model from xml.
 * 
 * @author Xuetao Niu
 * 
 */
public class LaunchModelBuilder {

	/**
	 * The resource matcher with include patterns and exclude patterns.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	private static final class ResourceMatcher {
		private final Set<String> include;
		private final Set<String> exclude;

		private ResourceMatcher(final Set<String> include, final Set<String> exclude) {
			super();
			this.include = include;
			this.exclude = exclude;
		}

	}

	private static final String NAME = "name";

	private Resource planFile;
	private LaunchModel model;

	/**
	 * Build from spring resource that points to xml.
	 * 
	 * @param res
	 *            the resource
	 * @param props
	 *            the initial properties to execute the plan file with
	 * @return the launch model, never null.
	 */
	public LaunchModel build(Resource res, Properties props) {

		InputStream in = null;
		try {
			model = new LaunchModel();
			planFile = res;

			in = res.getInputStream();

			model.setResource(res);

			OMElement root = AxiomUtils.getRootElement(in);

			// build the primitive parts
			buildLibraries(root, true);
			buildListeners(root, true);
			buildResources(root, true);

			model.setName(AxiomUtils.getAttribute(root, NAME));

			model.setPlanEntity(processPlan(root, props));

			return model;
		} catch (IOException e) {
			throw new PaxmlRuntimeException("Cannot open stream from resource: " + res, e);
		} finally {
			planFile = null;
			model = null;

			IOUtils.closeQuietly(in);

		}
	}

	private Plan processPlan(OMElement root, Properties props) {
		final Paxml paxml = new Paxml(0, PaxmlUtils.getNextExecutionId());
		paxml.addStaticConfig(model.getConfig());

		// add the plan tag lib temporarily
		paxml.getParser().addTagLibrary(new PlanTagLibrary(), false);

		final PaxmlResource planFileResource;
		try {
			planFileResource = PaxmlResource.createFromPath(planFile.getURI().toString());
		} catch (IOException e) {
			throw new PaxmlRuntimeException(e);
		}

		// add resource for execution
		paxml.addResources(planFileResource);
		IEntity entity = paxml.getParser().parseXml(root, planFileResource, null);
		if (entity == null) {
			throw new PaxmlRuntimeException("Internal error: should not be null!");
		}
		props.put(LaunchModel.class, model);
		paxml.execute(entity, System.getProperties(), props);
		Plan plan = (Plan) entity;

		return plan;
	}

	private void buildListeners(OMElement ele, boolean detach) {
		for (OMElement child : AxiomUtils.getElements(ele, "listener")) {
			String className = child.getText().trim();
			if (StringUtils.isNotBlank(className)) {
				Class<?> clazz = ReflectUtils.loadClassStrict(className, null);
				if (ReflectUtils.isImplementingClass(clazz, IExecutionListener.class, true)) {
					model.getConfig().getExecutionListeners().add((Class<? extends IExecutionListener>) clazz);
				} else if (ReflectUtils.isImplementingClass(clazz, IEntityExecutionListener.class, true)) {
					model.getConfig().getEntityListeners().add((Class<? extends IEntityExecutionListener>) clazz);
				} else if (ReflectUtils.isImplementingClass(clazz, ITagExecutionListener.class, true)) {
					model.getConfig().getTagListeners().add((Class<? extends ITagExecutionListener>) clazz);
				} else {
					throw new PaxmlParseException("Unknown listener type: " + clazz.getName());
				}
			}
			if (detach) {
				child.detach();
			}
		}
	}

	private void buildLibraries(OMElement ele, boolean detach) {
		for (OMElement child : AxiomUtils.getElements(ele, "library")) {
			String className = child.getText().trim();
			if (StringUtils.isNotBlank(className)) {
				Class<? extends ITagLibrary> clazz = (Class<? extends ITagLibrary>) ReflectUtils.loadClassStrict(className, null);

				model.getConfig().getTagLibs().add(clazz);
			}
			if (detach) {
				child.detach();
			}
		}

	}

	public static Set<PaxmlResource> findResources(String base, Set<String> includes, Set<String> excludes) {
		if (includes == null) {
			includes = new HashSet<String>(1);
			includes.add("**/*.*");
		}
		if (excludes == null) {
			excludes = Collections.EMPTY_SET;
		}
		if (base == null) {
			base = ""; // the current working dir
		}
		File f = new File(base);
		if (f.isDirectory()) {
			f = new File(f, "fake.file");
		}
		Resource baseRes = new FileSystemResource(f).getSpringResource();
		Set<PaxmlResource> include = new LinkedHashSet<PaxmlResource>(0);
		Set<PaxmlResource> exclude = new LinkedHashSet<PaxmlResource>(0);
		ResourceMatcher matcher = new ResourceMatcher(includes, excludes);
		for (String pattern : matcher.include) {
			include.addAll(ResourceLocator.findResources(pattern, baseRes));
		}
		for (String pattern : matcher.exclude) {
			exclude.addAll(ResourceLocator.findResources(pattern, baseRes));
		}
		include.removeAll(exclude);

		return include;
	}

	private void buildResources(OMElement root, boolean detach) {
		for (OMElement ele : AxiomUtils.getElements(root, "resource")) {
			ResourceMatcher matcher = parseIncludeAndExclude(ele);

			Set<PaxmlResource> include = new LinkedHashSet<PaxmlResource>(0);
			Set<PaxmlResource> exclude = new LinkedHashSet<PaxmlResource>(0);
			for (String pattern : matcher.include) {
				include.addAll(ResourceLocator.findResources(pattern, planFile));
			}
			for (String pattern : matcher.exclude) {
				exclude.addAll(ResourceLocator.findResources(pattern, planFile));
			}
			include.removeAll(exclude);

			final PaxmlResource planFileResource;
			try {
				planFileResource = PaxmlResource.createFromPath(planFile.getURI().toString());
			} catch (IOException e) {
				throw new PaxmlRuntimeException(e);
			}

			include.remove(planFileResource);

			model.getConfig().getResources().addAll(include);

			if (detach) {
				ele.detach();
			}
		}
	}

	private ResourceMatcher parseIncludeAndExclude(OMElement ele) {
		Set<String> include = new LinkedHashSet<String>(0);
		Set<String> exclude = new LinkedHashSet<String>(0);
		for (OMElement res : new Elements(ele)) {
			final String tagName = res.getLocalName();
			String pattern = res.getText().trim();
			if (StringUtils.isNotBlank(pattern)) {
				pattern = pattern.replace('\\', '/');
				if ("include".equals(tagName)) {
					include.add(pattern);
				} else if ("exclude".equals(tagName)) {
					exclude.add(pattern);
				}
			}
		}
		return new ResourceMatcher(include, exclude);

	}
}
