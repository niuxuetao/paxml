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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.el.IUtilFunctionsFactory;
import org.paxml.tag.ITagLibrary;
import org.paxml.util.ReflectUtils;

/**
 * Groovy tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "groovy")
public class GroovyTag extends BeanTag {
    private static final Log log = LogFactory.getLog(GroovyTag.class);

    @Override
    protected Object doInvoke(Context context) throws Exception {
        Binding binding = new Binding();
        // inject all context variables into groovy
        for (Map.Entry<String, Object> entry : context.getIdMap(true, true).entrySet()) {
            binding.setVariable(entry.getKey(), entry.getValue());
        }
        // set the util functions
        for (ITagLibrary tagLib : context.getPaxml().getParser().getTagLibraries()) {
            for (String util : tagLib.getUtilFunctionsFactoryNames()) {
                Class<? extends IUtilFunctionsFactory> clazz = (Class<? extends IUtilFunctionsFactory>) tagLib
                        .getUtilFunctionsFactory(util);
                if (clazz != null) {
                    Object utilObj = ReflectUtils.createObject(clazz).getUtilFunctions(context);
                    if (utilObj != null) {
                        binding.setVariable(util, utilObj);
                    }
                }
            }

        }

        GroovyShell shell = new GroovyShell(binding);

        Object value = getValue();

        Object result = shell.evaluate(String.valueOf(value));

        return result;
    }

}
