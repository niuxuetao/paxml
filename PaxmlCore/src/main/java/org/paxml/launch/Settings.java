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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * The settings of group.
 * 
 * @author Xuetao Niu
 * 
 */
public class Settings {
    private final String group;
    private final Map<String, Factor> factors = Collections.synchronizedMap(new LinkedHashMap<String, Factor>());
    private final Properties properties = new Properties();
    private final Set<Matcher> matchers = Collections.synchronizedSet(new LinkedHashSet<Matcher>());
    public Settings(String group){
        this.group=group;
    }
    public Map<String, Factor> getFactors() {
        return factors;
    }

    public Properties getProperties() {
        return properties;
    }

    public Set<Matcher> getMatchers() {
        return matchers;
    }
    public String getGroup() {
        return group;
    }

}
