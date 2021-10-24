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
 *  Class      :   DockingTabbedPane.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 2:44:46 PM
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
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A tabbed pane that may be docked into a `JDesktopPane` with a
 * {@link net.sf.swingdocking.DockingDesktopManager} and handles
 * `JInternalFrame`s as pages.
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
class DockingTabbedPane extends JTabbedPane {

    private final List<JInternalFrame> frames = new ArrayList<>();
    private final SelectionListener selectionListener = new SelectionListener();
    private final MaximizationListener maximizationListener = new MaximizationListener();

    public DockingTabbedPane() {
        setOpaque(true);
        addChangeListener(selectionListener);
    }

    public DockingTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
        setOpaque(true);
        addChangeListener(selectionListener);
    }

    public DockingTabbedPane(int tabPlacement) {
        super(tabPlacement);
        setOpaque(true);
        addChangeListener(selectionListener);
    }

    public void addTab(JInternalFrame frame) {
        int index = frames.indexOf(frame);
        if (index == -1) {
            addTab(frame, getTabCount());
        } else {
            index = getInsertionIndex(index);
            insertTab(null, null, createTabPage(frame), null, index);
            setTabComponentAt(index, new TitlePane(frame));
        }
    }

    public void addTab(JInternalFrame frame, int index) {
        if (frames.contains(frame)) {
            frames.remove(frame);
        }
        frame.addPropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, selectionListener);
        frame.addPropertyChangeListener(JInternalFrame.IS_MAXIMUM_PROPERTY, maximizationListener);
        JInternalFrame previousFrame = getPreviousFrame(index);
        frames.add(frames.indexOf(previousFrame) + 1, frame);
        Component tabPage = createTabPage(frame);
        if (index > getTabCount()) {
            addTab(null, null, tabPage, null);
            index = getTabCount() - 1;
        } else {
            insertTab(null, null, tabPage, null, index);
        }
        setTabComponentAt(index, new TitlePane(frame));
        if (frame.isSelected()) {
            setSelectedIndex(index);
        }
    }

    public void removeTab(JInternalFrame frame) {
        frame.removePropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, selectionListener);
        frame.removePropertyChangeListener(JInternalFrame.IS_MAXIMUM_PROPERTY, maximizationListener);
        int index = getTabIndex(frame);
        frames.remove(frame);
        super.removeTabAt(index);
    }

    @Override
    public void removeTabAt(int index) {
        for (JInternalFrame frame : frames) {
            if (getTabIndex(frame) == index) {
                removeTab(frame);
                return;
            }
        }
        super.removeTabAt(index);
    }

    public int getTabIndex(JInternalFrame internalFrame) {
        for (int i = 0; i < getTabCount(); i++) {
            Component component = getComponentAt(i);
            if (component instanceof Container && ((Container) component).isAncestorOf(internalFrame)) {
                return i;
            }
        }
        return -1;
    }

    public JInternalFrame[] getFrames() {
        List<JInternalFrame> internalFrames = new ArrayList<>(frames.size());
        frames.stream().filter(frame -> (getTabIndex(frame) != -1)).forEachOrdered(frame -> {
            internalFrames.add(frame);
        });
        return internalFrames.toArray(new JInternalFrame[internalFrames.size()]);
    }

    public JInternalFrame getFrameAt(int index) {
        Component component = getComponentAt(index);
        return component instanceof TabPage ? ((TabPage) component).getInternalFrame() : null;
    }

    private JInternalFrame getPreviousFrame(int index) {
        if (index == 0) {
            return null;
        }
        Component component = getComponentAt(index - 1);
        if (component instanceof TabPage) {
            return ((TabPage) component).getInternalFrame();
        }
        return getPreviousFrame(index - 1);
    }

    private int getInsertionIndex(int index) {
        int insertionIndex = 0;
        for (int i = 0; i < index; i++) {
            if (isAncestorOf(frames.get(i))) {
                insertionIndex++;
            }
        }
        return insertionIndex;
    }

    private Component createTabPage(JInternalFrame frame) {
        TabPage tabPage = new TabPage(frame);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(frame, BorderLayout.CENTER);
        tabPage.add(wrapper, BorderLayout.CENTER);
        return tabPage;
    }

    private class SelectionListener implements ChangeListener, PropertyChangeListener {

        @Override
        public void stateChanged(ChangeEvent event) {
            int index = getSelectedIndex();
            if (index != -1) {
                JInternalFrame selectedFrame = getFrameAt(index);
                frames.forEach(frame -> {
                    try {
                        frame.setSelected(frame == selectedFrame);
                    } catch (PropertyVetoException e) {
                        //ignore
                    }
                });
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            JInternalFrame frame = (JInternalFrame) event.getSource();
            int index = getTabIndex(frame);
            if (frame.isSelected()) {
                setSelectedIndex(index);
            }
        }
    }

    private class MaximizationListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            frames.forEach(frame -> {
                try {
                    frame.setMaximum((Boolean) event.getNewValue());
                } catch (PropertyVetoException e) {
                    //ignore
                }
            });
        }
    }
}
