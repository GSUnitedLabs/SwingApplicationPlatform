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
 *  Class      :   MenuItemProvider.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 7:12:40 AM
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
 * The `MenuItemProvider} interface allows An@Action` to be marked as providing
 * a menu item within the `Application`'s main menu bar. The `MenuItemProvider}
 * requires that the `name`, `owner}, `Action`, and `position} parameters be
 * provided. These parameters tell the system the following information:
 * <ul>
 * <li>`name` &mdash; The name for the menu item for resource injection</li>
 * <li>`ownerName} &mdash; In which menu the menu item is to be placed. This
 * value will need to be the name of the menu into which the menu item is to be
 * placed. Typically, menus are named with the word "Menu" appended to the text
 * of the menu in all lower-case, such as "fileMenu", "editMenu", "helpMenu",
 * etc. Alternatively, menus could possibly be named in Hungarian notation, such
 * as "mnuFile", "mnuEdit", "mnuHelp", etc.</li>
 * <li>`altOwnerName} &mdash; The alternative name for the menu into which the
 * menu item should be placed. Typically, menus are named with the word "Menu"
 * appended to the text of the menu in all lower-case, such as "fileMenu",
 * "editMenu", "helpMenu", etc. Alternatively, menus could possibly be named in
 * Hungarian notation, such as "mnuFile", "mnuEdit", "mnuHelp", etc.<br><br>The
 * `altOwnerName} field is an optional field (<em>defaults to an empty
 * String</em>) where an alternative owner menu name can be set. For example, if
 * the `ownerName} field is set to "fileMenu", the `altOwnerName} field could be
 * set to "mnuFile".
 * <br><br>The system will always attempt to place the menu item into the menu
 * named by the `ownerName} field. However, if no menu is found with that name,
 * the system will automatically attempt to place the menu item into a menu that
 * has the same name value as the `altOwnerName} field. If neither of these
 * methods work for adding the menu item to the menu, a
 * {@link NoValidMenuFoundException} will be thrown.
 * <li>`Action` &mdash; The `@Action` that the menu item executes</li>
 * <li>`position} &mdash; The position in the menu the menu item should
 * take</li>
 * </ul>
 * <dl><dt><strong><em>Note Regarding Menu Naming Conventions</em></strong></dt>
 * <dd>GS United Labs <em>always</em> uses the standard Java method of naming
 * objects. In Java, object instance variables, especially for GUI components,
 * are typically named as `${variableName}${componentType}}, such as `fileMenu},
 * `firstNameTextField}, `namePromptLabel}, etc. GS United Labs also follows
 * this construct when setting object name properties. For example, the File
 * menu on the Platform's main window has a variable name of `fileMenu}.
 * Therefore, we give the object's name property the same exact name as the
 * variable `fileMenu.setName("fileMenu")}. By following this rule, it
 * alleviates confusion that could arise between variable names and the returned
 * value of `${variableName}.getName()}, as the name property will always be the
 * exact same as the object variable name. Another advantage of following this
 * rule is that it makes understanding `@Action`'s ResourceBundle} file keys
 * easier. When resources are injected by the system, the value of
 * `Component.getName()} is used to determine the key to the property, as the
 * resource files are set up like this: `fileMenu.text = &File}. Notice that
 * before the dot is the value of `fileMenu.getName()}, and after the dot is the
 * name of the property, `text}. This is the setup that allows resource
 * injection to work properly.</dd></dl>
 * <p>
 * The `&#064;MenuItemProvider} annotation is used to annotate a method, and
 * will typically be placed just before or just after the `&#064Action}
 * annotation on a method.</p>
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
public @interface MenuItemProvider {

    String name();

    String ownerName();

    String altOwnerName() default "";

    Action action();

    int position();
}
