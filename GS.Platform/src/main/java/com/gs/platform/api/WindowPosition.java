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
 *  Class      :   WindowPosition.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 5:39:44 PM
 *  Modified   :   Oct 22, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Oct 22, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gs.platform.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * `&#064;WindowPosition} is an `java.lang.annotation.Annotation} that tells the
 * platform where to dock the window. This value must be one of the constants
 * defined in the {@link DockingLocation} enumeration. To dock an editor window
 * into the editor docking location, one simply needs to do this: ```java
 * &#064;WindowPosition(DockingLocation.EDITOR) public class MyEditorWindow
 * extends Window { // The window class code here... } ```
 * <p>
 * By using the annotation `&#064;WindowPosition(DockingLocation.EDITOR}, the
 * platform will be notified that the window is to be docked into the editor
 * docking location, which is the largest docking area, in the center area of
 * the main window. The rest of the window subclass will be handled as in any
 * other Java/Swing desktop application.</p>
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface WindowPosition {

    DockingLocation value() default DockingLocation.EDITOR;
}
