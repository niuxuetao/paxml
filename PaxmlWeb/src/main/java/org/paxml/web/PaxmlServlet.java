package org.paxml.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.paxml.core.EntityFactoryRegistry;
import org.paxml.core.PaxmlResource;
import org.paxml.launch.LaunchModelBuilder;
import org.paxml.launch.PaxmlRunner;
import org.paxml.launch.StaticConfig;

/**
 * Servlet that renders paxml execution result as HTML response or serve static
 * content if not found as paxml.
 * 
 * @author Xuetao Niu
 * 
 */
public class PaxmlServlet extends GenericServlet {

	public static final String CONFIG_TAG_LIB = "tagLibrary";
	public static final String CONFIG_PAXML_DIR = "paxmlDir";
	public static final String CONFIG_RES_DIR = "resourceDir";
	public static final String CONFIG_VALUE_SEP = "," + File.pathSeparator;

	static {
		EntityFactoryRegistry.getDefaultRegistry().register(new PageFactory());
	}

	private final StaticConfig config = new StaticConfig();
	private final Set<File> resources = new LinkedHashSet<File>();

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse rsp = (HttpServletResponse) response;
		String uri = StringUtils.substringBefore(req.getRequestURI(), "?");
		String fn = FilenameUtils.getName(uri);
		String bn = FilenameUtils.getBaseName(fn);

		boolean found = false;
		for (PaxmlResource res : config.getResources()) {
			if (bn.equals(res.getName())) {
				found = true;
				break;
			}
		}
		if (found) {
			Object result = PaxmlRunner.run(bn, req.getParameterMap(), config);
			if (result != null) {				
				rsp.setHeader("Content-Type", "text/html; charset=UTF-8");
				rsp.getWriter().print(new HtmlBuilder(result).build());
			}
			return;
		}

		for (File dir : resources) {
			File f = new File(dir, uri);
			if (f.isFile()) {
				FileInputStream in = new FileInputStream(f);
				try {
					IOUtils.copy(in, rsp.getOutputStream());
					return;
				} finally {
					IOUtils.closeQuietly(in);
				}
			}
		}
		rsp.sendError(404);
	}

	@Override
	public void init(ServletConfig c) throws ServletException {
		try {
			String tagLibs = c.getInitParameter(CONFIG_TAG_LIB);
			if (tagLibs != null) {
				for (String lib : StringUtils.split(tagLibs, CONFIG_VALUE_SEP)) {
					Class clazz = Class.forName(lib.trim());
					config.getTagLibs().add(clazz);
				}
			}
			String paxmlDir = c.getInitParameter(CONFIG_PAXML_DIR);
			if (paxmlDir != null) {
				for (String dir : StringUtils.split(paxmlDir, CONFIG_VALUE_SEP)) {
					dir = dir.trim();
					if (!dir.isEmpty()) {
						config.getResources().addAll(LaunchModelBuilder.findResources(dir, null, null));
					}
				}
			} else {
				config.getResources().addAll(LaunchModelBuilder.findResources(new File(".").getAbsolutePath(), null, null));
			}
			String resDir = c.getInitParameter(CONFIG_PAXML_DIR);
			if (resDir != null) {
				for (String dir : StringUtils.split(resDir, CONFIG_VALUE_SEP)) {
					dir = dir.trim();
					if (!dir.isEmpty()) {
						resources.add(new File(dir));
					}
				}
			} else {
				resources.add(new File("."));
			}
		} catch (Exception e) {
			throw new ServletException("Cannot init paxml servlet", e);
		}
	}

}
