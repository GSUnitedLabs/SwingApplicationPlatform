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
 *  Project    :   NorthwindTraders
 *  Class      :   NetUtils.java
 *  Author     :   Sean Carrick
 *  Created    :   Feb 13, 2021 @ 11:29:50 PM
 *  Modified   :   Feb 13, 2021
 *
 *  Purpose:     See class JavaDoc comment.
 *
 *  Revision History:
 *
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Feb 13, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gsul.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Sean Carrick &lt;sean at pekinsoft dot com&gt;
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public class NetUtils {

    private NetUtils() {
        // Preventing initialization
    }

    /**
     * An improved version of determining if the local computer is connected to
     * the internet. This version simply attempts to connect to a website. If
     * the internet is reachable, it returns {@code true}, otherwise {@code false}
     * is returned.
     * <p>
     * For best results, use this method in conjunction with the {@code canPing}
     * method to be absolutely positive the internet is reachable.</p>
     *
     * @return {@code true} if the internet is available and reachable;
     *          {@code false} otherwise.
     *
     * @see #canPing
     */
    public static boolean isNetworkConnectionAvailable() {
        try {
            URL url = new URL("https://duckduckgo.com");
            URLConnection connection = url.openConnection();
            connection.connect();

            MessageBox.showInfo("Internet connection successful!", "Internet Available");
            return true;
        } catch (IOException ex) {
            MessageBox.showWarning("Internet connection failed!", "Internet Unavailable");
            return false;
        }
    }

    public static boolean canPing(String siteToPing, String app) {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 5 " + siteToPing);
            int x = p.waitFor();
            if (x == 0) {
                MessageBox.showInfo("Ping successful!", "Site Reachable");
                return true;
            } else {
                MessageBox.showWarning("Ping failed!", "Site Unreachable");
                return false;
            }
        } catch (Exception ex) {
            MessageBox.showError(ex, "Error: " + ex.getClass().getSimpleName(),
                    app);
            return false;
        }
    }
}
