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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.paxml.annotation.Tag;
import org.paxml.control.AbstractClosureTag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.IterableResultSet;

/**
 * SqlQuery tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "sqlQuery")
public class SqlQueryTag extends AbstractClosureTag {
    /**
     * A closable result set reader.
     * 
     * @author Xuetao Niu
     * 
     */
    public static class ClosableResultSetIterable extends IterableResultSet {
        /**
         * Create from ResultSet.
         * 
         * @param rs
         *            the ResultSet
         * @param readColumnNames
         *            true to fetch the column names, false not to.
         */
        public ClosableResultSetIterable(final ResultSet rs, final boolean readColumnNames) {
            super(rs, readColumnNames);
        }

        /**
         * Close the associated sql statement and connection.
         */
        public void close() {
            Statement stmt = null;
            Connection con = null;
            ResultSet rs = null;
            try {
                rs = getResultSet();
                stmt = rs.getStatement();
                con = stmt.getConnection();
            } catch (SQLException e) {
                throw new PaxmlRuntimeException("Cannot close the sql Statement and Connection with ResultSet", e);
            } finally {

                closeResultSet(rs);
                closeStatement(stmt);
                closeConnection(con);                
            }

        }

    }

    /**
     * Private keys.
     * 
     * @author Xuetao Niu
     * 
     */
    private static enum PrivateKeys {
        RESULT_SETS
    }

    /**
     * Holder for resultsets that will be closed by the end of the execution of
     * the closure.
     * 
     * @author Xuetao Niu
     * 
     */
    public static class ResultSetsHolder {
        private final List<ClosableResultSetIterable> list = new ArrayList<ClosableResultSetIterable>();

        /**
         * Register a resultset to close later.
         * 
         * @param rs
         *            the resultset
         */
        public void register(ClosableResultSetIterable rs) {
            list.add(rs);
        }

        private void close() {
            for (ClosableResultSetIterable rs : list) {
                rs.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doExecute(Context context) {
        final ResultSetsHolder holder = new ResultSetsHolder();
        context.setInternalObject(PrivateKeys.RESULT_SETS, holder, false);
        Object result = null;
        try {
            result = super.doExecute(context);
            context.removeInternalObject(PrivateKeys.RESULT_SETS, false);
        } finally {            
            holder.close();            
        }
        return result;
    }
    /**
     * Get the nearest resultsets holder from context.
     * @param context the context
     * @return the resultsets holder or null if not found
     */
    public static ResultSetsHolder getClosureTag(Context context) {
        return (ResultSetsHolder) context.getLocalInternalObject(PrivateKeys.RESULT_SETS, true);
    }

    static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // swallow
                e.printStackTrace();
            }
        }
    }

    static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                // swallow
                e.printStackTrace();
            }
        }
    }

    static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // swallow
                e.printStackTrace();
            }
        }
    }

}
