/*
 * Copyright (C) 2021 GS United Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * *****************************************************************************
 *  Project    :   SAP
 *  Class      :   ApplicationActionMap.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 8:59:53 PM
 *  Modified   :   Oct 22, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  ??? ??, 2006  Hans Muller          Initial creation.
 *  Oct 22, 2021  Sean Carrick         Updated to JDK11.
 * *****************************************************************************
 */
package com.gs.platform.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ActionMap;

/**
 * An {@link javax.swing.ActionMap ActionMap} class where each entry corresponds
 * to An@Action` method from a single {
 *
 * `actionsClass` (i.e. a class that contains one or more `&#064;Actions}). Each
 * entry's key is the `&#064;Action's} name (the method name by default), and
 * the value is an {@link ApplicationAction} that calls the `&#064;Actions}
 * method. For example, the code below prints `"Hello World"}:
 * <pre>
 * public class HelloWorldActions {
 *     public &#064;Action void Hello() { System.out.print("Hello "); }
 *     public &#064;Action void World() { System.out.println("World"); }
 * }
 * // ...
 * ApplicationActionMap appAM = new ApplicationActionMap(SimpleActions.class);
 * ActionEvent e = new ActionEvent("no src", ActionEvent.ACTION_PERFORMED, "no cmd");
 * appAM.get("Hello").actionPerformed(e);
 * appAM.get("World").actionPerformed(e);
 * </pre>
 *
 * <p>
 * If a `ResourceMap} is provided then each `ApplicationAction's} ({@link javax.swing.Action#putValue
 * putValue}, {@link javax.swing.Action#getValue getValue}) properties are
 * initialized from the ResourceMap.
 *
 * <p>
 * TBD: explain use of resourcemap including action types, actionsObject,
 * actionsClass, ProxyActions,
 *
 * @see ApplicationAction
 * @see ResourceMap
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ApplicationActionMap extends ActionMap {

    private final ApplicationContext context;
    private final ResourceMap resourceMap;
    private final Class actionsClass;
    private final Object actionsObject;
    private final List<ApplicationAction> proxyActions;

    public ApplicationActionMap(ApplicationContext context, Class actionsClass,
            Object actionsObject, ResourceMap resourceMap) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
        if (actionsClass == null) {
            throw new IllegalArgumentException("null actionsClass");
        }
        if (actionsObject == null) {
            throw new IllegalArgumentException("null actionsObject");
        }
        if (!(actionsClass.isInstance(actionsObject))) {
            throw new IllegalArgumentException("actionsObject not an instanceof "
                    + "actionsClass");
        }
        this.context = context;
        this.actionsClass = actionsClass;
        this.actionsObject = actionsObject;
        this.resourceMap = resourceMap;
        this.proxyActions = new ArrayList<>();
        addAnnotationActions(resourceMap);
        maybeAddActionsPCL();
    }

    public final ApplicationContext getContext() {
        return context;
    }

    public final Class getActionsClass() {
        return actionsClass;
    }

    public final Object getActionsObject() {
        return actionsObject;
    }

    /**
     * All of the `@ProxyActions} recursively defined by this
     * `ApplicationActionMap` and its parent ancestors.
     * <p>
     * Returns a read-only list of the `@ProxyActions} defined by this
     * `ApplicationActionMap's} `actionClass} and, recursively, by this
     * `ApplicationActionMap's} parent. If there are no proxyActions, an empty
     * list is returned.
     *
     * @return a list of all the proxyActions for this `ApplicationActionMap`
     */
    public List<ApplicationAction> getProxyActions() {
        // TBD: proxyActions that shadow should be merged
        ArrayList<ApplicationAction> allProxyActions = new ArrayList<>(proxyActions);
        ActionMap parent = getParent();
        while (parent != null) {
            if (parent instanceof ApplicationActionMap) {
                allProxyActions.addAll(((ApplicationActionMap) parent).proxyActions);
            }
            parent = parent.getParent();
        }
        return Collections.unmodifiableList(allProxyActions);
    }

    private String aString(String s, String emptyValue) {
        return (s.length() == 0) ? emptyValue : s;
    }

    private void putAction(String key, ApplicationAction action) {
        if (get(key) != null) {
            // TBD log a warning - two actions with the same key
        }
        put(key, action);
    }


    /* Add Actions for each actionsClass method with an @Action
     * annotation and for the class's @ProxyActions annotation
     */
    private void addAnnotationActions(ResourceMap resourceMap) {
        Class<?> actionsClass = getActionsClass();
        // @Action 
        for (Method m : actionsClass.getDeclaredMethods()) {
            Action action = m.getAnnotation(Action.class);
            if (action != null) {
                String methodName = m.getName();
                String enabledProperty = aString(action.enabledProperty(), null);
                String selectedProperty = aString(action.selectedProperty(), null);
                String actionName = aString(action.name(), methodName);
                Task.BlockingScope block = action.block();
                ApplicationAction appAction
                        = new ApplicationAction(this, resourceMap, actionName, m, enabledProperty, selectedProperty, block);
                putAction(actionName, appAction);
            }
        }
        // @ProxyActions
        ProxyActions proxyActionsAnnotation = actionsClass.getAnnotation(ProxyActions.class);
        if (proxyActionsAnnotation != null) {
            for (String actionName : proxyActionsAnnotation.value()) {
                ApplicationAction appAction = new ApplicationAction(this, resourceMap, actionName);
                appAction.setEnabled(false); // will track the enabled property of the Action it's bound to
                putAction(actionName, appAction);
                proxyActions.add(appAction);
            }
        }
    }

    /* If any of the ApplicationActions need to track an 
     * enabled or selected property defined in actionsClass, then add our 
     * PropertyChangeListener.  If none of the @Actions in actionClass
     * provide an enabledProperty or selectedProperty argument, then
     * we don't need to do this.
     */
    private void maybeAddActionsPCL() {
        boolean needsPCL = false;
        Object[] keys = keys();
        if (keys != null) {
            for (Object key : keys) {
                javax.swing.Action value = get(key);
                if (value instanceof ApplicationAction) {
                    ApplicationAction actionAdapter = (ApplicationAction) value;
                    if ((actionAdapter.getEnabledProperty() != null)
                            || (actionAdapter.getSelectedProperty() != null)) {
                        needsPCL = true;
                        break;
                    }
                }
            }
            if (needsPCL) {
                try {
                    Class actionsClass = getActionsClass();
                    Method m = actionsClass.getMethod("addPropertyChangeListener",
                            PropertyChangeListener.class);
                    m.invoke(getActionsObject(), new ActionsPCL());
                } catch (IllegalAccessException | IllegalArgumentException
                        | NoSuchMethodException | SecurityException
                        | InvocationTargetException e) {
                    String s = "addPropertyChangeListener undefined " + actionsClass;
                    throw new Error(s, e);
                }
            }
        }
    }

    /* When the value of an actionsClass @Action enabledProperty or 
     * selectedProperty changes, forward the PropertyChangeEvent to 
     * the ApplicationAction object itself.
     */
    private class ActionsPCL implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String propertyName = event.getPropertyName();
            Object[] keys = keys();
            if (keys != null) {
                for (Object key : keys) {
                    javax.swing.Action value = get(key);
                    if (value instanceof ApplicationAction) {
                        ApplicationAction appAction = (ApplicationAction) value;
                        if (propertyName.equals(appAction.getEnabledProperty())) {
                            appAction.forwardPropertyChangeEvent(event, "enabled");
                        } else if (propertyName.equals(appAction.getSelectedProperty())) {
                            appAction.forwardPropertyChangeEvent(event, "selected");
                        }
                    }
                }
            }
        }
    }
}
