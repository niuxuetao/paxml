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
package org.paxml.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.paxml.core.PaxmlRuntimeException;

/**
 * Converts an java.sql.ResultSet into iterable.
 * 
 * @author Xuetao Niu
 * 
 */
public class IterableResultSet implements Iterable<Map<String, Object>> {

    private final ResultSet rs;
    private final String[] cols;
    private final boolean readColumnNames;

    /**
     * Construct from ResultSet.
     * 
     * @param rs
     *            the ResultSet, never null
     * @param readColumnNames
     *            true to fetch the column names, false not to.
     */
    public IterableResultSet(final ResultSet rs, final boolean readColumnNames) {
        this.readColumnNames = readColumnNames;
        this.rs = rs;
        try {
            cols = new String[rs.getMetaData().getColumnCount()];
            if (readColumnNames) {
                for (int i = cols.length; i >= 1; i--) {
                    cols[i - 1] = rs.getMetaData().getColumnName(i);
                }
            }

        } catch (SQLException e) {
            throw new PaxmlRuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Map<String, Object>> iterator() {
        return new Iterator<Map<String, Object>>() {
            private Map<String, Object> next;
            {
                fetch();
            }

            private void fetch() {
                try {
                    if (rs.next()) {
                        next = new LinkedHashMap<String, Object>();

                        for (int i = 1; i <= cols.length; i++) {
                            next.put(readColumnNames ? cols[i - 1] : (i + ""), rs.getObject(i));
                        }
                    } else {
                        next = null;
                    }
                } catch (SQLException e) {
                    throw new PaxmlRuntimeException(e);
                }
            }

            public boolean hasNext() {
                return next != null;
            }

            public Map<String, Object> next() {

                final Map<String, Object> result = next;
                if (next == null) {
                    throw new PaxmlRuntimeException("No more elements!");
                }
                fetch();
                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException("Cannot remove from Enumeration");
            }

        };
    }

    public String[] getColumns() {
        return cols;
    }

    public ResultSet getResultSet() {
        return rs;
    }

    public boolean isReadColumnNames() {
        return readColumnNames;
    }

}
