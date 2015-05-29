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
package org.paxml.control;

import java.util.Arrays;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.el.ExpressionFactory;
import org.paxml.tag.AbstractTagFactory;
import org.paxml.util.AxiomUtils;

/**
 * The iterate tag factory.
 * 
 * @author Xuetao Niu
 * 
 */
public class IterateTagFactory extends AbstractTagFactory<IterateTag> {

    /**
     * The attr name for var name.
     */
    public static final String ATTR_NAME = "name";
    /**
     * The attr name for var.
     */
    public static final String ATTR_VAR = "var";
    /**
     * The attr name for var index.
     */
    public static final String ATTR_INDEX = "index";
    /**
     * The attr name for list.
     */
    public static final String ATTR_LIST = "list";
    /**
     * The attr name for map.
     */
    public static final String ATTR_MAP = "map";
    /**
     * The attr name for bean.
     */
    public static final String ATTR_BEAN = "bean";
    /**
     * The attr name for xpath.
     */
    public static final String ATTR_XPATH = "xpath";
    /**
     * The attr name for times.
     */
    public static final String ATTR_TIMES = "times";
    /**
     * The attr name for values.
     */
    public static final String ATTR_VALUES = "values";
    /**
     * The attr name for values.
     */
    public static final String ATTR_FILE = "file";
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(final IterateTag tag, IParserContext context) {

        super.populate(tag, context);

        final OMElement ele = context.getElement();

        assertNoAttributes(ele, tag.getClass(), ATTR_VALUES, ATTR_VAR, ATTR_BEAN, ATTR_INDEX, ATTR_LIST, ATTR_MAP,
                ATTR_XPATH, ATTR_TIMES, ATTR_NAME, ATTR_FILE);

        String varName = AxiomUtils.getAttribute(ele, ATTR_VAR);
        if (StringUtils.isNotBlank(varName)) {
            tag.setVarName(varName);
        }
        String indexVarName = AxiomUtils.getAttribute(ele, ATTR_INDEX);
        if (StringUtils.isNotBlank(indexVarName)) {
            tag.setIndexVarName(indexVarName);
        }
        String varNameText = AxiomUtils.getAttribute(ele, ATTR_NAME);
        if (StringUtils.isNotBlank(varNameText)) {
            tag.setVarNameText(varNameText);
        }

        String list = AxiomUtils.getAttribute(ele, ATTR_LIST);
        String map = AxiomUtils.getAttribute(ele, ATTR_MAP);
        String bean = AxiomUtils.getAttribute(ele, ATTR_BEAN);
        String xpath = AxiomUtils.getAttribute(ele, ATTR_XPATH);
        String times = AxiomUtils.getAttribute(ele, ATTR_TIMES);
        String values = AxiomUtils.getAttribute(ele, ATTR_VALUES);
        String file = AxiomUtils.getAttribute(ele, ATTR_FILE);

        int c = 0;

        if (StringUtils.isNotBlank(list)) {
            tag.setList(ExpressionFactory.create(list));
            c++;
        }
        if (StringUtils.isNotBlank(map)) {
            tag.setMap(ExpressionFactory.create(map));
            c++;
        }
        if (StringUtils.isNotBlank(bean)) {
            tag.setBean(ExpressionFactory.create(bean));
            c++;
        }
        if (StringUtils.isNotBlank(xpath)) {
            tag.setXpath(ExpressionFactory.create(xpath));
            c++;
        }
        if (StringUtils.isNotBlank(times)) {
            tag.setTimes(ExpressionFactory.create(times));
            c++;
        }
        if (StringUtils.isNotBlank(values)) {
            tag.setValues(ExpressionFactory.create(values));
            c++;
        }
        if (StringUtils.isNotBlank(file)) {
            tag.setFile(ExpressionFactory.create(file));
            c++;
        }
        
        if (c != 1) {

            throw new PaxmlRuntimeException("Only one of these attributes can be specified: "
                    + Arrays.asList(ATTR_LIST, ATTR_MAP, ATTR_XPATH, ATTR_TIMES, ATTR_VALUES, ATTR_FILE));

        }
        return false;
    }
}
