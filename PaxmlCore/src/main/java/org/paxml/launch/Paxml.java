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
package org.paxml.launch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.paxml.core.IEntityExecutionListener;
import org.paxml.core.IEntity;
import org.paxml.core.IExecutionListener;
import org.paxml.core.ITagExecutionListener;
import org.paxml.core.EntityFactoryRegistry;
import org.paxml.core.Parser;
import org.paxml.core.PaxmlResource;
import org.paxml.core.ResourceLocator;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.core.Context.Stack.IStackTraverser;
import org.paxml.tag.AbstractTag;
import org.paxml.tag.ITag;
import org.paxml.tag.ITagLibrary;
import org.paxml.tag.invoker.FileInvokerTag;
import org.paxml.util.ReflectUtils;
import org.springframework.core.io.Resource;

/**
 * paxml operations starting point.
 * 
 * @author Xuetao Niu
 * 
 */
public class Paxml {

    private static final Log log = LogFactory.getLog(Paxml.class);

    private final Parser parser;
    private final long processId;
    private volatile List<IExecutionListener> paxmlExecutionListeners;
    private volatile List<IEntityExecutionListener> entityExecutionListeners;
    private volatile List<ITagExecutionListener> tagExecutionListeners;

    /**
     * Construct from default paxml entity registry.
     * 
     * @param processId
     *            the processId
     */
    public Paxml(long processId) {
        this(EntityFactoryRegistry.getDefaultRegistry(), processId);
    }

    /**
     * Construct from given paxml entity registry.
     * 
     * @param reg
     *            the registry
     * @param processId
     *            the processId
     */
    public Paxml(final EntityFactoryRegistry reg, long processId) {
        parser = new Parser(this, reg, new ResourceLocator());
        this.processId = processId;
    }

    /**
     * Add a static config to this.
     * 
     * @param config
     *            the config
     */
    public void addStaticConfig(StaticConfig config) {
        // add resources
        getResourceLocator().addResources(config.getResources());

        // add tag libs
        for (Class<? extends ITagLibrary> lib : config.getTagLibs()) {
            addTagLibrary(lib);
        }

        // add listeners
        for (Class<? extends IExecutionListener> lis : config.getExecutionListeners()) {
            addPaxmlExecutionListener(ReflectUtils.createObject(lis));
        }
        for (Class<? extends IEntityExecutionListener> lis : config.getEntityListeners()) {
            addEntityExecutionListener(ReflectUtils.createObject(lis));
        }
        for (Class<? extends ITagExecutionListener> lis : config.getTagListeners()) {
            addTagExecutionListener(ReflectUtils.createObject(lis));
        }

    }

    public ResourceLocator getResourceLocator() {
        return parser.getResourceLocator();
    }

    /**
     * Execute a paxml resource.
     * 
     * @param name
     *            the resource name
     * @param initialProperties
     *            the initial properties
     * @return the execution result
     */
    public Object execute(String name, Properties topLevelProperties, Properties initialProperties) {
        IEntity entity = getEntity(name);
        if (entity == null) {
            throw new PaxmlRuntimeException("Entity with name '" + name + "' unknown!");
        }
        return execute(entity, topLevelProperties, initialProperties);
    }

    /**
     * Get paxml entity by name, triggering the parse if needed.
     * 
     * @param name
     *            the tag name
     * @return the parsed entity, or null if not found.
     */
    public IEntity getEntity(String name) {
        return parser.getResourceLocator().getEntity(name, null);
    }

    /**
     * Execute a paxml entity.
     * 
     * @param entity
     *            the paxml entity
     * @param topLevelProperties
     *            the top level properties.
     * @param initialProperties
     *            the initial properties.
     * @return the execution result
     */
    public Object execute(IEntity entity, Properties topLevelProperties, Properties initialProperties) {
        Context context = new Context(topLevelProperties, processId);
        if (initialProperties != null) {
            context = new Context(context);
            context.addProperties(initialProperties);
        }
        return execute(entity, context, true, true);
    }

    /**
     * Execute a paxml entity.
     * 
     * @param entity
     *            the paxml entity
     * @param rootContext
     *            the root context, null to automatically create one.
     * @param callEntryListener
     *            if to call the entity entry listener or not
     * @param callExitListener
     *            if to call the entity exit listener or not
     * 
     * 
     * @return the execution result
     */
    public Object execute(IEntity entity, Context rootContext, boolean callEntryListener, boolean callExitListener) {

        final Context propertiesContext = rootContext == null ? new Context(System.getProperties(), processId)
                : rootContext;
        final Context context = new Context(propertiesContext);
        context.setAsCurrentThreadContext();

        context.setPaxml(this);
        context.setPaxmlExecutionListeners(paxmlExecutionListeners);
        context.setEntityExecutionListeners(entityExecutionListeners);
        context.setTagExecutionListeners(tagExecutionListeners);

        if (propertiesContext.isRoot()) {
            if (log.isInfoEnabled()) {
                log.info("Starting process " + processId + " to execute scenario: " + entity.getResource().getPath());
            }

        }

        try {
            if (callEntryListener && paxmlExecutionListeners != null) {
                for (IExecutionListener listener : context.getPaxmlExecutionListeners(false)) {
                    listener.onEntry(this, context);
                }
            }

            Object result = entity.execute(context);
            return result;
        } catch (Throwable e) {
            String msg = getCause(e);
            throw new PaxmlRuntimeException((msg == null ? "" : msg), e);

        } finally {
            if (callExitListener) {
                List<Throwable> exceptions = callEntityExitListener(context);
                if (exceptions != null && exceptions.size() > 0) {
                    if (log.isErrorEnabled()) {
                        log.error(exceptions.size() + " error(s) executing paxml exit listener:");
                        int i = 1;
                        for (Throwable t : exceptions) {
                            log.error("Error number " + (i++), t);
                        }
                    }
                    throw new PaxmlRuntimeException(
                            "Exception(s) occurred while calling the onExit() method(s) on paxml execution listener(s). "
                                    + "See the error log for details.", null);
                }
            }
        }

    }

