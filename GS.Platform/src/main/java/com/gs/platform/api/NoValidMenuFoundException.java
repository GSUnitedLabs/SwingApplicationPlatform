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
 *  Class      :   NoValidMenuFoundException.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 11:34:42 AM
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

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class NoValidMenuFoundException extends Exception {

    /**
     * Creates a new instance of <code>NoValidMenuFoundException</code> without
     * detail message.
     */
    public NoValidMenuFoundException() {
    }

    /**
     * Constructs an instance of <code>NoValidMenuFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoValidMenuFoundException(String msg) {
        super(msg);
    }
}
