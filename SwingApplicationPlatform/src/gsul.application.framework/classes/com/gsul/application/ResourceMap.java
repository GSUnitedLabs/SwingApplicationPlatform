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
 *  Project    :   SwingApplicationPlatform
 *  Class      :   ResourceMap.java
 *  Author     :   Sean Carrick
 *  Created    :   Aug 18, 2021 @ 12:21:19 PM
 *  Modified   :   Aug 18, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Aug 18, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gsul.application;

import com.gsul.utils.TerminalErrorPrinter;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A read-only encapsulation of one or more {@code ResourceBundle}s that adds
 * automatic string conversion, support for field and Swing component property
 * injection, string resource variable substitution, and chaining.
 * <dl>
 * <dt><strong><em>Copyright Notice</em></strong></dt>
 * <dd>This class has been modified with the permission of the original author,
 * Hans Muller. This class was originally written for the JSR-296 Swing
 * Application Framework, using JDK6 in 2006, at Sun Microsystems.<br><br>This
 * work is an adaptation and update of that original work.</dd></dl>
 * <p>
 * {@code ResourceMap}s are typically obtained with the
 * {@code ApplicationContext}
 * {@link ApplicationContext#getResourceMap getResourceMap} method which lazily
 * creates per Application, package, and class ResourceMaps that are linked
 * together with the {@code ResourceMap} {@code parent} property.</p>
 * <p>
 * An individual {@code ResourceMap} provides read-only access to all of the
 * resources defined by the {@code ResourceBundle} named when the {@code
 * ResourceMap} was created as well as all of its parent {@code ResourceMap}s.
 * Resources are retrieved with the {@code getObject} method which requires both
 * the name of the resource and its expected type. The latter is used to convert
 * strings if necessary. Converted values are cached. As a convenience, {@code
 * getObject} wrapper methods for common GUI types, like {@code getFont}, and
 * {@code getColor}, are provided.</p>
 * <p>
 * The {@code getObject} method scans raw string resource values for {@code
 * ${resourceName}} variable substitutions before performing string conversion.
 * Variable named this way can refer to String resources defined anywhere in
 * their {@code ResourceMap} or any parent {@code ResourceMap}. The special
 * variable {@code ${null}} means that the value of the resource will be {@code
 * null}.</p>
 * <p>
 * {@code ResourceMap}s can be used to "inject" resource values into Swing
 * component properties and into object fields. The {@code injectComponents}
 * method uses Component names ({@link Component#setName}) to match resource
 * names with properties. The {@code injectFields} method sets fields that have
 * been tagged with the {@code &#064;Resource} annotation to have the value of
 * the resources with the same name.</p>
 *
 * @author Hans Muller &ndash; Original JSR-296 Framework author
 * @author Sean Carrick &ndash; Modular Java Swing Application Platform author
 *
 * @version 1.0.0
 * @since 1.0.0
 *
 * @see #injectComponents(java.awt.Component)
 * @see #injectFields(java.lang.Object)
 * @see ResourceConverter
 * @see ResourceBundle
 */
public class ResourceMap {

    private static final Logger logger = Logger.getLogger(ResourceMap.class.getName());
    private static final Object nullResource = "null resource";
    private final ClassLoader classLoader;
    private final ResourceMap parent;
    private final Map<Class, ResourceBundle> resourceBundles;
    private final List<String> bundleNames;
    private final String resourcesDir;
    private Map<String, Object> bundlesMapP;    // See getBundlesMap()
    private Locale locale = Locale.getDefault();
    private Set<String> bundlesMapKeysP = null; // See getBundlesMapKeys()
    private boolean bundlesLoaded = false;  // ResourceBundles are loaded lazily

    /**
     * Creates a ResourceMap that contains all fo the resources defined in the
     * named {@link ResourceBundle}s as well as (recursively) the {@code parent}
     * ResourceMap. The {@code parent} may be {@code null}. Typically, just one
     * ResourceBundle is specified, however, one might name additional
     * ResourceBundles that contain platform or Swing look and feel specific
     * resources. When multiple bundles are named, a resource defined in
     * bundle<sub>n</sub> will override the same resource defined in
     * bundles<sub>0&hellip;n-1</sub>. In other words, bundles named later in
     * the argument list take precedence over the bundles named earlier.</p>
     * <p>
     * ResourceBundles are loaded with the specified ClassLoader. If {@code
     * classLoader} is {@code null}, an IllegalArgumentException is thrown.</p>
     * <p>
     * At least one bundleName must be specified and all of the bundleNames must
     * be non-empty strings, or an IllegalArgumentException is thrown. The
     * bundles are listed in priority order, highest priority first. In other
     * words, resources in the ResourceBundle named first, shadow resources with
     * the same name later in the list.</p>
     * <p>
     * TODO: Determine if we need to enforce the common package prefix rule for
     * ResourceBundles. All of the bundleNames must share a common package
     * prefix. The package prefix implicitly specifies the resources directory
     * (see {@link
     * #getResourcesDir}). For example, the resources directory for bundle names
     * {@code myapp.resources.foo} and {@code myapp.resources.bar} would be
     * {@code myapp/resources/}. If bundle names do not share a common package
     * prefix, then an IllegalArgumentException is thrown.</p>
     *
     * @param parent parent ResourceMap or {@code null}
     * @param classLoader the ClassLoader to be used to load the ResourceBundle
     * @param bundleNames names of the ResourceBundle(s) to be loaded
     * @throws IllegalArgumentException if classLoader or any bundleName is
     * {@code null}, if no bundleNames are specified, if any bundleName is an
     * empty (zero length) String, or if all of the bundleNames do not have a
     * common package prefix
     *
     * @see ResourceBundle
     * @see #getParent()
     * @see #getClassLoader()
     * @see #getResourcesDir()
     * @see #getBundleNames()
     */
    public ResourceMap(ResourceMap parent, ClassLoader classLoader,
            List<String> bundleNames) {
        if (classLoader == null) {
            IllegalArgumentException e = new IllegalArgumentException("null "
                    + "classLoader");
            TerminalErrorPrinter.printException(getClass().getName()
                    + " (constructor)", e);
            throw e;
        }
        if ((bundleNames == null) || (bundleNames.isEmpty())) {
            IllegalArgumentException e = new IllegalArgumentException("no "
                    + "bundle specified");
            TerminalErrorPrinter.printException(getClass().getName()
                    + " (constructor)", e);
            throw e;
        }
        bundleNames.stream().filter(bn -> ((bn == null)
                || (bn.length() == 0))).forEachOrdered(bn -> {
            IllegalArgumentException e = new IllegalArgumentException(
                    "invalid bundleName: \"" + bn + "\"");
            TerminalErrorPrinter.printException(getClass().getName()
                    + " (constructor)", e);
            throw e;
        });
        String bpn = bundlePackageName(bundleNames.get(0));
        bundleNames.stream().filter(bn
                -> (!bpn.equals(bundlePackageName(bn)))).forEachOrdered(bn -> {
            IllegalArgumentException e = new IllegalArgumentException(
                    "invalid bundleName: \"" + bn + "\"");
            TerminalErrorPrinter.printException(getClass().getName()
                    + " (constructor)", e);
            throw e;
        });

        this.parent = parent;
        this.classLoader = classLoader;
        this.bundleNames = Collections.unmodifiableList(
                new ArrayList<>(bundleNames));
        this.resourcesDir = bpn.replace(".", "/") + "/";
        this.resourceBundles = new HashMap<>();
    }

    /* Internal use only: Used to determine if all bundles are in the same package */
    private String bundlePackageName(String bundleName) {
        int i = bundleName.lastIndexOf(".");
        return (i == -1) ? "" : bundleName.substring(0, i);
    }

}
