/**
 * This file is part of PaxmlSelenium.
 *
 * PaxmlSelenium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlSelenium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlSelenium.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.selenium.rc;

import java.util.Properties;

import org.paxml.launch.Paxml;
import org.paxml.selenium.rc.SeleniumTag;
import org.paxml.selenium.rc.TagLibrary;

public class SeleniumTagRunner {
    public static void main(String[] args) throws Exception {
            
        Paxml paxml = new Paxml(0);

        paxml.addTagLibrary(TagLibrary.class);
        paxml.addResources(paxml.getResourceLocator().findResources("classpath*:paxml/**/*.xml", null));

        String name = "dynamic";
        System.err.println(paxml.inspectEntity(name));
        Properties props = new Properties();

        props.put(SeleniumTag.SELENIUM_RC_HOST, "localhost");
        props.put(SeleniumTag.SELENIUM_RC_PORT, "14444");
        props.put(SeleniumTag.SELENIUM_BROWSER_IDENTIFIER, "*googlechrome");

        paxml.execute(name, System.getProperties(), props);

    }
}
