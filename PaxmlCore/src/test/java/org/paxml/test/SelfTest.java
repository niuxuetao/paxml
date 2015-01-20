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

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.paxml.launch.LaunchModel;
import org.paxml.launch.LaunchPoint;
import org.paxml.launch.Paxml;

public class SelfTest {
    
    private static final Log log = LogFactory.getLog(SelfTest.class);
    
    
    public void testSyntax()throws Exception {
        ScriptEngine  runtime = new ScriptEngineManager().getEngineByName("javascript");
        Bindings bindings = runtime.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("util", "xx");
        runtime.setBindings(new SimpleBindings(){
            
        }, ScriptContext.ENGINE_SCOPE);
        
    }
    
    @Test
    public void testInvalid() {
        LaunchModel model = Paxml.executePlanFile("selftestInvalid/test.plan.xml", System.getProperties());
        for (LaunchPoint lp : model.getLaunchPoints(false)) {
            try {

                model.execute(lp);
                Assert.fail("Should have error, but got none executing this file:\r\n"+lp.getResource()+"\r\n"+
                       FileUtils.readFileToString(lp.getResource().getSpringResource().getFile()));
            } catch (Exception e) {
                // ok
                log.debug("Got expected exception", e);
            }
        }
    }
    
    @Test
    public void testValid() {

        LaunchModel model = Paxml.executePlanFile("selftest/test.plan.xml", System.getProperties());
        model.execute(model.getLaunchPoints(false));
    }
    
    @Test
    public void testReturnTag() {
        Paxml paxml = new Paxml(0);

        paxml.addTagLibrary(MyTagLibrary.class);
        paxml.addResources(paxml.getResourceLocator().findResources("classpath:selftest/**/*.xml", null));

        final String name = "invokeReturnTest";
        final Object result="this is ok!!";
        
        Properties props = new Properties();

        Assert.assertEquals(result, paxml.execute(name, System.getProperties(), props));
    }

}
