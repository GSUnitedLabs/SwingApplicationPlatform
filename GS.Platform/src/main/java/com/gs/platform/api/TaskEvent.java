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
 *  Class      :   TaskEvent.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 8:31:37 PM
 *  Modified   :   Oct 22, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  ??? ??, 2006  Hans Muller          Initial creation.
 *  Oct 22, 2021  Sean Carrick         Updated to JDK11.
 * *****************************************************************************
 */
package com.gs.platform.api;

import java.util.EventObject;

/**
 * An encapsulation of the value produced one of the `Task} execution methods:
 * `doInBackground()}, `process}, `done}. The source of a `TaskEvent} is the
 * `Task} that produced the value.
 *
 * @param <T> the return object type for this `TaskEvent}'s `getValue} method
 *
 * @see TaskListener
 * @see Task
 */
public class TaskEvent<T> extends EventObject {

    private final T value;

    /**
     * Returns the value this event represents.
     *
     * @return the `value} constructor argument.
     */
    public final T getValue() {
        return value;
    }

    /**
     * Construct a `TaskEvent}.
     *
     * @param source the `Task} that produced the value.
     * @param value the value, null if type `T} is `Void}.
     */
    public TaskEvent(Task source, T value) {
        super(source);
        this.value = value;
    }
}
