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
 *  Class      :   IconifyButton.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 2:52:54 PM
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class IconifyButton extends AbstractTitleButton {

    public IconifyButton(final JInternalFrame frame) {
        super("InternalFrame.iconifyIcon");
        addActionListener(new IconifyAction(frame));
        setVisible(frame.isIconifiable());
        frame.addPropertyChangeListener("iconable", (PropertyChangeEvent event) -> {
            setVisible(frame.isIconifiable());
        });
    }

    private class IconifyAction implements ActionListener {

        private final JInternalFrame frame;

        public IconifyAction(JInternalFrame internalFrame) {
            frame = internalFrame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                frame.setIcon(true);
            } catch (PropertyVetoException ignore) {
            }
        }
    }
}
