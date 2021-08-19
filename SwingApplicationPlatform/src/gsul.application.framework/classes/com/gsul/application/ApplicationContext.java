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
 *  Class      :   ApplicationContext.java
 *  Author     :   Sean Carrick
 *  Created    :   Aug 18, 2021 @ 8:56:36 PM
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

/**
 * A singleton that manages shared objects, like actions, resources, and tasks,
 * for {@code Applications}.
 * <p>
 * {@link Application Applications} use the {@code ApplicationContext} singleton
 * to find global values and services. The majority of the Swing Application
 * Framework API can be accessed through {@code ApplicationContext}. The static
 * {@code getInstance} method returns the singleton. Typically it is only called
 * after the application has been {@link Application#launch launched}, however
 * it is always safe to call {@code getInstance}.
 * <dl>
 * <dt><strong><em>Copyright Notice</em></strong></dt>
 * <dd>This class has been modified with the permission of the original author, 
 * Hans Muller. This class was originally written for the JSR-296 Swing 
 * Application Framework, using JDK6 in 2006, at Sun Microsystems.<br><br>This
 * work is an adaptation and update of that original work.</dd></dl>
 * 
 * @see Application
 *
 * @author Hans Muller &ndash; Original JSR-296 Framework author
 * @author Sean Carrick  &ndash; Modular Java Swing Application Platform author &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class ApplicationContext {

    /**
     * Constructs the {@code ApplicationContext} singleton.
     */
    protected ApplicationContext () {

    }

}