    public List<Throwable> callEntityExitListener(Context context) {
        List<Throwable> exceptions = null;
        paxmlExecutionListeners = context.getPaxmlExecutionListeners(false);
        if (paxmlExecutionListeners != null) {
            for (IExecutionListener listener : paxmlExecutionListeners) {
                try {
                    listener.onExit(this, context);
                } catch (Throwable e) {
                    if (exceptions == null) {
                        exceptions = new ArrayList<Throwable>();
                    }

                    exceptions.add(e);
                    if (log.isErrorEnabled()) {
                        log.error("Exception in the end of paxml execution", e);
                    }
                }
            }
        }
        return exceptions;
    }

    public static String getCause(Throwable t) {
        String msg = null;

        while (t != null && StringUtils.isNotBlank((msg = t.getMessage()))) {
            t = t.getCause();
        }
        return msg;
    }

    /**
     * Add tag library.
     * 
     * @param classes
     *            tag library classes.
     * @return this
     */
    public Paxml addTagLibrary(Class<? extends ITagLibrary>... classes) {
        for (Class<? extends ITagLibrary> clazz : classes) {
            ITagLibrary lib = ReflectUtils.createObject(clazz);
            // let later libs overwrite earlier ones, the system default lib is the earliest one
            parser.addTagLibrary(lib, false);
        }
        return this;
    }

    /**
     * Add paxml resources.
     * 
     * @param resources
     *            the resources.
     * @return this
     */
    public Paxml addResources(PaxmlResource... resources) {
        for (PaxmlResource resource : resources) {
            parser.getResourceLocator().addResource(resource);
        }
        return this;
    }

    /**
     * Add paxml resources.
     * 
     * @param resources
     *            the resources
     * @return this
     */
    public Paxml addResources(Collection<PaxmlResource> resources) {
        for (PaxmlResource resource : resources) {
            parser.getResourceLocator().addResource(resource);
        }
        return this;
    }

    /**
     * Remove paxml resources.
     * 
     * @param resources
     *            the resources
     * @return this
     */
    public Paxml removeResources(Collection<PaxmlResource> resources) {
        for (PaxmlResource resource : resources) {
            parser.getResourceLocator().removeResource(resource);
        }
        return this;
    }

    /**
     * Remove paxml resources.
     * 
     * @param resources
     *            the resources
     * @return this
     */
    public Paxml removeResources(PaxmlResource... resources) {
        for (PaxmlResource resource : resources) {
            parser.getResourceLocator().removeResource(resource);
        }
        return this;
    }

    /**
     * Inspect a parsed paxml entity.
     * 
     * @param name
     *            the name of the paxml entity
     * @return the printed xml tree in string.
     */
    public String inspectEntity(String name) {
        AbstractTag tag = ((AbstractTag) parser.getResourceLocator().getEntity(name, null));
        if (tag == null) {
            throw new PaxmlRuntimeException("No resource found from locator: "
                    + parser.getResourceLocator().getResourceNames());
        }
        return tag.printTree(0);
    }

    /**
     * Add paxml execution listener.
     * 
     * @param listener
     *            the listener
     */
    public void addPaxmlExecutionListener(IExecutionListener listener) {
        if (paxmlExecutionListeners == null) {
            paxmlExecutionListeners = new ArrayList<IExecutionListener>(1);
        }
        paxmlExecutionListeners.add(listener);
    }

    /**
     * Add entity execution listener.
     * 
     * @param listener
     *            the listener
     */
    public void addEntityExecutionListener(IEntityExecutionListener listener) {
        if (entityExecutionListeners == null) {
            entityExecutionListeners = new ArrayList<IEntityExecutionListener>(1);
        }
        entityExecutionListeners.add(listener);
    }

    /**
     * Add tag execution listener.
     * 
     * @param listener
     *            the listener
     */
    public void addTagExecutionListener(ITagExecutionListener listener) {
        if (tagExecutionListeners == null) {
            tagExecutionListeners = new ArrayList<ITagExecutionListener>(1);
        }
        tagExecutionListeners.add(listener);
    }

    public Parser getParser() {
        return parser;
    }

    public static LaunchModel executePlanFile(String planFile, Properties props) {

        Resource res = Parser.getResource(planFile, null);

        LaunchModel model = new LaunchModelBuilder().build(res, props == null ? new Properties() : props);

        return model;
    }

    public long getProcessId() {
        return processId;
    }

}
