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
 *  Created    :   Aug 19, 2021 @ 2:44:45 PM
 *  Modified   :   Aug 19, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Aug 19, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */

module gsul.application.platform {
    requires java.base;
    requires java.desktop;
    
    requires gsul.application.framework;
}
