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
 *  Project    :   GS_United_Labs_Swing_Application_Platform
 *  Class      :   module-info.java
 *  Author     :   Sean Carrick
 *  Created    :   Aug 24, 2021 @ 8:33:25 AM
 *  Modified   :   Aug 24, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Aug 24, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */

module gsul.platform.api {
    requires java.base;
    requires java.desktop;
    requires java.logging;
    requires gsul.framework.api;
}