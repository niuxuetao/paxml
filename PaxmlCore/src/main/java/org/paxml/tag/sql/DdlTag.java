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

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.ZipScanner;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

/**
 * Ddl tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "ddl")
public class DdlTag extends SqlTag {

    public static final String CLEAN = "clean";
    public static final String CREATE = "create";
    public static final String UPDATE = "update";

    private static interface IApplicabilityChecker {
        boolean apply(DdlScript script);
    }

    private class VersionChecker implements IApplicabilityChecker {
        private final DdlVersion fromV;
        private final DdlVersion toV;

        private VersionChecker(Context context) {
            String fromVstr = getVersion(fromVersion, context);
            this.fromV = StringUtils.isBlank(fromVstr) ? null : new DdlVersion(fromVstr);
            String toVstr = getVersion(toVersion, context);
            this.toV = StringUtils.isBlank(toVstr) ? null : new DdlVersion(toVstr);
        }

        @Override
        public boolean apply(DdlScript script) {
            // script must represent a version higher than the "fromVersion"
            if (fromV != null && script.getVersion().compareTo(fromV) <= 0) {
                return false;
            }
            // script must represent a version lower than or equal to the
            // "toVersion"
            if (toV != null && script.getVersion().compareTo(toV) > 0) {
                return false;
            }
            return true;
        }

    }

    private static final Log log = LogFactory.getLog(DdlTag.class);
    private String dir;
    private Object include;
    private Object exclude;
    private String fromVersion;
    private String toVersion;
    private Object cleanSchemaSql;
    private String action;

    @Override
    protected Object doInvoke(Context context) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Performing action: " + action);
        }
        if (CLEAN.equalsIgnoreCase(action)) {
            clean(context);
        } else if (UPDATE.equalsIgnoreCase(action)) {
            update(context);
        } else if (CREATE.equalsIgnoreCase(action)) {
            create(context);
        } else {
            throw new PaxmlRuntimeException("Unknown action: " + action);
        }
        return null;
    }

    private void clean(Context context) {

        if (cleanSchemaSql == null) {
            throw new PaxmlRuntimeException("No clean schema sql given.");
        }

        List sql = null;
        Iterator it = null;
        if (cleanSchemaSql instanceof Iterable) {
            it = ((Iterable) cleanSchemaSql).iterator();
        } else if (cleanSchemaSql instanceof Array) {
            sql = CollectionUtils.arrayToList(cleanSchemaSql);
        } else if (cleanSchemaSql instanceof Iterator) {
            it = (Iterator) cleanSchemaSql;
        } else {
            sql = Arrays.asList(cleanSchemaSql.toString());
        }
        if (sql == null) {
            sql = new ArrayList<String>();
            while (it.hasNext()) {
                sql.add(it.next());
            }
        }
        for (Object s : sql) {
            if (s != null) {
                executeSql(s.toString(), context);
            }
        }
    }

    private void create(Context context) {
        runScripts(context, new VersionChecker(context));
    }

    private void update(final Context context) {

        runScripts(context, new VersionChecker(context) {

            @Override
            public boolean apply(DdlScript script) {
                // filter out non-update scripts
                if (script.getType() != DdlScript.Type.UPDATE_DDL && script.getType() != DdlScript.Type.UPDATE_DATA) {
                    return false;
                }
                return super.apply(script);
            }
        });
    }

    private void runScripts(Context context, IApplicabilityChecker checker) {
        List<DdlScript> list = new ArrayList<DdlScript>();
        File container = getContainerFile();
        for (String file : getFiles(container)) {
            DdlScript script = new DdlScript(container, file);
            if (checker.apply(script)) {
                list.add(script);
                if (log.isDebugEnabled()) {
                    log.debug("This ddl script does apply: " + script.getFileName());
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("This ddl script does not apply: " + script.getFileName());
                }
            }
        }

        Collections.sort(list);
        for (DdlScript script : list) {
            if (log.isInfoEnabled()) {
                log.info("Running script: " + script.getFileName());
            }
            String sql = script.readContent();
            executeSql(sql, context);
        }
    }

    private File getContainerFile() {

        if (StringUtils.isEmpty(dir)) {
            throw new PaxmlRuntimeException("No 'dir' property specified!");
        }

        try {
            return ResourceUtils.getFile(dir);
        } catch (FileNotFoundException e) {
            return new File(dir);
        }
    }

    private String[] getFiles(File container) {
        DirectoryScanner ds;
        if (container.isDirectory()) {
            ds = new DirectoryScanner();
            if (log.isDebugEnabled()) {
                log.debug("Scanning directory for sql files: " + container.getAbsolutePath());
            }
        } else {
            ds = new ZipScanner();
            if (log.isDebugEnabled()) {
                log.debug("Scanning zip file for sql files: " + container.getAbsolutePath());
            }
        }

        ds.setIncludes(getArray(include));
        ds.setExcludes(getArray(exclude));

        if (ds instanceof ZipScanner) {
            ((ZipScanner) ds).setSrc(container);
        } else {
            ds.setBasedir(container);
        }

        ds.setCaseSensitive(true);
        ds.scan();
        String[] files = ds.getIncludedFiles();
        return files;

    }

    private String getVersion(String version, Context context) {

        if (isQuery(version)) {
            Object result = executeSql(version, context);
            if (result instanceof List) {
                List list = (List) result;
                if (list.isEmpty()) {
                    throw new PaxmlRuntimeException("No ddl version value can be found with query: " + version);
                }
                Map row = (Map) list.get(0);
                return row.values().iterator().next().toString();
            } else if (result instanceof ResultSet) {
                ResultSet rs = (ResultSet) result;
                try {
                    if (rs.next()) {
                        return rs.getObject(1).toString();
                    } else {
                        throw new PaxmlRuntimeException("No ddl version value can be found with query: " + version);
                    }
                } catch (SQLException e) {
                    throw new PaxmlRuntimeException("Cannot get ddl version value with query: " + version, e);
                }
            } else {
                throw new PaxmlRuntimeException("Unknown ddl version query result: " + result);
            }
        } else {
            return version;
        }
    }

    private String[] getArray(Object value) {
        if (value == null) {
            return new String[0];
        }

        Iterator it = null;
        
        if (value instanceof Iterable) {
            it = ((Iterable) value).iterator();
        } else if (value instanceof Iterator) {
            it = (Iterator) value;
        } else if (value.getClass().isArray()) {
            int len = Array.getLength(value);
            List<String> list = new ArrayList<String>(len);
            for (int i = 0; i < len; i++) {
                Object item = Array.get(value, i);
                if (item != null) {
                    list.add(item.toString());
                }
            }
            return list.toArray(new String[list.size()]);
        }

        if (it != null) {
            List<String> list = new ArrayList<String>();
            while (it.hasNext()) {
                Object next = it.next();
                if (next != null) {
                    list.add(next.toString());
                }
            }
            return list.toArray(new String[list.size()]);
        }

        return new String[] {value.toString()};

    }

    @Override
    protected void afterPropertiesInjection(Context context) {

        super.afterPropertiesInjection(context);

        if (dir == null) {
            dir = ".";
        }
        if (include == null) {
            include = "**/*.*";
        }

        setFile(null);
        setReadColumnNames(false);
    }

    public String getFromVersion() {
        return fromVersion;
    }

    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }

    public String getToVersion() {
        return toVersion;
    }

    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public Object getInclude() {
        return include;
    }

    public void setInclude(Object include) {
        this.include = include;
    }

    public Object getExclude() {
        return exclude;
    }

    public void setExclude(Object exclude) {
        this.exclude = exclude;
    }

    public Object getCleanSchemaSql() {
        return cleanSchemaSql;
    }

    public void setCleanSchemaSql(Object cleanSchemaSql) {
        this.cleanSchemaSql = cleanSchemaSql;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
