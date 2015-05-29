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
package org.paxml.tag.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DdlVersion implements Comparable<DdlVersion> {

    private final Integer[] version;

    public DdlVersion(String version) {
        String[] vs = StringUtils.split(version, '.');
        List<Integer> v = new ArrayList<Integer>(vs.length);
        for (int i = 0; i < vs.length; i++) {
            String s = vs[i];
            if (StringUtils.isNumeric(s)) {
                v.add(Integer.parseInt(s));
            } else {
                break;
            }

        }
        this.version = v.toArray(new Integer[v.size()]);
    }

    @Override
    public int compareTo(DdlVersion o) {
        int r = 0;
        for (int i = 0; i < version.length; i++) {
            // shorter sections are smaller
            if (o.version.length <= i) {
                return 1;
            }
            r = version[i].compareTo(o.version[i]);
            // if different in any section, return immediately
            if (r != 0) {
                return r;
            }
        }        
        // compare length, shorter length is smaller
        return new Integer(version.length).compareTo(o.version.length);
    }

    public Integer[] getVersion() {
        return version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < version.length; i++) {
            if (i > 0) {
                sb.append(".");
            }
            sb.append(version[i]);
        }
        return sb.toString();
    }

}
