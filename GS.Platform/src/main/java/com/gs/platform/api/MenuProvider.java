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
 *  Class      :   MenuProvider.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 7:09:32 AM
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
 * The `MenuProvider} interface can be used on a class to allow that class to
 * provide a top-level menu for the `Application`'s main menu bar. All of the
 * parameters for the `&#064;MenuProvider} must be supplied to tell the system
 * how to create and display the menu in the menu bar. These parameters are:
 * <ul>
 * <li>`name` &mdash; The name of the menu for resource injection</li>
 * <li>`text} &mdash; The marked text to be displayed on the menu. The text may
 * be marked with an ampersand (&amp;) or an underscore (_) placed immediately
 * before the letter in the text to be used as the mnemonic character. The
 * resource injection methods will then remove the ampersand and set the next
 * character as the mnemonic and the position of that character as the mnemonic
 * index.</li>
 * <li>`position} &mdash; The position on the menu bar that the menu should
 * take</li>
 * </ul>
 * <p>
 * More than one `&#064;MenuProvider} annotation may be used on a single class,
 * if that class will be providing multiple menus to the application. The system
 * will determine if a menu has been added prior and not allow for adding
 * multiple menus of the same name or text. In other words, if multiple classes
 * have the following annotation associated with them, then only one of the
 * annotations will be acted upon to add the menu.</p>
 * ```java &#064;MenuProvider( name = "toolsMenu", text = "&amp;Tools", position
 * = 500) public class MyTools { // Class functionality and fields here... }
 *
 * &#064;MenuProvider( name = "mnuTools", text = "&amp;Tools", position = 600)
 * public class MyOtherTools { // Class functionality and fields here... } ```
 * <p>
 * Even if multiple classes define the `&#064MenuProvider} annotation with
 * different `name` and `position} values, but each of them provide the same
 * `text} value, only the first one located will be added to the `Application`'s
 * menu bar. All successive `&#064;MenuProvider}s will simply be ignored.</p>
 *
 * @see MenuItemProvider
 * @see ButtonProvider
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MenuProvider {

    String name();

    String text();

    int position();
}
