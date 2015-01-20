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
package org.paxml.el;

import org.apache.commons.lang.StringUtils;
import org.paxml.core.PaxmlParseException;
import org.paxml.core.PaxmlRuntimeException;

/**
 * The factory to create expressions.
 * 
 * @author Xuetao Niu
 * 
 */
public final class ExpressionFactory {

    private ExpressionFactory() {

    }

    private static JexlExpression createJexlExpression(String exp, boolean strict) {
        exp = trimExp(exp);
        try {
            return new JexlExpression(exp, strict);
        } catch (Exception e) {
            throw new PaxmlRuntimeException("Cannot parse jexl expression: " + exp, e);
        }
    }

    private static String trimExp(String exp) {
        if (!StringUtils.isBlank(exp)) {
            exp = exp.trim();
        }
        return exp;
    }

    /**
     * Construct an expression from a string.
     * 
     * @param exp
     *            the string expression
     * @return the expression object, never null
     */
    public static IExpression create(final String exp) {
        // comment off the following two lines in order to preserve spaces and
        // line breaks
        // exp = trimExp(exp);
        // exp = exp.replace('\n', ' ').replace('\r', ' ');
        final ConcatExpression result = new ConcatExpression();

        int lastStart = 0;
        for (int searchStart = 0; searchStart < exp.length();) {

            final int startPos = findNextStart(exp, searchStart);

            if (startPos >= 0) {
                final boolean strict = exp.charAt(startPos) == '$';
                final int endPos = exp.indexOf("}", startPos + 2);
                if (endPos > 0) {
                    final String subExp = exp.substring(startPos + 2, endPos);
                    if (StringUtils.isBlank(subExp)) {
                        throw new PaxmlParseException("Empty evaluation given at position " + startPos
                                + " in expression: " + exp);
                    }
                    String literal = exp.substring(lastStart, startPos);
                    if (literal.length() > 0) {
                        // escape the brackets                        
                        result.addPart(new LiteralExpression(escape(literal)));
                    }
                    result.addPart(createJexlExpression(subExp, strict));
                    searchStart = endPos + 1;
                    lastStart = searchStart;
                } else {
                    throw new PaxmlParseException("No closing sign } found after opening sign ${ at position "
                            + startPos + " in expression string: " + exp);
                }
            } else {
                // not found
                break;
            }
        }
        // check the remaining literal
        if (lastStart < exp.length()) {
            // still there is a tail as literal
            String literal = exp.substring(lastStart);
            if(literal.length()>0){
                result.addPart(new LiteralExpression(escape(literal)));
            }

        }
        return result;
    }
    private static String escape(String literal){
        return literal.replace("$${", "${").replace("$?{", "?{");
    }
    private static int findNextStart(String exp, int since) {

        while (since < exp.length()) {
            int start = exp.indexOf("${", since);
            if (start < 0) {
                start = exp.indexOf("?{", since);
            }
            if (start == 0) {
                return 0;
            }
            if (start < 0) {
                break;
            }
            if (start > 0 && exp.charAt(start - 1) != '$') {
                return start;
            }
            since = start + 2;
        }
        return -1;
    }

    private static String makeLiteral(String literal) {
        literal = literal.replace("\\", "\\\\");
        StringBuilder sb = new StringBuilder("'");
        for (int i = 0; i < literal.length(); i++) {
            char c = literal.charAt(i);
            if (c == '\'') {
                sb.append("'+\"'\"+'");
            } else if (c == '"') {
                sb.append("'+\"'+'");
            } else {
                sb.append(c);
            }
        }
        sb.append("'");
        return sb.toString();
    }

    private static boolean isEscaped(String exp, int pos) {
        int count = 0;
        for (int i = pos - 1; i >= 0; i--) {
            char c = exp.charAt(i);
            if ('$' != c) {
                break;
            }
            count++;
        }
        return count > 0 && count % 2 == 0;
    }

    /**
     * Check if a value is true.
     * 
     * @param obj
     *            the value
     * @return true if the value is not null and the string form is not "false".
     *         Otherwise false.
     */
    public static boolean isTrue(Object obj) {
        return obj != null && !String.valueOf(obj).equals("false");
    }

}
