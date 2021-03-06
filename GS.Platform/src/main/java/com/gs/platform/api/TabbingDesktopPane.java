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
 *  Class      :   TabbingDesktopPane.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 2:44:11 PM
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

import com.gs.platform.api.DockConstraints.DockLocation;
import com.gs.platform.api.LayoutConstraints.TabConstraints;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JInternalFrame;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class TabbingDesktopPane extends DockingDesktopPane {

    public TabbingDesktopPane() {
        setDesktopManager(new TabbingDesktopManager());
    }

    @Override
    public void add(Component component, Object constraints) {
        if (component instanceof JInternalFrame && constraints instanceof TabConstraints) {
            add((JInternalFrame) component, (TabConstraints) constraints);
        } else {
            super.add(component, constraints);
        }
    }

    public void add(JInternalFrame frame, TabConstraints constraints) {
        TabbingDesktopManager desktopManager = (TabbingDesktopManager) getDesktopManager();
        Component tabbedPane = desktopManager.getChild(constraints.getComponentToDockAt());
        int index = 0;
        if (tabbedPane instanceof DockingTabbedPane) {
            DockingTabbedPane dockingTabbedPane = (DockingTabbedPane) tabbedPane;
            index = dockingTabbedPane.getTabIndex(constraints.getComponentToDockAt());
        }
        if (constraints.getDockLocation() == DockLocation.EAST) {
            index++;
        }
        desktopManager.addTab(tabbedPane, index, frame);
    }

    @Override
    public void remove(Component component) {
        if (component instanceof JInternalFrame) {
            remove((JInternalFrame) component);
        } else {
            super.remove(component);
        }
    }

    public void remove(JInternalFrame frame) {
        TabbingDesktopManager desktopManager = (TabbingDesktopManager) getDesktopManager();
        Component child = desktopManager.getChild(frame);
        if (child instanceof DockingTabbedPane) {
            DockingTabbedPane tabbedPane = (DockingTabbedPane) child;
            tabbedPane.removeTab(frame);
            desktopManager.handleRemoveLastTab(tabbedPane);
        } else {
            super.remove(frame);
        }
    }

    @Override
    public JInternalFrame[] getAllFrames() {
        ArrayList<JInternalFrame> allFrames = new ArrayList<>();
        for (int i = 0; i < getComponentCount(); i++) {
            Component component = getComponent(i);
            if (component instanceof JInternalFrame) {
                allFrames.add((JInternalFrame) component);
            } else if (component instanceof DockingTabbedPane) {
                allFrames.addAll(Arrays.asList(((DockingTabbedPane) component).getFrames()));
            }
        }
        return allFrames.toArray(new JInternalFrame[allFrames.size()]);
    }
}
