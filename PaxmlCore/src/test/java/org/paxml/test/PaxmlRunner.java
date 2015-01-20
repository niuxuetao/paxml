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
package org.paxml.test;

import java.util.Properties;

import org.paxml.launch.Paxml;

/**
 * The runner for debugging a single file's purpose.
 * 
 * @author Xuetao Niu
 * 
 */
public final class PaxmlRunner {
    private PaxmlRunner() {

    }

    /**
     * The main method.
     * 
     * @param args
     *            the args
     * @throws Exception
     *             any exception
     */
    public static void main(String[] args) throws Exception {
//        Properties props = new Properties();
//        LaunchModel model = paxml.executePlanFile("selftest/test.plan.xml", props);
//        Plan plan = model.getPlanEntity();
//        System.out.println(plan.printTree(0));
//        
//        System.out.println(model.getGroups().get("factored").getSettings().getFactors());
//        
//        if (true) {
//            return;
//        }
        Paxml paxml = new Paxml(0);

        paxml.addTagLibrary(MyTagLibrary.class);
        paxml.addResources(paxml.getResourceLocator().findResources("classpath*:Demo.xml", null));

        String name = "Demo";
        System.err.println(paxml.inspectEntity(name));
        Properties props = new Properties();

        // props.put(SeleniumTag.SELENIUM_RC_HOST, "localhost");
        // props.put(SeleniumTag.SELENIUM_RC_PORT, "4444");
        // props.put(SeleniumTag.SELENIUM_BROWSER_IDENTIFIER, "*firefox");

        paxml.execute(name, System.getProperties(), props);
    }
    
}
