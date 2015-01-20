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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallStack implements Serializable{
    private String tagName;
    private int line;
    private String entityName;
    private ContextDump contextDump;
    
    public String getTagName() {
        return tagName;
    }
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    public int getLine() {
        return line;
    }
    public void setLine(int line) {
        this.line = line;
    }
    
    
    public String getEntityName() {
        return entityName;
    }
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    public ContextDump getContextDump() {
        return contextDump;
    }
    public void setContextDump(ContextDump contextDump) {
        this.contextDump = contextDump;
    }
    
}
