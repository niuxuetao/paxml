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
package org.paxml.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.paxml.annotation.RootTag;
import org.paxml.tag.DataSetEntityFactory;
import org.paxml.tag.ScenarioEntityFactory;
import org.paxml.tag.SpringXmlEntityFactory;
import org.paxml.tag.plan.PlanEntityFactory;

/**
 * Registry for paxml entity factories. Keyed by root xml tag of the resources.
 * 
 * @author Xuetao Niu
 * 
 */
public class EntityFactoryRegistry {

    private final Map<String, IEntityFactory> registry = new ConcurrentHashMap<String, IEntityFactory>();

    /**
     * Create a default registry.
     * 
     * @return the new default registry.
     */
    public static EntityFactoryRegistry getDefaultRegistry() {
        EntityFactoryRegistry registry = new EntityFactoryRegistry();
        registry.register(new ScenarioEntityFactory());
        registry.register(new DataSetEntityFactory());
        registry.register(new SpringXmlEntityFactory());
        registry.register(new PlanEntityFactory());
        
        return registry;
    }

    /**
     * Register an paxml entity factory.
     * 
     * @param factory
     *            the factory instance which must have its class annotated by
     * @RootTag.
     */
    public void register(IEntityFactory factory) {
        RootTag tag = factory.getClass().getAnnotation(RootTag.class);
        String tagName = tag == null ? null : tag.value();
        if (StringUtils.isBlank(tagName)) {
            throw new PaxmlRuntimeException(factory.getClass().getName() + " has no tag value given in the @"
                    + RootTag.class.getSimpleName() + " annotation");
        }
        registry.put(tagName, factory);
    }

    /**
     * Get the registered paxml entity factory with root tag name.
     * 
     * @param rootTag
     *            the root tag name
     * @return null if not found, otherwise the factory instance.
     */
    public IEntityFactory lookup(String rootTag) {
        return registry.get(rootTag);
    }
    
}
