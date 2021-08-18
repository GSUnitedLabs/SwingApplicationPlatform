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
 *  Class      :   MenuProvider.java
 *  Author     :   Sean Carrick
 *  Created    :   Aug 16, 2021 @ 2:28:27 PM
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
 * The {@code com.gsul.app.api.MenuProvider} interface is used to identify an
 * {@link com.gsul.application.Action Action} as needing a menu item. When the
 * application discovers such a class from a provided module, it can then build
 * the menu item, in the specified menu and at the specified position in the
 * menu, when the application starts.
 * <p>
 * The {@code MenuProvider} is also capable of providing a new menu to be placed
 * on the main menubar. In order to have this happen, the {@code 
 * com.gsul.application.api.MenuType} will need to be the constant {@code 
 * MenuType.MENU} and the position will need to be {@code -1}. This tells the
 * platform to place the menu on the main menubar in the first available position
 * between the Tools and Help menus.</p>
 * <dl><dt><strong><em>Developer Note</em></strong>:</dt>
 * <dd>{@code MenuProvider} {@code Action}'s that are being placed on the main
 * menubar are placed in a first-encountered, first-added manner. There is no
 * method by which the {@code MenuProvider} can specify the position on the main
 * menubar. Furthermore, a {@code MenuProvider} being placed on the main menubar
 * will <em>only</em> be placed <em>between</em> the <strong>Tools</strong> and
 * <strong>Help</strong> menus.</dd></dl>
 * <p>
 * The menus that are built by creating from {@code MenuProvider}s do have some
 * set menus that occupy a specific position and cannot be altered. Such menus
 * are:</p>
 * <ul>
 * <li><em>New</em>: This menu will always be the first item in the <em>File</em>
 * menu.</li>
 * <li><em>Exit</em>: This menu item will always be the last menu item in the 
 * <em>File</em> menu.</li>
 * <li><em>Undo</em>, <em>Redo</em>, <em>Cut</em>, <em>Copy</em>, and <em>Paste
 * </em>: These five menu items will always be the first five menu items in the
 * <em>Edit</em> menu. Also, there will always be a separator just below the 
 * <em>Paste</em> menu item.</li>
 * <li><em>Options</em>: This menu item will always be the last menu item in the
 * <em>Tools</em> menu and will always have a separator just above it.</em></li>
 * <li><em>Contents</em>: This will always be the first menu item in the <em>
 * Help</em> menu, with a separator just below it.</li>
 * <li><em>About</em>: This will always be the last menu item in the <em>Help</em>
 * menu, with a separator just above it.</li></ul>
 * <p>
 * The icon for the menu is contained in the implementing {@code Action} class.
 * 
 * @see com.gsul.application.Action
 * @see com.gsul.app.api.MenuType
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MenuProvider {
    
    /**
     * Gets the menu in which the implementing {@code Action} needs to be placed.
     * 
     * @return the name of the menu in which to place the {@code Action}
     * 
     * @see #getMenuItemType() 
     */
    public String getWhichMenu();
    
    /**
     * Gets the text to display on the menu item.
     * 
     * @return the menu item text
     */
    public String getMenuItemText();
    
    /**
     * Gets the type of the menu. The type will be one of the {@code MenuType}
     * constants:
     * <ul>
     * <li>MENU: a menu that contains menu items</li>
     * <li>MENU_ITEM: a standard menu item</li>
     * <li>CHECKED_MENU_ITEM: a checked menu item</li>
     * <li>RADIO_OPTION_MENU_ITEM: a radio button menu item</li>
     * <ul>
     * 
     * @return the {@code MenuType} constant for the type of menu item to be
     *          created
     */
    public MenuType getMenuType();
    
    /**
     * Gets the desired position that the menu item would like in the menu. The
     * position should always be in increments of 500. This is because if more
     * than one {@code MenuProvider} {@code Action} requests the same position,
     * the value of one can be adjusted.
     * 
     * @return the desired menu position
     */
    public int getMenuPosition();
    
    /**
     * If {@code true}, then a {@code javax.swing.JSeparator} will be placed
     * above this {@code MenuProvider} {@code Action}'s menu item.
     * <dl><dt><strong><em>Developer Note</em></strong></dt>
     * <dd>If the {@code MenuProvider} has the type of {@code MenuType.MENU} and
     * the return value from a call to {@code MenuProvider.getWhichMenu()} returns
     * {@code null}, the {@code hasSeparatorAbove} method will not be called.</dd>
     * </dl>
     * 
     * @return {@code true} place a separator above the menu item
     */
    public boolean hasSeparatorAbove();
    
    /**
     * If {@code true}, then a {@code javax.swing.JSeparator} will be placed
     * below this {@code MenuProvider} {@code Action}'s menu item.
     * <dl><dt><strong><em>Developer Note</em></strong></dt>
     * <dd>If the {@code MenuProvider} has the type of {@code MenuType.MENU} and
     * the return value from a call to {@code MenuProvider.getWhichMenu()} returns
     * {@code null}, the {@code hasSeparatorBelow} method will not be called.</dd>
     * </dl>
     * @return 
     */
    public boolean hasSeparatorBelow();
    
    /**
     * Retrieves the {@code javax.swing.JMenuItem} for this implementing 
     * {@code Action}. Since {@code javax.swing.JMenu} is a subclass of the
     * {@code JMenuItem} class, the application will be able to determine if the
     * returned object is a single {@code JMenuItem} or an entire {@code JMenu}
     * with enclosed {@code JMenuItem}s and take the appropriate action based on
     * the determination of {@code ${ReturnedItem} instanceof JMenu}.
     * 
     * @return the {@code JMenuItem} or {@code JMenu} for this {@code MenuProvider}
     */
    public javax.swing.JMenuItem getMenu();
}
