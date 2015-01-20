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
package org.paxml.bean;

import java.io.File;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.springframework.util.ResourceUtils;

/**
 * Ant tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "ant")
public class AntTag extends BeanTag {

    private static final Log log = LogFactory.getLog(AntTag.class);

    public static final String DEFAULT_ANT_FILE = "build.xml";

    private boolean failOnError = true;
    private String dir;
    private String file;
    private String target;
    private boolean shareContext = true;

    @Override
    protected Object doInvoke(Context context) throws Exception {

        File dirFile = StringUtils.isBlank(dir) ? null : ResourceUtils.getFile(dir);

        if (dirFile != null && !dirFile.isDirectory()) {
            throw new PaxmlRuntimeException("The given 'dir' property is not a directory: " + dirFile.getAbsolutePath());
        }
        String antFile = StringUtils.isBlank(file) ? DEFAULT_ANT_FILE : file;
        File antFileFile = new File(antFile);

        File buildFile;
        // classpath is logically absolute path!
        if (antFile.startsWith("classpath:") || antFile.startsWith("classpath*:")) {
            buildFile = ResourceUtils.getFile(antFile);
        }else if (antFileFile.isFile() && antFileFile.isAbsolute()) {
            buildFile = antFileFile;
        } else if (dirFile == null) {
            buildFile = new File(antFile);
        } else {
            buildFile = new File(dirFile, antFile);
        }
        if (buildFile == null) {
            throw new PaxmlRuntimeException("Ant build fild not found: " + antFile + " under dir: " + dir);
        }

        Project p = new Project();

        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        p.addBuildListener(consoleLogger);

        if (shareContext) {
            Map<String, Object> map = context.getIdMap(true, true);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();

                if (value != null) {
                    if (log.isDebugEnabled()) {
                        String svalue = value.toString();
                        final int MAX_LEN = 100;
                        if (svalue.length() > MAX_LEN) {
                            svalue = svalue.substring(0, 100) + " ...";
                        }
                        log.debug("Setting user property: " + key + " = " + svalue);
                    }
                    p.setUserProperty(key, value.toString());
                }
            }
        }

        p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        if (dirFile != null) {
            p.setUserProperty("basedir", dirFile.getAbsolutePath());
        }

        boolean result = true;
        try {
            p.fireBuildStarted();
            p.init();
            ProjectHelper helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);

            String target = StringUtils.isBlank(this.target) ? p.getDefaultTarget() : this.target;
            StringTokenizer st = new StringTokenizer(target, " \r\n\t,;|");
            while (st.hasMoreTokens()) {
                String t = st.nextToken();
                if (log.isInfoEnabled()) {
                    log.info("Running target '" + t + "' in ant file: " + buildFile);
                }
                p.executeTarget(t);
            }
            p.fireBuildFinished(null);
        } catch (Exception e) {
            p.fireBuildFinished(e);
            if (failOnError) {
                throw new PaxmlRuntimeException(e);
            }
            result = false;
        }
        return result;

    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public boolean isShareContext() {
        return shareContext;
    }

    public void setShareContext(boolean shareContext) {
        this.shareContext = shareContext;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
