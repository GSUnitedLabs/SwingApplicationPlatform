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
 *  Project    :   NTA-Basic
 *  Class      :   ToolbarProvider.java
 *  Author     :   Sean Carrick
 *  Created    :   Aug 16, 2021 @ 3:28:51 PM
 *  Modified   :   Aug 16, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Aug 16, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gsul.application.support;

/**
 * The {@code com.gsul.app.api.ToolbarProvider} interface is used to 
 * identify an {@link com.gsul.application.Action Action} as needing a toolbar button. 
 * When the application discovers such a class from a provided module, it can 
 * then build the toolbar button, in the main toolbar and at the specified position
 * in the toolbar, when the application starts.
 * <p>
 * Of importance is the fact that no button will be added to the toolbar between
 * the application's <em>Exit</em> and <em>Paste</em> buttons. Also important is
 * to note that no button will ever be placed <em>after</em> the <em>Help</em>
 * button.</p>
 * <p>
 * The type of the button to add to the toolbar is more limited than when creating
 * a GUI manually. The button must be one of the 
 * {@link com.gsul.app.api.ToolbarType ToolbarType}
 * constants. The toolbar button types allowed are:</p>
 * <ul>
 * <li>BUTTON: a standard {@code javax.swing.JButton}</li>
 * <li>COMBOBOX: a standard {@code javax.swing.JComboBox}</li>
 * <li>LABEL: a standard {@code javax.swing.JLabel}</li>
 * <li>TEXTFIELD: a standard {@code javax.swing.JTextField}</li>
 * <li>TOGGLE_BUTTON: a {@code javax.swing.JToggleButton}</li>
 * </ul><p>
 * The icon for the button is contained in the implementing {@code Action} class.
 * </p>
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ToolbarProvider {
    
    /**
     * Gets the toolbar in which the implementing {@code Action} needs to be 
     * placed.
     * 
     * @return the name of the toolbar in which to place the {@code Action}
     * 
     * @see #getToolbarType() 
     */
    public String whichToolbar();
    
    /**
     * Gets the text to place on the button.
     * 
     * @return the button's text
     */
    public String getButtonText();
    
    /**
     * Gets the type of the toolbar. The type will be one of the {@code 
     * ToolbarType}
     * constants:
     * <ul>
     * <li>TOOLBAR: a toolbar that contains toolbar buttons</li>
     * <li>BUTTON: a standard toolbar button</li>
     * <li>COMBOBOX: a combo box item</li>
     * <li>LABEL: a label item</li>
     * <li>TEXTFIELD: a text field item</li>
     * <li>TOGGLEBUTTON: a toggle button item</li>
     * </ul><p>
     * If this {@code ToolbarProvider} is providing a toolbar, it will be placed
     * as closely as possible to its desired position as determined by a call to
     * {@code getButtonPosition}.</p>
     * 
     * @return the {@code MenuType} constant for the type of menu item to be
     *          created
     */
    public ToolbarType getToolbarType();
    
    /**
     * Gets the desired position of the button. The
     * position should always be in increments of 500. This is because if more
     * than one {@code MenuProvider} {@code Action} requests the same position,
     * the value of one can be adjusted.
     * 
     * @return the desired position of the button
     */
    public int getButtonPosition();
    
    /**
     * Determines if the button wants to have a separator placed before it.
     * 
     * @return {@code true} places a separator before the button
     */
    public boolean hasSeparatorBefore();
    
    /**
     * Determines if the button wants to have a separator placed after it.
     * 
     * @return {@code true} places a separator after the button
     */
    public boolean hasSeparatorAfter();
    
    /**
     * Retrieves the {@code javax.swing.JComponent} that is to be added to the 
     * toolbar by this {@code ToolbarProvider}. {@code JComponent} is the first
     * common base class of all of the components that are allowed to be added
     * to an application toolbar. The application will determine if the returned
     * {@code JComponent} is itself a {@code JToolbar} by performing an {@code 
     * instanceof} check. If it is a {@code JToolbar}, it will be added to the 
     * application's toolbars collection as close to its desired position as
     * possible. If it is one of the other acceptable {@code JComponent}s, then
     * it will be added to the specified application toolbar, again, as close to
     * its desired position as possible.
     * 
     * @return the {@code JComponent} to add to the application's toolbars or
     *      the {@code JToolBar} to add to the applications toolbars collection
     */
    public javax.swing.JComponent getToolbar();

}
