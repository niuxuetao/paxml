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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * Date tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "date")
public class DateTag extends BeanTag {
    private static final Log log = LogFactory.getLog(DateTag.class);
    private String toFormat;
    private String fromFormat;
    private int addYear;
    private int addMonth;
    private int addDay;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        Date date = parseDate();
        date = changeDate(date);
        if (StringUtils.isNotBlank(toFormat)) {
            return dateToString(toFormat, date);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("No 'toFormat' given, doing no conversion. Directly returning: " + date);
            }
            return date;
        }
    }

    private Date changeDate(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.YEAR, addYear);
        cal.add(Calendar.MONTH, addMonth);
        cal.add(Calendar.DATE, addDay);

        Date result = cal.getTime();
        if (log.isDebugEnabled()) {
            log.debug("Adding " + addYear + " years, " + addMonth + " months, " + addDay + " days to date: " + date
                    + ", the result is: " + result);
        }
        return result;

    }

    private Date parseDate() throws ParseException {
        final Object value = getValue();

        Date date;
        if (value instanceof Date) {
            date = (Date) value;
        } else if (value instanceof Calendar) {
            date = ((Calendar) value).getTime();
        } else if (value instanceof java.sql.Date) {
            date = new Date(((java.sql.Date) value).getTime());
        } else if (value instanceof java.sql.Timestamp) {
            date = new Date(((java.sql.Timestamp) value).getTime());
        } else if (value == null || StringUtils.isBlank(value.toString())) {
            date = new Date();
        } else if (value instanceof Number) {
            date = new Date(((Number) value).longValue());
        } else {
            if (StringUtils.isBlank(fromFormat)) {
                date = DatatypeConverter.parseDate(value.toString()).getTime();
            } else {
                date = new SimpleDateFormat(fromFormat).parse(value.toString());
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Parsing from: " + value + " to: " + date + " where fromFormat=" + fromFormat);
        }
        return date;
    }

    private String dateToString(String fmt, Date date) throws ParseException {
        String result = new SimpleDateFormat(fmt).format(date);
        if (log.isDebugEnabled()) {
            log.debug("Formatting from: " + date + " to: " + result + " where toFormat=" + fmt);
        }
        return result;
    }

    public String getToFormat() {
        return toFormat;
    }

    public void setToFormat(String toFormat) {
        this.toFormat = toFormat;
    }

    public String getFromFormat() {
        return fromFormat;
    }

    public void setFromFormat(String fromFormat) {
        this.fromFormat = fromFormat;
    }

    public int getAddYear() {
        return addYear;
    }

    public void setAddYear(int addYear) {
        this.addYear = addYear;
    }

    public int getAddMonth() {
        return addMonth;
    }

    public void setAddMonth(int addMonth) {
        this.addMonth = addMonth;
    }

    public int getAddDay() {
        return addDay;
    }

    public void setAddDay(int addDay) {
        this.addDay = addDay;
    }

}
