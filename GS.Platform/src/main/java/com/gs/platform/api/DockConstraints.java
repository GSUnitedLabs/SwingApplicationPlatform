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
 *  Class      :   DockConstraints.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 2:30:10 PM
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

/**
 * Provides docking constraints for dockable windows.
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class DockConstraints {

    public static DockConstraints leftOf(Component component) {
        return new DockConstraints(component, DockLocation.WEST);
    }

    public static DockConstraints leftOf(Component component, float alignment) {
        return new DockConstraints(component, DockLocation.WEST, alignment);
    }

    public static DockConstraints rightOf(Component component) {
        return new DockConstraints(component, DockLocation.EAST);
    }

    public static DockConstraints rightOf(Component component, float alignment) {
        return new DockConstraints(component, DockLocation.EAST, alignment);
    }

    public static DockConstraints topOf(Component component) {
        return new DockConstraints(component, DockLocation.NORTH);
    }

    public static DockConstraints topOf(Component component, float alignment) {
        return new DockConstraints(component, DockLocation.NORTH, alignment);
    }

    public static DockConstraints bottomOf(Component component) {
        return new DockConstraints(component, DockLocation.SOUTH);
    }

    public static DockConstraints bottomOf(Component component, float alignment) {
        return new DockConstraints(component, DockLocation.SOUTH, alignment);
    }

    private Component componentToDockAt;
    private DockLocation dockLocation;
    private float alignment;

    public DockConstraints(Component componentToDockAt) {
        this(componentToDockAt, DockLocation.EAST);
    }

    public DockConstraints(Component componentToDockAt, DockLocation dockLocation) {
        this(componentToDockAt, dockLocation, 0.5f);
    }

    public DockConstraints(Component componentToDockAt, DockLocation dockLocation, float alignment) {
        if (componentToDockAt == null) {
            throw new IllegalArgumentException(
                    "componentToDockAt must not be null");
        }
        if (dockLocation == null) {
            throw new IllegalArgumentException(
                    "dockLocation must not be null");
        }
        if (alignment < 0 || alignment > 1) {
            throw new IllegalArgumentException(
                    "alignment must be between 0 (inclusive) and 1 (inclusive)");
        }
        this.componentToDockAt = componentToDockAt;
        this.dockLocation = dockLocation;
        this.alignment = alignment;
    }

    public Component getComponentToDockAt() {
        return componentToDockAt;
    }

    public DockLocation getDockLocation() {
        return dockLocation;
    }

    public float getAlignment() {
        return alignment;
    }

    public enum DockLocation {
        NORTH, SOUTH, EAST, WEST;
    }
}
