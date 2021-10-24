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
 *  Class      :   Action.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 11:46:01 AM
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that will be used to define a Swing `Action` object's
 * `actionPerformed` method. It also identifies the resources that will be used
 * to initialize the Action's properties. Additional `@Action` parameters can be
 * used to specify the name of the bound properties (from the same class) that
 * indicate if the Action is to be enabled/selected, and if the GUI should be
 * blocked while the Action's background {@link Task} is running.
 * <p>
 * The {@link ApplicationActionMap} class creates an `ActionMap` that contains
 * one {@link ApplicationAction} for each `&@064;Action} found in a target or
 * "actions" class. Typically applications will use {@link
 * ApplicationContext#getActionMap(Class, Object) getActionMap} to lazily
 * construct and cache ApplicationActionMaps, rather than constructing them
 * directly. By default the ApplicationActionMap's {@link ApplicationActionMap#get
 * `key` for  An@Action` is the name of the method. The `name`
 * parameter can be used to specify a different key.</p>
 * <p>
 * The `ApplicationAction`'s properties are initialized with resources loaded
 * from a `ResourceBundle` with the same name as the actions class. The list of
 * properties initialized this way is documented by the {@link
 * ApplicationAction ApplicationAction's} constructor.</p>
 * <p>
 * The method marked with `@Action` can have no parameters, or a single
 * `ActionEvent` parameter. The method's return type can be ` void} or
 * {@link Task}. If the return type is Task, the Task will be executed by the
 * ApplicationAction's `actionPerformed` method.</p>
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 *
 * @see ApplicationAction
 * @see ApplicationActionMap
 * @see ApplicationContext
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {

    String name() default "";

    String enabledProperty() default "";

    String selectedProperty() default "";

    Task.BlockingScope block() default Task.BlockingScope.NONE;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Parameter {

        String value() default "";
    }
}
