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
 *  Class      :   ResourceManager.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 8:44:56 PM
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

import com.gs.platform.utils.LogRecord;
import com.gs.platform.utils.Logger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The application's `ResourceManager} provides read-only cached access to
 * resources in `ResourceBundles} via the {@link ResourceMap ResourceMap} class.
 * `ResourceManager} is a property of the `ApplicationContext` and most
 * applications look up resources relative to it, like this:
 * <pre>
 * ApplicationContext appContext = Application.getInstance().getContext();
 * ResourceMap resourceMap = appContext.getResourceMap(MyClass.class);
 * String msg = resourceMap.getString("msg");
 * Icon icon = resourceMap.getIcon("icon");
 * Color color = resourceMap.getColor("color");
 * </pre>
 * {@link ApplicationContext#getResourceMap(Class) ApplicationContext.getResourceMap()}
 * just delegates to its `ResourceManager}. The `ResourceMap} in this example
 * contains resources from the ResourceBundle named `MyClass}, and the rest of
 * the chain contains resources shared by the entire application.
 * <p>
 * Resources for a class are defined by an eponymous `ResourceBundle` in a
 * `resources} subpackage. The Application class itself may also provide
 * resources. A complete description of the naming conventions for
 * ResourceBundles is provided by the
 * {@link #getResourceMap(Class) getResourceMap()} method.
 * <p>
 * The mapping from classes and `Application` to a list ResourceBundle names is
 * handled by two protected methods: null {@link #getClassBundleNames(Class)
 * getClassBundleNames}, {@link #getApplicationBundleNames()
 * getApplicationBundleNames}. Subclasses could override these methods to append
 * additional ResourceBundle names to the default lists.
 *
 * @see ApplicationContext#getResourceManager
 * @see ApplicationContext#getResourceMap
 * @see ResourceMap
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ResourceManager extends AbstractBean {

    private static final LogRecord record = new LogRecord(ResourceManager.class.getSimpleName());
    private static final Logger logger = Logger.getLogger(Application.getInstance(), Logger.TRACE);
    private final Map<String, ResourceMap> resourceMaps;
    private final ApplicationContext context;
    private List<String> applicationBundleNames = null;
    private ResourceMap appResourceMap = null;

    /**
     * Construct a `ResourceManager}. Typically applications will not create a
     * ResourceManager directly, they'll retrieve the shared one from the
     * `ApplicationContext` with:
     * <pre>
     * Application.getInstance().getContext().getResourceManager()
     * </pre> Or just look up `ResourceMaps} with the ApplicationContext
     * convenience method:
     * <pre>
     * Application.getInstance().getContext().getResourceMap(MyClass.class)
     * </pre>
     *
     * @param context The context in which this `ResourceManager` is being used
     *
     * @see ApplicationContext#getResourceManager
     * @see ApplicationContext#getResourceMap
     */
    protected ResourceManager(ApplicationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
        this.context = context;
        resourceMaps = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the `ApplicationContext` singleton for which this
     * `ResourceManager` instance is being used.
     *
     * @return the current `ApplicationContext`
     */
    protected final ApplicationContext getContext() {
        return context;
    }

    /* Returns a read-only list of the ResourceBundle names for all of
     * the classes from startClass to (including) stopClass.  The
     * bundle names for each class are #getClassBundleNames(Class).
     * The list is in priority order: resources defined in bundles
     * earlier in the list shadow resources with the same name that
     * appear bundles that come later.
     */
    private List<String> allBundleNames(Class startClass, Class stopClass) {
        List<String> bundleNames = new ArrayList<>();
        Class limitClass = stopClass.getSuperclass(); // could be null
        for (Class c = startClass; c != limitClass; c = c.getSuperclass()) {
            bundleNames.addAll(getClassBundleNames(c));
        }
        return Collections.unmodifiableList(bundleNames);
    }

    private String bundlePackageName(String bundleName) {
        int i = bundleName.lastIndexOf(".");
        return (i == -1) ? "" : bundleName.substring(0, i);
    }

    /* Creates a parent chain of ResourceMaps for the specfied
     * ResourceBundle names.  One ResourceMap is created for each
     * subsequence of ResourceBundle names with a common bundle
     * package name, i.e. with a common resourcesDir.  The parent 
     * of the final ResourceMap in the chain is root.
     */
    private ResourceMap createResourceMapChain(ClassLoader cl, ResourceMap root,
            ListIterator<String> names) {
        if (!names.hasNext()) {
            return root;
        } else {
            String bundleName0 = names.next();
            String rmBundlePackage = bundlePackageName(bundleName0);
            List<String> rmNames = new ArrayList<>();
            rmNames.add(bundleName0);
            while (names.hasNext()) {
                String bundleName = names.next();
                if (rmBundlePackage.equals(bundlePackageName(bundleName))) {
                    rmNames.add(bundleName);
                } else {
                    names.previous();
                    break;
                }
            }
            ResourceMap parent = createResourceMapChain(cl, root, names);
            return createResourceMap(cl, parent, rmNames);
        }
    }

    /* Lazily creates the Application ResourceMap chain,
     * appResourceMap.  If the Application hasn't been launched yet,
     * i.e. if the ApplicationContext applicationClass property hasn't
     * been set yet, then the ResourceMap just corresponds to
     * Application.class.
     */
    private ResourceMap getApplicationResourceMap() {
        if (appResourceMap == null) {
            List<String> appBundleNames = getApplicationBundleNames();
            Class appClass = getContext().getApplicationClass();
            if (appClass == null) {
                String msg = "getApplicationResourceMap(): no Application "
                        + "class";
                record.setInstant(Instant.now());
                record.setMessage(msg);
                record.setParameters(null);
                record.setSourceMethodName("getApplicationResourceMap");
                Long tid = Thread.currentThread().getId();
                record.setThreadID(tid.intValue());
                logger.warn(record);
                appClass = Application.class;
            }
            ClassLoader classLoader = appClass.getClassLoader();
            appResourceMap = createResourceMapChain(classLoader, null,
                    appBundleNames.listIterator());
        }
        return appResourceMap;
    }

    /* Lazily creates the ResourceMap chain for the the class from 
     * startClass to stopClass.
     */
    private ResourceMap getClassResourceMap(Class startClass, Class stopClass) {
        String classResourceMapKey = startClass.getName() + stopClass.getName();
        ResourceMap classResourceMap = resourceMaps.get(classResourceMapKey);
        if (classResourceMap == null) {
            List<String> classBundleNames = allBundleNames(startClass, stopClass);
            ClassLoader classLoader = startClass.getClassLoader();
            ResourceMap appRM = getResourceMap();
            classResourceMap = createResourceMapChain(classLoader, appRM,
                    classBundleNames.listIterator());
            resourceMaps.put(classResourceMapKey, classResourceMap);
        }
        return classResourceMap;
    }

    /**
     * Returns a {@link ResourceMap#getParent chain} of `ResourceMaps} that
     * encapsulate the `ResourceBundles} for each class from `startClass} to
     * (including) `stopClass}. The final link in the chain is Application
     * ResourceMap chain, i.e. the value of
     * {@link #getResourceMap() getResourceMap()}.
     * <p>
     * The ResourceBundle names for the chain of ResourceMaps are defined by
     * {@link #getClassBundleNames} and {@link #getApplicationBundleNames}.
     * Collectively they define the standard location for `ResourceBundles} for
     * a particular class as the `resources} subpackage. For example, the
     * ResourceBundle for the single class `com.myco.MyScreen}, would be named
     * `com.myco.resources.MyScreen}. Typical ResourceBundles are ".properties"
     * files, so: ` com/foo/bar/resources/MyScreen.properties}. The following
     * table is a list of the ResourceMaps and their constituent ResourceBundles
     * for the same example:
     * <p>
     * <table border="1">
     * <caption><em>ResourceMap chain for class MyScreen in MyApp</em></caption>
     * <tr>
     * <th></th>
     * <th>ResourceMap</th>
     * <th>ResourceBundle names</th>
     * <th>Typical ResourceBundle files</th>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>class: com.myco.MyScreen</td>
     * <td>com.myco.resources.MyScreen</td>
     * <td>com/myco/resources/MyScreen.properties</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>application: com.myco.MyApp</td>
     * <td>com.myco.resources.MyApp</td>
     * <td>com/myco/resources/MyApp.properties</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>application: javax.swing.application.Application</td>
     * <td>javax.swing.application.resources.Application</td>
     * <td>javax.swing.application.resources.Application.properties</td>
     * </tr>
     * </table>
     *
     * <p>
     * None of the ResourceBundles are required to exist. If more than one
     * ResourceBundle contains a resource with the same name then the one
     * earlier in the list has precedence
     * <p>
     * ResourceMaps are constructed lazily and cached. One ResourceMap is
     * constructed for each sequence of classes in the same package.
     *
     * @param startClass the first class whose ResourceBundles will be included
     * @param stopClass the last class whose ResourceBundles will be included
     * @return a `ResourceMap} chain that contains resources loaded from
     * `ResourceBundles} found in the resources subpackage for each class.
     * @see #getClassBundleNames
     * @see #getApplicationBundleNames
     * @see ResourceMap#getParent
     * @see ResourceMap#getBundleNames
     */
    public ResourceMap getResourceMap(Class startClass, Class stopClass) {
        if (startClass == null) {
            throw new IllegalArgumentException("null startClass");
        }
        if (stopClass == null) {
            throw new IllegalArgumentException("null stopClass");
        }
        if (!stopClass.isAssignableFrom(startClass)) {
            throw new IllegalArgumentException("startClass is not a subclass, "
                    + "or the same as, stopClass");
        }
        return getClassResourceMap(startClass, stopClass);
    }

    /**
     * Return the ResourcedMap chain for the specified class. This is just a
     * convenince method, it's the same as:
     * <code>getResourceMap(cls, cls)</code>.
     *
     * @param cls the class that defines the location of ResourceBundles
     * @return a `ResourceMap} that contains resources loaded from
     * `ResourceBundles} found in the resources subpackage of the specified
     * class's package.
     * @see #getResourceMap(Class, Class)
     */
    public final ResourceMap getResourceMap(Class cls) {
        if (cls == null) {
            throw new IllegalArgumentException("null class");
        }
        return getResourceMap(cls, cls);
    }

    /**
     * Returns the chain of ResourceMaps that's shared by the entire
     * application, beginning with the resources defined for the application's
     * class, i.e. the value of the ApplicationContext
     * {@link ApplicationContext#getApplicationClass applicationClass} property.
     * If the `applicationClass} property has not been set, e.g. because the
     * application has not been {@link Application#launch launched} yet, then a
     * ResourceMap for just `Application.class` is returned.
     *
     * @return the Application's ResourceMap
     * @see ApplicationContext#getResourceMap()
     * @see ApplicationContext#getApplicationClass
     */
    public ResourceMap getResourceMap() {
        return getApplicationResourceMap();
    }

    /**
     * The names of the ResourceBundles to be shared by the entire application.
     * The list is in priority order: resources defined by the first
     * ResourceBundle shadow resources with the the same name that come later.
     * <p>
     * The default value for this property is a list of {@link
     * #getClassBundleNames per-class} ResourceBundle names, beginning with the
     * `Application's} class and of each of its superclasses, up to
     * `Application.class`. For example, if the Application's class was
     * `com.foo.bar.MyApp}, and MyApp was a subclass of
     * `SingleFrameApplication.class}, then the ResourceBundle names would be:
     * <ol>
     * <li>com.foo.bar.resources.MyApp</li>
     * <li>javax.swing.application.resources.SingleFrameApplication</li>
     * <li>javax.swing.application.resources.Application</li>
     * </ol>
     * <p>
     * The default value of this property is computed lazily and cached. If it's
     * reset, then all ResourceMaps cached by `getResourceMap} will be updated.
     *
     * @return a `java.util.List` of the application's bundle names
     * 
     * @see #setApplicationBundleNames
     * @see #getResourceMap
     * @see #getClassBundleNames
     * @see ApplicationContext#getApplication
     */
    public List<String> getApplicationBundleNames() {
        /* Lazily compute an initial value for this property, unless the
	 * application's class hasn't been specified yet.  In that case
	 * we just return a placeholder based on Application.class.
         */
        if (applicationBundleNames == null) {
            Class appClass = getContext().getApplicationClass();
            if (appClass == null) {
                return allBundleNames(Application.class, Application.class);
// placeholder
            } else {
                applicationBundleNames = allBundleNames(appClass,
                        Application.class);
            }
        }
        return applicationBundleNames;
    }

    /**
     * Specify the names of the ResourceBundles to be shared by the entire
     * application. More information about the property is provided by the
     * {@link #getApplicationBundleNames} method.
     *
     * @param bundleNames the application's bundle names as a `java.util.List`
     * 
     * @see #setApplicationBundleNames
     */
    public void setApplicationBundleNames(List<String> bundleNames) {
        if (bundleNames != null) {
            bundleNames.stream().filter(bundleName -> {
                return (bundleName == null) 
                        || (bundleNames.isEmpty());
            }).forEachOrdered(bundleName -> {
                throw new IllegalArgumentException("invalid bundle name \""
                        + bundleName + "\"");
            });
        }
        Object oldValue = applicationBundleNames;
        if (bundleNames != null) {
            applicationBundleNames = Collections.unmodifiableList(new ArrayList(
                    bundleNames));
        } else {
            applicationBundleNames = null;
        }
        resourceMaps.clear();
        firePropertyChange("applicationBundleNames", oldValue,
                applicationBundleNames);
    }

    /* Convert a class name to an eponymous resource bundle in the 
     * resources subpackage.  For example, given a class named
     * com.foo.bar.MyClass, the ResourceBundle name would be
     * "com.foo.bar.resources.MyClass"  If MyClass is an inner class,
     * only its "simple name" is used.  For example, given an
     * inner class named com.foo.bar.OuterClass$InnerClass, the
     * ResourceBundle name would be "com.foo.bar.resources.InnerClass".
     * Although this could result in a collision, creating more
     * complex rules for inner classes would be a burden for
     * developers.
     */
    private String classBundleBaseName(Class cls) {
        String className = cls.getName();
        StringBuilder sb = new StringBuilder();
        int i = className.lastIndexOf('.');
        if (i > 0) {
            sb.append(className.substring(0, i));
            sb.append(".resources.");
            sb.append(cls.getSimpleName());
        } else {
            sb.append("resources.");
            sb.append(cls.getSimpleName());
        }
        return sb.toString();
    }

    /**
     * Map from a class to a list of the names of the `ResourceBundles} specific
     * to the class. The list is in priority order: resources defined by the
     * first ResourceBundle shadow resources with the the same name that come
     * later.
     * <p>
     * By default this method returns one ResourceBundle whose name is the same
     * as the class's name, but in the `"resources"} subpackage.
     * <p>
     * For example, given a class named `com.foo.bar.MyClass}, the
     * ResourceBundle name would be `"com.foo.bar.resources.MyClass"}. If
     * MyClass is an inner class, only its "simple name" is used. For example,
     * given an inner class named `com.foo.bar.OuterClass$InnerClass}, the
     * ResourceBundle name would be `"com.foo.bar.resources.InnerClass"}.
     * <p>
     * This method is used by the `getResourceMap} methods to compute the list
     * of ResourceBundle names for a new `ResourceMap}. ResourceManager
     * subclasses can override this method to add additional class-specific
     * ResourceBundle names to the list.
     *
     * @param cls the named ResourceBundles are specific to `cls}.
     * @return the names of the ResourceBundles to be loaded for `cls}
     * @see #getResourceMap
     * @see #getApplicationBundleNames
     */
    protected List<String> getClassBundleNames(Class cls) {
        String bundleName = classBundleBaseName(cls);
        return Collections.singletonList(bundleName);
    }

    /**
     * Called by {@link #getResourceMap} to construct `ResourceMaps}.By default
     * this method is effectively just:<pre>
     * return new ResourceMap(parent, classLoader, bundleNames);
     * </pre> Custom ResourceManagers might override this method to construct
     * their own ResourceMap subclasses.
     * 
     * @param classLoader the `ClassLoader` to use for loading resources
     * @param parent the parent for this `ResourceMap`
     * @param bundleNames a `java.util.List` of the bundle names
     * @return a `ResourceMap` of the resources
     */
    protected ResourceMap createResourceMap(ClassLoader classLoader,
            ResourceMap parent, List<String> bundleNames) {
        return new ResourceMap(parent, classLoader, bundleNames);
    }

    /**
     * The value of the special Application ResourceMap resource named
     * "platform". By default the value of this resource is "osx" if the
     * underlying operating environment is Apple OSX or "default".
     *
     * @return the value of the platform resource
     * @see #setPlatform
     */
    public String getPlatform() {
        return getResourceMap().getString("platform");
    }

    /**
     * Defines the value of the special Application ResourceMap resource named
     * "platform". This resource can be used to define platform specific
     * resources. For example:
     * <pre>
     * myLabel.text.osx = A value that's appropriate for OSX
     * myLabel.text.default = A value for other platforms
     * myLabel.text = myLabel.text.${platform}
     * </pre>
     * <p>
     * By default the value of this resource is "osx" if the underlying
     * operating environment is Apple OSX or "default". To distinguish other
     * platforms one can reset this property based on the value of the
     * `"os.name"} system property.
     * <p>
     * This method should be called as early as possible, typically in the
     * Application {@link Application#initialize initialize} method.
     *
     * @param platform the platform on which the application is running
     * 
     * @see #getPlatform
     * @see System#getProperty
     */
    public void setPlatform(String platform) {
        if (platform == null) {
            throw new IllegalArgumentException("null platform");
        }
        getResourceMap().putResource("platform", platform);
    }
}
