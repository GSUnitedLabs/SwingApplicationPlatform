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
 *  Class      :   Dialog.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 3:21:48 PM
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

import com.gs.platform.utils.ScreenUtils;

/**
 * The `Dialog` class is for showing modal dialogs in a Swing Application
 * Platform `PlatformApplication`. 
 *
 * @author Sean Carrick
 */
public class Dialog extends javax.swing.JDialog {

    protected final Application application;

    /**
     * Creates new `Dialog` for displaying an application modal dialog box to
     * the user. The `Dialog` class should be used for custom dialogs only. To
     * display errors, warnings, informational messages, or to ask questions,
     * the `MessageBox` class should be used. `Dialog` takes care of setting the
     * title of the dialog box and the initial position on the screen.
     *
     * @param application the `Application` in which this `Dialog` is being
     * displayed
     * @param parent the `Application`'s main window from `getMainFrame()`. A
     * value of `null` may be provided for this parameter
     */
    public Dialog(Application application, java.awt.Frame parent) {
        super((parent == null) ? application.getMainFrame() : parent, true);

        this.application = application;

        initComponents();

        setTitle(application.getContext().getResourceMap(application.getClass(),
                getClass()).getString("AboutBox.title"));
        setLocation(ScreenUtils.getCenterPoint((parent == null) ? null
                : parent.getSize(), getSize()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
