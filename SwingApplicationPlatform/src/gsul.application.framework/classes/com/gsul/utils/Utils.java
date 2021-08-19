/*
 * Copyright (C) 2021 PekinSOFT Systems
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
 *  Project    :   PSAppFramework
 *  Class      :   Utils.java
 *  Author     :   Sean Carrick
 *  Created    :   Mar 8, 2020 @ 12:32:47 PM
 *  Modified   :   Mar 8, 2020
 *
 *  Purpose:
 *      Provides useful string-related methods.
 *
 *  Revision History:
 *
 *  WHEN          BY                  REASON
 *  ------------  ------------------- ------------------------------------------
 *  Mar 08, 2020  Sean Carrick        Initial creation.
 *  Mar 21, 2020  Jiri Kovalsky       Added the getCenterPoint function.
 *  Mar 21, 2020  Sean Carrick        Moved getCenterPoint function into code
 *                                    fold for `public static methods and
 *                                    functions`.
 *  Nov 24, 2020  Sean Carrick        Added the following methods:
 *                                      - getApplicationFolderByOS
 *                                      - getApplicationDataFolderByOS
 *                                      - getApplicationSettingsLocationByOS
 *                                      - getSystemLogLocationByOS
 *  Feb 13, 2021  Sean Carrick        Modified getCenterPoint function to accept
 *                                    null container to center on the screen.
 * *****************************************************************************
 */
package com.gsul.utils;

import java.io.File;
import java.net.URISyntaxException;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 *
 * @version 0.2.15
 * @since 0.1.0
 */
public class Utils {

    //<editor-fold defaultstate="collapsed" desc="Constructor(s)">
    private Utils() {
        // Privatized in order to prohibit this class from being instantiated.
    }
    //</editor-fold>

    /**
     * Gets the name of the program's JAR file.
     *
     * @return java.lang.String program's JAR file name
     */
    private static String getJarName() {
        return new File(Utils.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }

    /**
     * Tests to see if the project is currently executing from within a JAR file
     * or from a folder structure. This is useful for testing if the program is
     * running inside or outside the IDE.
     *
     * @return boolean true if running inside a JAR file; false if running in
     * the IDE
     */
    private static boolean runningFromJAR() {
        String jarName = getJarName();
        return jarName.contains(".jar");
    }

    /**
     * Retrieves the currently executing program's program directory. This
     * should be the directory in which the program was executed, which could
     * also be considered the program's installation path.
     *
     * @return java.lang.String the directory from which the program is running
     */
    public static String getProgramDirectory() {
        if (runningFromJAR()) {
            return getCurrentJARDirectory();
        } else {
            return getCurrentProjectDirectory();
        }
    }

    /**
     * Retrieves the current project's project directory.
     *
     * @return java.lang.String the project's directory
     */
    private static String getCurrentProjectDirectory() {
        return new File("").getAbsolutePath();
    }

    /**
     * Retrieves the JAR file's current directory location.
     *
     * @return java.lang.String the directory in which the JAR file is located
     */
    private static String getCurrentJARDirectory() {
        try {
            return new File(Utils.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath())
                    .getParent();
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }

        return "";
    }

}
