/**
 * This file is part of PaxmlSelenium.
 *
 * PaxmlSelenium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlSelenium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlSelenium.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.selenium.rc;

import java.text.MessageFormat;
import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;

public final class FormattingUtils {

    /** A constant representing the empty string. */
    public static final String EMPTY_STRING = "";
    /**
     * A constant representing how many characters should be skipped before the
     * IBAN_SEPARATOR character is put in the formatted IBAN string.
     */
    public static final int IBANSPACING = 4;
    /**
     * A constant representing the string with which the IBAN has to be
     * separated.
     */
    public static final String IBAN_SEPARATOR = " ";

    private static final String DEFAULT_FORMAT = "dd/MM/yyyy";

    
    //TODO: make this configurable, instead of hardcoded
    private static final Hashtable<String, String> ANF = new Hashtable<String, String>();
    static {
        ANF.put("accountnumber.pattern.9", "{1}{2}.{3}{4}.{5}{6}.{7}{8}{9}");
        ANF.put("accountnumber.pattern.10", "{1}{2}{3}.{4}{5}.{6}{7}.{8}{9}{10}");
        ANF.put("accountnumber.pattern.13", "{1}{2}{3}.{4}{5}{6}.{7}{8}{9}.{10}{11}{12}{13}");
        ANF.put("accountnumber.pattern.14", "{1}{2}{3}.{4}{5}.{6}{7}.{8}{9}{10}.{11}{12}{13}{14}");
        ANF.put("accountnumber.pattern.15", "{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}{11}.{12}{13}{14}{15}");
    }

    /**
     * Hide the constructor for utility class.
     */
    private FormattingUtils() {
    }

    /**
     * Util method for formatting Account Number to displayed in the FE. The
     * formatting assumes valid account numbers. If number might be invalid use
     * formatValidAccountNumber
     * 
     * @param accountnumber
     *            Account Number to be formatted.
     * @return String.
     */
    public static String formatAccountNumber(String accountnumber) {

        String cleanAccountnumber = accountnumber.replaceAll("[.\\s]", "");

        // Format number only if not empty and is valid account number.
        if (StringUtils.isEmpty(cleanAccountnumber)) {
            return cleanAccountnumber;
        }

        String pattern = determinePattern(cleanAccountnumber.length());
        if (pattern != null) {
            return MessageFormat.format(pattern, (Object[]) cleanAccountnumber.split(""));
        }

        return cleanAccountnumber;
    }

    /**
     * Util method for formatting Account Number to displayed in the FE. The
     * formatting will be done ONLY for valid account numbers!
     * 
     * @param accountnumber
     *            Account Number to be formatted.
     * @return String.
     */
    public static String formatValidAccountNumber(String accountnumber) {
        String cleanAccountnumber = accountnumber.replaceAll("[.\\s]", "");

        // Format number only if not empty and is valid account number.
        if (StringUtils.isEmpty(cleanAccountnumber) || !AccountNumberValidator.validate(cleanAccountnumber)) {
            return cleanAccountnumber;
        } else {
            return FormattingUtils.formatAccountNumber(accountnumber);
        }
    }

    /**
     * Util Method for formatting IBAN to displayed in the FE.
     * 
     * @param iban
     *            IBAN to be formatted.
     * @return String.
     */
    public static String formatIBAN(String iban) {

        StringBuffer formattediban = new StringBuffer(EMPTY_STRING);

        if (iban != null) {
            for (int i = 0; i < iban.length(); i++) {
                formattediban.append(iban.charAt(i));
                if (((i + 1) % IBANSPACING) == 0) {
                    formattediban.append(IBAN_SEPARATOR);
                }
            }
        }

        return formattediban.toString().trim();
    }

    /**
     * Like {@link #prettyTruncate(String, int, String)} but with '...' as
     * postfix.
     * 
     * @param value
     *            The string to truncate
     * @param maxLength
     *            The max length of the resulting string.
     * @return A psooible truncated string
     */
    public static String prettyTruncate(String value, int maxLength) {
        return prettyTruncate(value, maxLength, "...");
    }

    /**
     * Truncates the given string when its length exceeds the maxlength, and
     * concats the postFix when truncating. When the given value does not need
     * truncating the value itself is returned, when <code>null</code> is
     * specified, the empty string is returned..
     * 
     * @param value
     *            The string to truncate
     * @param maxLength
     *            The max length of the resulting string.
     * @param postFix
     *            The string that is added at the end of the resulting string.
     * @return A possibly truncated string, never <code>null</code>.
     */
    public static String prettyTruncate(String value, int maxLength, String postFix) {
        if (value == null) {
            return "";
        }

        if (value.length() > maxLength) {
            return value.substring(0, maxLength - postFix.length()) + postFix;
        }
        return value;
    }

    private static String determinePattern(int length) {
        final String key = "accountnumber.pattern." + length;
        return ANF.get(key);

    }

    /**
     * Concatenates strings.
     * 
     * @param size
     *            Size to pad
     * @param strings
     *            Strings.
     * @return String
     */
    public static String concatDescription(int size, String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            if (string != null) {
                builder.append(StringUtils.rightPad(string, size));
            }
        }
        return builder.toString();
    }
}
