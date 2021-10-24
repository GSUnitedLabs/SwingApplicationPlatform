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
 *  Class      :   MessageBox.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 3:52:57 PM
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

import com.gs.platform.api.Application;
import javax.swing.JOptionPane;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageBox {

    public static final int YES_OPTION = JOptionPane.YES_OPTION;
    public static final int NO_OPTION = JOptionPane.NO_OPTION;
    public static final int CANCEL_OPTION = JOptionPane.CANCEL_OPTION;
    public static final int OK_OPTION = JOptionPane.OK_OPTION;
    public static final int CLOSED_OPTION = JOptionPane.CLOSED_OPTION;

    private MessageBox() {
        /* Internal use only. */ }

    /**
     * Standardized method of displaying informational messages to the user.
     * <p>
     * Messages displayed in this manner should not be for errors in the program
     * or warnings of improper usage. Furthermore, questions should not be asked
     * via this method, nor should input be sought. Again, only informational
     * messages should be displayed using this method.
     * </p>
     *
     * @param message The message to display to the user.
     * @param title The title of the message box.
     *
     */
    public static void showInfo(String message, String title) {
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Standardized method of displaying warning messages to the user.
     * <p>
     * Messages shown in this manner should be used to warn users of a potential
     * problem, such as not providing required data, etc. Simple informational
     * messages should be shown using the showInfo method and errors should be
     * shown using the showError method. Questions and input should be sought
     * using the appropriate methods.
     * </p>
     *
     * @param message The message to display to the user.
     * @param title The title of the message box.
     *
     */
    public static void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Standardized method of displaying errors to the user.
     * <p>
     * Error messages should be displayed from any `catch()` block where
     * the `try` block has failed. Some critical errors should be
     * followed by a call to the `exit()` method and the program should
     * be terminated. However, that functionality should only be reserved for
     * critical errors, from which the user cannot recover.
     *
     * @param app The `Application` in which the error occurred.
     * @param ex The exception that was thrown.
     * @param title The title of the message box
     */
    public static void showError(Application app, Exception ex, String title) {
        ErrorDialog dialog = new ErrorDialog(app, app.getMainFrame(), ex);
        app.getContext().getResourceMap(ErrorDialog.class).injectComponents(dialog);
    }

    /**
     * Standardized method of displaying a question to the user.
     * <p>
     * This method should be used to request confirmation from the user for any
     * particular action they may have taken. Mostly, this method should be used
     * to confirm closing a window that has unsaved changes.
     * </p>
     *
     * @param question The question that the user needs to answer.
     * @param title The title of the message box.
     * @param cancel Whether to include a cancel button as an option.
     * @return an integer indicating the option selected by the user. This will
     * be one of:
     * <ul>
     * <li>MessageBox.YES_OPTION</li>
     * <li>MessagBox.NO_OPTION</li>
     * <li>MessageBox.CANCEL_OPTION, if cancel button is included</li>
     * </ul>
     */
    public static int askQuestion(String question, String title, boolean cancel) {
        int choice = 0;

        if (cancel) {
            choice = JOptionPane.showConfirmDialog(null,
                    question,
                    title,
                    JOptionPane.YES_NO_CANCEL_OPTION);
        } else {
            choice = JOptionPane.showConfirmDialog(null,
                    question,
                    title,
                    JOptionPane.YES_NO_OPTION);
        }

        return choice;
    }

    /**
     * Standardized method of seeking additional input from the user.
     * <p>
     * This method should be used to prompt the user for a single piece of input
     * that they may have forgotten to provide in another manner. A good use of
     * this method would be to get the value of a required field that the user
     * did not enter on a form.
     * </p>
     *
     * @param prompt A description of the information the user needs to provide.
     * @param title The title of the message box.
     * @return The input requested of the user.
     */
    public static String getInput(String prompt, String title) {
        return JOptionPane.showInputDialog(null, prompt, title,
                JOptionPane.PLAIN_MESSAGE);
    }

}
