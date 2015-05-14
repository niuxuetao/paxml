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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.launch.PaxmlRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
/**
 * The command line application.
 * 
 * @author Xuetao Niu
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
	public static final String BASE_DIR = "paxml.dir";
	private static final Log log = LogFactory.getLog(Application.class);

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			log.error("No Paxml file name is given.");
			return;
		}
		final String fn = args[0];
		
		String baseDir = System.getProperty(BASE_DIR);
		if (StringUtils.isBlank(baseDir)) {
			baseDir = "";
		}

		if (log.isInfoEnabled()) {
			log.info("Searching for Paxml " + fn + " from dir: " + baseDir);
		}
		
		PaxmlRunner.run(fn, null, baseDir, org.paxml.selenium.rc.TagLibrary.class);

	}

}
