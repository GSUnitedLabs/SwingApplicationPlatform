/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gsul.utils;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 */
public class ScreenUtils {
    

    /**
     * Calculates central position of the window within its container.
     * <p>If the specified {@code window} is a top-level window or dialog,
     * {@code null} can be specified for the provided {@code container} to
     * center the window or dialog on the screen.</p>
     *
     * <dl>
     * <dt>Contributed By</dt>
     * <dd>Jiří Kovalský &lt;jiri dot kovalsky at centrum dot cz&gt;</dd>
     * <dt>Updated Feb 13, 2021</dt>
     * <dd>Update allows the specified {@code container} to be set to
     *      {@code null} to allow centering a top-level window or dialog on the
     *      screen.<br><br>
     *      Sean Carrick &lt;sean at pekinsoft dot com&gt;</dd>
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
