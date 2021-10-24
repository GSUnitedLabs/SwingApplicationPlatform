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
 *  Class      :   ErrorDialog.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 3:54:25 PM
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

import com.gs.platform.api.Action;
import com.gs.platform.api.Application;
import com.gs.platform.api.ApplicationContext;
import com.gs.platform.api.Dialog;
import com.gs.platform.api.ResourceMap;
import javax.swing.ActionMap;

/**
 *
 * @author Sean Carrick
 */
public class ErrorDialog extends Dialog {

    private final Exception ex;
    private final ApplicationContext ctx;
    private final ActionMap actionMap;
    private final ResourceMap resourceMap;

    /**
     * Creates new form ErrorDialog
     * 
     * @param application the `Application` from which this error is being shown
     * @param parent the parent for this `ErrorDialog`; may be `null`
     * @param ex the `Exception` that caused this `ErrorDialog` to be shown
     */
    public ErrorDialog(Application application, java.awt.Frame parent,
            Exception ex) {
        super(application, parent);

        this.ex = ex;
        ctx = application.getContext();
        actionMap = ctx.getActionMap();
        resourceMap = ctx.getResourceMap();

        initComponents();

        String msg = "The following exception was thrown by the program:\n\n";
        msg += ex.getMessage() + "\n\nException: ";
        msg += ex.getClass().getSimpleName();
        msg += "\n\nSee also the application log: "
                + Application.getInstance().getContext().getResourceMap().getString(
                        "Application.app.log.folder");

        errorTypeField.setText(ex.getClass().getSimpleName());
        messageField.setText(msg);
        for (StackTraceElement element : ex.getStackTrace()) {
            detailsField.append(element.toString());
            detailsField.append("\n");
        }
    }

    @Action
    public void close() {
        dispose();
    }

    @Action
    public void help() {

    }

    @Action
    public void submitReport() {

    }

    @Action
    public void saveReport() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        errorTypeLabel = new javax.swing.JLabel();
        messageLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        messageField = new javax.swing.JTextArea();
        errorTypeField = new javax.swing.JTextField();
        commandPanel = new javax.swing.JPanel();
        helpButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();
        detailsPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detailsField = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        errorTypeLabel.setText(resourceMap.getString("errorTypeLabel.text"));
        errorTypeLabel.setName("errorTypeLabel"); // NOI18N

        messageLabel.setText(resourceMap.getString("messageLabel.text"));
        messageLabel.setName("messageLabel"); // NOI18N

        messageField.setEditable(false);
        messageField.setColumns(20);
        messageField.setRows(5);
        messageField.setName("messageField"); // NOI18N
        jScrollPane1.setViewportView(messageField);

        errorTypeField.setEditable(false);
        errorTypeField.setName("errorTypeField"); // NOI18N

        helpButton.setText(resourceMap.getString("helpButton.text"));
        helpButton.setName("helpButton"); // NOI18N
        helpButton.setAction(actionMap.get("help"));

        closeButton.setText(resourceMap.getString("closeButton.text"));
        closeButton.setName("closeButton"); // NOI18N
        closeButton.setAction(actionMap.get("close"));

        saveButton.setText(resourceMap.getString("saveButton.text"));
        saveButton.setName("saveButton"); // NOI18N
        saveButton.setAction(actionMap.get("saveReport"));

        reportButton.setText(resourceMap.getString("reportButton.text"));
        reportButton.setName("reportButton"); // NOI18N
        reportButton.setAction(actionMap.get("submitReport"));

        javax.swing.GroupLayout commandPanelLayout = new javax.swing.GroupLayout(commandPanel);
        commandPanel.setLayout(commandPanelLayout);
        commandPanelLayout.setHorizontalGroup(
            commandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(commandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 273, Short.MAX_VALUE)
                .addComponent(reportButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );
        commandPanelLayout.setVerticalGroup(
            commandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, commandPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(commandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(helpButton)
                    .addComponent(closeButton)
                    .addComponent(saveButton)
                    .addComponent(reportButton))
                .addContainerGap())
        );

        detailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("detailsPanel.title")));
        detailsPanel.setName("detailsPanel"); // NOI18N

        detailsField.setEditable(false);
        detailsField.setColumns(20);
        detailsField.setRows(5);
        detailsField.setName("detailsField"); // NOI18N
        jScrollPane2.setViewportView(detailsField);

        javax.swing.GroupLayout detailsPanelLayout = new javax.swing.GroupLayout(detailsPanel);
        detailsPanel.setLayout(detailsPanelLayout);
        detailsPanelLayout.setHorizontalGroup(
            detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        detailsPanelLayout.setVerticalGroup(
            detailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(commandPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(messageLabel)
                    .addComponent(errorTypeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(errorTypeField)
                    .addComponent(detailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(errorTypeLabel)
                    .addComponent(errorTypeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel commandPanel;
    private javax.swing.JTextArea detailsField;
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JTextField errorTypeField;
    private javax.swing.JLabel errorTypeLabel;
    private javax.swing.JButton helpButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea messageField;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton reportButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
