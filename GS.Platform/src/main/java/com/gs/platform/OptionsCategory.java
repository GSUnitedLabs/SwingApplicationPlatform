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
 *  Class      :   OptionsCategory.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 9:46:59 PM
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
package com.gs.platform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The `@OptionsCategory` annotation is used on `OptionsPanel` classes to mark
 * in which options category the `OptionsPanel` is to be displayed. The constant
 * values are located in the `OptionsCategories` enum and are as follows:
 * <ul>
 * <li>`OptionsCategories.GENERAL` &mdash; The General tab of the Options 
 *      dialog</li>
 * <li>`OptionsCategories.ACCOUNTING` &mdash; The Accounting tab of the Options
 *      dialog</li>
 * <li>`OptionsCategories.INTERFACE` &mdash; The Look &amp; Feel tab of the 
 *      Options dialog</li>
 * <li>`OptionsCategories.MISCELLANEOUS` &mdash; The Miscellaneous tab of the
 *      Options dialog</li>
 * </ul>
 * <p>
 * When the `OptionsDialog` is loaded, the marked `OptionsPanel` will be given 
 * its own tab under that category.</p>
 * 
 * @see OptionsCategories
 * @see OptionsPanel
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface OptionsCategory {
    OptionsCategories name() default OptionsCategories.MISCELLANEOUS;
}
