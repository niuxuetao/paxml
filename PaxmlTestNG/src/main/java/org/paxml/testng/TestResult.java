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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestResult extends TestResultSummary {
    private String errorScreenshotName;
    private List<ScreenshotInfo> screenshots;
    private List<CallStack> errorStack;
    private List<Const> systemProperties;
    private ContextDump contextDump;
    
    public List<CallStack> getErrorStack() {
        if(errorStack==null){
            errorStack=new ArrayList<CallStack>();
        }
        return errorStack;
    }
    public void setErrorStack(List<CallStack> errorStack) {
        this.errorStack = errorStack;
    }
    public List<Const> getSystemProperties() {
        if(systemProperties==null){
            systemProperties=new ArrayList<Const>();
        }
        return systemProperties;
    }
    public void setSystemProperties(List<Const> systemProperties) {
        this.systemProperties = systemProperties;
    }
    public ContextDump getContextDump() {
        return contextDump;
    }
    public void setContextDump(ContextDump contextDump) {
        this.contextDump = contextDump;
    }
    
    public String getErrorScreenshotName() {
        return errorScreenshotName;
    }
    public void setErrorScreenshotName(String errorScreenshotName) {
        this.errorScreenshotName = errorScreenshotName;
    }
    public List<ScreenshotInfo> getScreenshots() {
        if(screenshots==null){
            screenshots=new ArrayList<ScreenshotInfo>();
        }
        return screenshots;
    }
    public void setScreenshots(List<ScreenshotInfo> screenshots) {
        this.screenshots = screenshots;
    }
            
}
