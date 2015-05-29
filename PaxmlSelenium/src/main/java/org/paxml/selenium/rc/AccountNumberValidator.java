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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Custom Validator for Account Number Validation.
 * 
 * @author Xuetao Niu
 */
public class AccountNumberValidator {

    
    private static final Log LOG = LogFactory
            .getLog(AccountNumberValidator.class);

    private static final int ELEVEN = 11;
    
    private static final int MIN_ACCOUNTNUMBER_LENGTH = 9;
    private static final int MAX_ACCOUNTNUMBER_LENGTH = 10;
    private static final int MIN_POSTACCOUNTNUMBER_LENGTH = 1;
    private static final int MAX_POSTACCOUNTNUMBER_LENGTH = 8;
 
    public static boolean validate(String value) {
        value = value.replaceAll("[.\\s]", "");
        boolean isNumeric = StringUtils.isNumeric(value);
        int len = value.length();
        if (len >= MIN_ACCOUNTNUMBER_LENGTH && len <= MAX_ACCOUNTNUMBER_LENGTH) { // regular account
            if (!isNumeric || !modula11(value)) {
                return false;
            } else if (StringUtils.containsOnly(value, "0")) { // check if only zeros
                return false;
            }
        } else if (len >= MIN_POSTACCOUNTNUMBER_LENGTH && len <= MAX_POSTACCOUNTNUMBER_LENGTH) { // post bank
            if (StringUtils.containsOnly(value, "pP0")) { // check if one of these: p, P, p0, P0, 0, 0000000
                return false;    
            } else if (len == MAX_POSTACCOUNTNUMBER_LENGTH && isNumeric) { 
                // exactly 8 digits is an invalid account number
                return false;
            } else if (!isNumeric) { // contains letters, check if it is correct one in correct position
                int noOfLetters = value.replaceAll("\\d", "").length();
                if (noOfLetters > 1) { // more then one letter is a problem
                    return false;
                } else if (!value.substring(0, 1).equalsIgnoreCase("P")) { // P must be first if present
                    return false;
                }           
            }
        } else { // value too long or empty (it can happen, e.g.: ". . .")
            return false;
        }
        return true;
    }
    
    /**
     * Method to Validate Account Number.
     * 
     * @param accNo for modula11 validation.
     * @return boolean
     */
    public static boolean modula11(String accNo) {
        boolean result = false;
        int l = accNo.length();
        int total = 0;
        try {
            for (int i = 0; i < l; i++) {
                int j = Character.getNumericValue(accNo.charAt(i)) * (l - i);
                total += j;
            }
            total = total % ELEVEN;
            if (total == 0) {
                result = true;
            }
        } catch (NumberFormatException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error1: " + e.getMessage());
            }
        }
        return result;
    }

}
