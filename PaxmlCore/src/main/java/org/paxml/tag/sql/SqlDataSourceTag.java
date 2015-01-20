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

import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Soap data source tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "sqlDataSource")
public class SqlDataSourceTag extends BeanTag {
    /**
     * Private context keys.
     * 
     * @author Xuetao Niu
     * 
     */
    private static enum PrivateKeys {
        DATA_SOURCE
    }

    private String url;
    private String username;
    private String password;
    private String driver;

    /**
     * Get the data source from context, searching parents.
     * 
     * @param context
     *            the context
     * @return the data source, null if not found.
     */
    public static DriverManagerDataSource getDataSource(Context context) {
        return (DriverManagerDataSource) context.getLocalInternalObject(PrivateKeys.DATA_SOURCE, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password == null ? "" : password);
        Context targetContext = context.findContextForEntity(getEntity());
        String id = getId(context);
        if (StringUtils.isNotBlank(id)) {
            targetContext.addConst(id, null, dataSource, true);
        } else {
            targetContext.setInternalObject(PrivateKeys.DATA_SOURCE, dataSource, false);
        }
        return dataSource;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

}
