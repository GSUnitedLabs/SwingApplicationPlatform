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
 *  Class      :   TerminalErrorPrinter.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 4:47:29 PM
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
package com.gs.platform.utils;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class TerminalErrorPrinter {

    private TerminalErrorPrinter() {
        /* For internal use only */ }

    public static void print(Exception ex, String msg) {
        System.err.println("The following Exception was encountered in the "
                + "Desktop API:");
        System.out.println("-".repeat(80));
        System.out.println();
        System.out.println(msg);
        System.out.println("-".repeat(80));
        System.err.println(ex.getMessage());
        System.err.println(ex.getCause());
        ex.printStackTrace(System.err);
        System.out.println("-".repeat(80));
    }

}
