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
 *  Class      :   ButtonProvider.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 7:17:32 AM
 *  Modified   :   Oct 23, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Oct 23, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gs.platform.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The `ButtonProvider} interface is an annotation used to tell the system that
 * the method annotated provides a toolbar button for executing the annotated
 * method. This annotation is typically placed just before or just after the
 * `@Action` annotation to denote that the action provides a toolbar button.
 * This interface may be used in conjunction with the {@link
 * MenuItemProvider @MenuItemProvider} annotation when it is desired that an
 * `@Action` have both a toolbar button and a menu item associated with it.
 * <p>
 * The `&#064;ButtonProvider} annotation must have all of the parameters set.
 * These parameters are as follows:</p>
 * <ul>
 * <li>`name` &mdash; The name of the toolbar button for resource injection</li>
 * <li>`Action` &mdash; The `@Action` the button is to execute</li>
 * <li>`position} &mdash; The position on the toolbar the button should
 * take</li>
 * </ul>
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ButtonProvider {

    String name();

    Action action();

    int position();
}
