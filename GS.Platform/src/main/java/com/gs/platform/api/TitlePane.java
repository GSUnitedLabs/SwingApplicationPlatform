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
 *  Class      :   TitlePane.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 2:55:31 PM
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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class TitlePane extends JPanel {

    public TitlePane(JInternalFrame frame) {
        super(new BorderLayout(1, 0));
        add(new TitleLabel(frame), BorderLayout.CENTER);
        add(createButtonBar(frame), BorderLayout.EAST);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 1));
    }

    private JComponent createButtonBar(JInternalFrame frame) {
        JPanel buttonBar = new JPanel(new GridLayout(1, 3, 1, 1));
        buttonBar.add(new IconifyButton(frame));
        buttonBar.add(new MaximizeButton(frame));
        buttonBar.add(new CloseButton(frame));
        return buttonBar;
    }
}
