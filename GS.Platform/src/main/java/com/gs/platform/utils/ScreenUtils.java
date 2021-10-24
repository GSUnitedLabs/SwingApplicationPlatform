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
 *  Class      :   ScreenUtils.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 3:44:07 PM
 *  Modified   :   Oct 23, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Mar 08, 2020  Sean Carrick         Initial creation.
 *  Mar 21, 2020  Jiri Kovalsky        Added the getCenterPoint function.
 *  Feb 13, 2021  Sean Carrick         Modified getCenterPoint function to accept
 *                                     null container to center on the screen.
 * *****************************************************************************
 */
package com.gs.platform.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class ScreenUtils {

    /**
     * Calculates central position of the window within its container.
     * <p>
     * If the specified {@code window} is a top-level window or dialog,
     * {@code null} can be specified for the provided {@code container} to
     * center the window or dialog on the screen.</p>
     *
     * <dl>
     * <dt>Contributed By</dt>
     * <dd>Jiří Kovalský &lt;jiri dot kovalsky at centrum dot cz&gt;</dd>
     * <dt>Updated Feb 13, 2021</dt>
     * <dd>Update allows the specified {@code container} to be set to
     * {@code null} to allow centering a top-level window or dialog on the
     * screen.<br><br>
     * Sean Carrick &lt;sean at pekinsoft dot com&gt;</dd>
     * </dl>
     *
     * @param container Dimensions of parent container where window will be
     * located.
     * @param window Dimensions of child window which will be displayed within
     * its parent container.
     * @return Location of top left corner of window to be displayed in the
     * center of its parent container.
     */
    public static Point getCenterPoint(Dimension container, Dimension window) {
        if (container != null) {
            int x = container.width / 2;
            int y = container.height / 2;
            x = x - (window.width / 2);
            y = y - (window.height / 2);
            x = x < 0 ? 0 : x;
            y = y < 0 ? 0 : y;
            return new Point(x, y);
        } else {
            int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
            int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
            x = x - (window.width / 2);
            y = y - (window.width / 2);
            x = x < 0 ? 0 : x;
            y = y < 0 ? 0 : y;
            return new Point(x, y);
        }
    }

}
