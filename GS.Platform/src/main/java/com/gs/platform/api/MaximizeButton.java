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
 *  Class      :   MaximizeButton.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 2:53:35 PM
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class MaximizeButton extends AbstractTitleButton {

    private JInternalFrame frame;
    private final Icon maximizeIcon;
    private final Icon minimizeIcon;

    public MaximizeButton(JInternalFrame internalFrame) {
        super("InternalFrame.maximizeIcon");
        frame = internalFrame;
        maximizeIcon = getIcon();
        minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
        addActionListener(new MaximizeAction());
        frame.addPropertyChangeListener(JInternalFrame.IS_MAXIMUM_PROPERTY, new MaximizeListener());
        setVisible(frame.isMaximizable());
        frame.addPropertyChangeListener("maximizable", (PropertyChangeEvent event) -> {
            setVisible(frame.isMaximizable());
        });
    }

    private class MaximizeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                frame.setMaximum(!frame.isMaximum());
            } catch (PropertyVetoException ignore) {
            }
        }
    }

    private class MaximizeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            setIcon(frame.isMaximum() ? minimizeIcon : maximizeIcon);
        }
    }
}
