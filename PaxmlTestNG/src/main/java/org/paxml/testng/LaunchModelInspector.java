/**
 * This file is part of PaxmlTestNG.
 *
 * PaxmlTestNG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlTestNG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlTestNG.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.testng;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.paxml.launch.LaunchModel;
import org.paxml.launch.LaunchPoint;
import org.paxml.util.AxiomUtils;

/**
 * Inspector for launch model that prints out effective TesNG xml.
 * 
 * @author Xuetao Niu
 * 
 */
public class LaunchModelInspector {
    /**
     * The new line.
     */
    public static final String LF = "\n";
    /**
     * The tab.
     */
    public static final String TAB = "\t";
    /**
     * The name attr.
     */
    public static final String NAME = "name";

    /**
     * Print the inspection xml to given out stream.
     * 
     * @param model
     *            the model
     * @param launchPoints
     *            the launch points
     * @param testNGOut
     *            the out stream
     */
    public void inspect(LaunchModel model, List<LaunchPoint> launchPoints, OutputStream testNGOut) {

        try {

            OMElement ele = AxiomUtils.newDocument("suit");
            ele.addChild(AxiomUtils.createTextNode(LF + LF));
            // set the concurrency info
            ele.addAttribute(NAME, model.getName(), null);
            ele.addAttribute("parallel", "tests", null);
            ele.addAttribute("thread-count", "?", null);
            // generate global parameters
            for (OMElement param : createParameters(model.getGlobalSettings().getProperties())) {
                ele.addChild(param);
                ele.addChild(AxiomUtils.createTextNode(LF));
            }
            ele.addChild(createParameter("file", model.getResource()));
            ele.addChild(AxiomUtils.createTextNode("\n\n"));
            // generate test cases
            for (int i = 0; i < launchPoints.size(); i++) {
                ele.addChild(createTestCase(launchPoints.get(i), i));
                ele.addChild(AxiomUtils.createTextNode("\n\n"));
            }

            ele.serializeAndConsume(testNGOut);
            testNGOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

    }

    private List<OMElement> createListeners(Class<?>... listenerClasses) {
        List<OMElement> list = new ArrayList<OMElement>(listenerClasses.length);
        for (Class<?> clazz : listenerClasses) {
            list.add(createListener(clazz));
        }

        return list;
    }

    private OMElement createListener(Class<?> listenerClass) {
        OMElement listener = AxiomUtils.getOMFactory().createOMElement("listener", null);
        listener.addAttribute("class-name", listenerClass.getName(), null);
        return listener;
    }

    private OMElement createTestCase(LaunchPoint launchPoint, int index) {
        OMElement ele = AxiomUtils.getOMFactory().createOMElement("test", null);
        ele.addAttribute(NAME, makeTestCaseName(launchPoint, index), null);
        ele.addChild(AxiomUtils.createTextNode(LF));
        // add the parameters
        for (OMElement param : createParameters(launchPoint.getFactors(), launchPoint.getProperties())) {
            ele.addChild(AxiomUtils.createTextNode(TAB));
            ele.addChild(param);
            ele.addChild(AxiomUtils.createTextNode(LF));
        }

        ele.addChild(AxiomUtils.createTextNode(LF));
        ele.addChild(AxiomUtils.createTextNode(TAB));
        // add the test class
        OMElement classEle = AxiomUtils.getOMFactory().createOMElement("class", null);
        ele.addChild(classEle);
        ele.addChild(AxiomUtils.createTextNode(TAB));
        ele.addChild(AxiomUtils.createTextNode(LF));
        classEle.addAttribute(NAME, PaxmlTestCase.class.getName(), null);
        return ele;
    }

    private List<OMElement> createParameters(Map<?, ?>... properties) {
        List<OMElement> list = new ArrayList<OMElement>();

        for (Map<?, ?> map : properties) {
            if (map != null) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    list.add(createParameter(entry.getKey(), entry.getValue()));
                }
            }
        }

        return list;

    }

    private OMElement createParameter(Object name, Object value) {
        OMElement ele = AxiomUtils.getOMFactory().createOMElement("parameter", null);
        ele.addAttribute(NAME, name.toString(), null);
        ele.addAttribute("value", value.toString(), null);
        return ele;
    }

    private String makeTestCaseName(LaunchPoint point, int index) {
        StringBuilder sb = new StringBuilder();
        sb.append(point.getResource().getName());

        if (point.getFactors() != null) {
            boolean first = true;
            for (Object value : point.getFactors().values()) {

                if (first) {
                    first = false;
                    sb.append("::");
                    sb.append(value);
                } else {
                    sb.append(",");
                    sb.append(value);
                }

            }
        }
        return sb.toString();
    }

}
