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

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.testng.Assert;

/**
 * Test case that represents factory failure.
 * 
 * @author Xuetao Niu
 * 
 */
public class PaxmlPlanFileFailure extends AbstractPaxmlTestResult {
    private static final Log log = LogFactory.getLog(PaxmlPlanFileFailure.class);
    private final Throwable exception;
    private final Context context;
    private final String threadName;
    private final long start;
    private final long stop;
    private final String planEntityName;

    /**
     * Constructor.
     * 
     * @param exception
     *            failure reason.
     * @param planResource
     *            the plan file's resource
     * 
     */
    public PaxmlPlanFileFailure(final Throwable exception, String planFile, File outputDir, ResultType resultType,
            Context c, String threadName, long start, long stop) {
        super(null, FilenameUtils.getBaseName(planFile), 0, outputDir, resultType, null);
        this.exception = exception;
        this.threadName = threadName;
        this.context = c;
        this.start = start;
        this.stop = stop;
        this.planEntityName = FilenameUtils.getBaseName(planFile);
    }

    @Override
    protected long getStartMs() {
        return start;
    }

    @Override
    protected long getStopMs() {
        return stop;
    }

    @Override
    protected Context getContext() {

        return context;

    }

    @Override
    protected void onSummary(TestResultSummary s) {
        TestResultIndex index = new TestResultIndex();
        index.getSummary().add(s);
        index.setStart(start);
        index.setStop(stop);
        index.setPlanEntityName(planEntityName);
        
        writeReportFile(index, true);
        
    }

    @Override
    protected String getThreadName() {
        return threadName;
    }

    /**
     * Test method that will fail with the reason given during construction.
     */
    @Override
    protected void doTest() {

        Context.cleanCurrentThreadContext();
       
        Assert.fail(exception.getMessage(), exception);
    }
}
