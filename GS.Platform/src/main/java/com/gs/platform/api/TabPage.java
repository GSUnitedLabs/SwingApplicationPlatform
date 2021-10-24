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
 *  Class      :   TabPage.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 2:42:53 PM
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
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class TabPage extends JPanel {

    public TabPage(JInternalFrame frame) {
        setLayout(new TabPageLayout(frame));
    }

    public JInternalFrame getInternalFrame() {
        return ((TabPageLayout) getLayout()).getInternalFrame();
    }

    /* 
     * Overridden to surround a bug in swing
     * that does not allow JInternalFrames in
     * non-selected tabs to be selected.
     */
    @Override
    public boolean isShowing() {
        return getParent() != null && getParent().isShowing();
    }

    private class TabPageLayout implements LayoutManager {

        private final JInternalFrame frame;
        private Component component;

        public TabPageLayout(JInternalFrame internalFrame) {
            frame = internalFrame;
        }

        public JInternalFrame getInternalFrame() {
            return frame;
        }

        @Override
        public void layoutContainer(Container parent) {
            if (component == null || !frame.isVisible()) {
                return;
            }
            Insets insets = parent.getInsets();
            int x = insets.left;
            int y = insets.top;
            int width = parent.getWidth() - insets.left - insets.right;
            int height = parent.getHeight() - insets.top - insets.bottom;
            Component contentPane = frame.getContentPane();
            if (frame.isShowing() && contentPane.isShowing()) {
                Point frameLocation = frame.getLocationOnScreen();
                Point contentPaneLocation = contentPane.getLocationOnScreen();
                x -= contentPaneLocation.x - frameLocation.x;
                y -= contentPaneLocation.y - frameLocation.y;
            }
            width += frame.getWidth() - contentPane.getWidth();
            height += frame.getHeight() - contentPane.getHeight();
            component.setBounds(x, y, width, height);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return frame.getContentPane().getMinimumSize();
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return frame.getContentPane().getPreferredSize();
        }

        @Override
        public void addLayoutComponent(String name, Component layoutComponent) {
            component = layoutComponent;
        }

        @Override
        public void removeLayoutComponent(Component layoutComponent) {
            if (component == layoutComponent) {
                component = null;
            }
        }
    }
}
