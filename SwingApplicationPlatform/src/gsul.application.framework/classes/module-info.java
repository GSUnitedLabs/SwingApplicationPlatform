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
 *  Project    :   SwingApplicationPlatform
 *  Class      :   module-info.java
 *  Author     :   Sean Carrick
 *  Created    :   Aug 17, 2021 @ 8:08:02 PM
 *  Modified   :   Aug 17, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Aug 17, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */

module gsul.application.framework {
    requires java.base;
    requires java.desktop;
    
    // Qualified export: we are only exporting to the Platform module so that it
    //+ has access to the com.gsul.utils package.
    exports com.gsul.utils to gsul.application.platform;
}
