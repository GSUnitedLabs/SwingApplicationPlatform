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
 *  Class      :   IconDockLayout.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 2:37:44 PM
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame.JDesktopIcon;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class IconDockLayout extends DockLayout {

    private final List<JDesktopIcon> icons = new ArrayList<>();

    @Override
    public void addLayoutComponent(Component component, Object constraints) {
        if (component instanceof JDesktopIcon) {
            icons.add((JDesktopIcon) component);
        } else {
            super.addLayoutComponent(component, constraints);
        }
    }

    @Override
    public void removeLayoutComponent(Component component) {
        if (component instanceof JDesktopIcon) {
            icons.remove(component);
        } else {
            super.removeLayoutComponent(component);
        }
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int iconWidth = getPreferredIconWidth();
        int iconHeight = getPreferredIconHeight();
        int iconX = 0;
        int iconY = parent.getHeight() - iconHeight;
        layout(insets.left, insets.top, parent.getWidth() - insets.left - insets.right, parent.getHeight() - insets.top - insets.bottom - iconHeight);
        for (JDesktopIcon icon : icons) {
            icon.setVisible(true);
            int width = icon.getPreferredSize().width;
            if (iconWidth > parent.getWidth()) {
                width = parent.getWidth() / icons.size();
            }
            icon.setBounds(iconX, iconY, width, iconHeight);
            iconX += width;
        }
        for (Component component : parent.getComponents()) {
            if (!(component instanceof JDesktopIcon)
                    && (component.getY() + component.getHeight() > parent.getHeight() - insets.bottom - iconHeight)) {
                component.setBounds(component.getX(), component.getY(), component.getWidth(),
                        parent.getHeight() - insets.bottom - iconHeight - component.getY());
            }
        }
    }

    private int getPreferredIconWidth() {
        int width = 0;
        for (JDesktopIcon icon : icons) {
            width = Math.max(width, icon.getPreferredSize().width);
        }
        return width;
    }

    private int getPreferredIconHeight() {
        int height = 0;
        for (JDesktopIcon icon : icons) {
            height = Math.max(height, icon.getPreferredSize().height);
        }
        return height;
    }
}
