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

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.InMemoryResource;
import org.paxml.tag.plan.ScenarioTag;

public class PaxmlRunner {

	private static final Log log = LogFactory.getLog(PaxmlRunner.class);
	
	public static void run(String paxmlFileName, StaticConfig config) throws Exception {
		
		Properties properties = new Properties();
		properties.putAll(System.getProperties());
		Paxml paxml = new Paxml(0);
		paxml.addStaticConfig(config);
		Context context = new Context(new Context(properties, paxml.getProcessId()));

		String xml = "<" + ScenarioTag.TAG_NAME + "><" + paxmlFileName + "/></" + ScenarioTag.TAG_NAME + ">";

		IEntity entity = paxml.getParser().parse(new InMemoryResource(xml), true, null);
		paxml.execute(entity, context, true, true);
		
		
	}

}
