/**
 * This file is part of PaxmlExecutable.
 *
 * PaxmlExecutable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlExecutable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlExecutable.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.PaxmlResource;
import org.paxml.launch.LaunchModelBuilder;
import org.paxml.launch.PaxmlRunner;
import org.paxml.launch.StaticConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
	public static final String BASE_DIR = "paxml.basedir";
	private static final Log log = LogFactory.getLog(Application.class);

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			log.error("No Paxml file name is given.");
			return;
		}
		String fn = args[0];
		File file = new File(fn);
		if (file.isFile()) {
			fn = FilenameUtils.getBaseName(fn);
		}
		String baseDir = System.getProperty(BASE_DIR);
		if (StringUtils.isBlank(baseDir)) {
			baseDir = new File("").getAbsolutePath();
		}

		if (log.isInfoEnabled()) {
			log.info("Launching Paxml " + fn + " from base dir: " + baseDir);
		}
		StaticConfig config = new StaticConfig();
		// add additional projects tag libs
		config.getTagLibs().add(org.paxml.selenium.rc.TagLibrary.class);
		// find resources
		Set<String> includes = new HashSet<String>(1);
		includes.add("**/*.*");
		String fakeFile = new File(baseDir, "fake.file").getAbsolutePath();
		Set<PaxmlResource> res = LaunchModelBuilder.findResources(fakeFile, includes, Collections.EMPTY_SET);

		config.getResources().addAll(res);

		PaxmlRunner.run(fn, config);

		if (log.isInfoEnabled()) {
			log.info("Paxml execution finished: " + fn);
		}
	}

}
