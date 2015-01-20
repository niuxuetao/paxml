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
package org.paxml.assertion;

import java.util.regex.Pattern;

import org.paxml.annotation.Tag;
import org.paxml.assertion.AssertTag.AssertTagException;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;

/**
 * AssertPattern tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "assertPattern")
public class AssertPatternTag extends BeanTag {

    private String regexp;
    private Object actual;
    private boolean unixLines;
    private boolean canonEq;
    private boolean caseSensitive;
    private boolean comments;
    private boolean dotAll;
    private boolean literal;
    private boolean multiline;
    private boolean unicodeCase;
    private boolean emptyNull;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) {
        int flags = 0;
        if (unixLines) {
            flags |= Pattern.UNIX_LINES;
        }
        if (canonEq) {
            flags |= Pattern.CANON_EQ;
        }
        if (caseSensitive) {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        if (comments) {
            flags |= Pattern.COMMENTS;
        }
        if (dotAll) {
            flags |= Pattern.DOTALL;
        }
        if (literal) {
            flags |= Pattern.LITERAL;
        }
        if (multiline) {
            flags |= Pattern.MULTILINE;
        }
        if (unicodeCase) {
            flags |= Pattern.UNICODE_CASE;
        }
        
        if (!Pattern.compile(regexp, flags).matcher(getStringValue()).matches()) {
            throw new AssertTagException(getValue() == null ? "Expected pattern: " + regexp + " does not match value: "
                    + actual : getValue().toString());
        }

        return null;
    }

    private String getStringValue() {

        if (actual == null) {
            return emptyNull ? "" : String.valueOf(actual);
        } else {
            return String.valueOf(actual);
        }
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public Object getActual() {
        return actual;
    }

    public void setActual(Object actual) {
        this.actual = actual;
    }

    public boolean isUnixLines() {
        return unixLines;
    }

    public void setUnixLines(boolean unixLines) {
        this.unixLines = unixLines;
    }

    public boolean isCanonEq() {
        return canonEq;
    }

    public void setCanonEq(boolean canonEq) {
        this.canonEq = canonEq;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isComments() {
        return comments;
    }

    public void setComments(boolean comments) {
        this.comments = comments;
    }

    public boolean isDotAll() {
        return dotAll;
    }

    public void setDotAll(boolean dotAll) {
        this.dotAll = dotAll;
    }

    public boolean isLiteral() {
        return literal;
    }

    public void setLiteral(boolean literal) {
        this.literal = literal;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public boolean isUnicodeCase() {
        return unicodeCase;
    }

    public void setUnicodeCase(boolean unicodeCase) {
        this.unicodeCase = unicodeCase;
    }

    public boolean isEmptyNull() {
        return emptyNull;
    }

    public void setEmptyNull(boolean emptyNull) {
        this.emptyNull = emptyNull;
    }

}
