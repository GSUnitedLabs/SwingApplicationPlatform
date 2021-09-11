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
 *  Project    :   framework
 *  Class      :   Action.java
 *  Author     :   Sean Carrick
 *  Created    :   Sep 6, 2021 @ 8:27:11 PM
 *  Modified   :   Sep 6, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Sep 06, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gsul.framework;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Action extends AbstractAction {
    
    private boolean selected;
    
    /**
     * Creates an Action.
     */
    public Action () {
        super();
        
        selected = false;
    }
    
    /**
     * Creates an Action with the specified name.
     * @param name 
     */
    public Action(String name) {
        super(name);
        
        selected = false;
    }
    
    public Action(String name, Icon icon) {
        super(name, icon);
        
        selected = false;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public abstract void setSelected(boolean selected);

}
