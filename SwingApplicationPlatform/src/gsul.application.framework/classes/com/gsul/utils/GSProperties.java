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
 *  Class      :   GSProperties.java
 *  Author     :   Sean Carrick
 *  Created    :   Jul 17, 2021 @ 10:13:34 AM
 *  Modified   :   Jul 17, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Jul 17, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gsul.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public class GSProperties {
    
    private final Properties application;
    private final Properties runtime;
    
    public GSProperties () {
        application = new Properties();
        runtime = new Properties();
    }
    
    /**
     * Retrieves the property value associated with the specified key. The
     * application properties are checked first. If the property for the 
     * application does not exist, the runtime properties are then checked.
     * 
     * @param key the key for which the property value should be gotten
     * @return the property value or an empty string if it is not set
     * 
     * @see #getProperty(java.lang.String, java.lang.String) 
     */
    public String getProperty(String key) {
        String property = application.getProperty(key);
        
        if (property == null || property.isEmpty()) {
            property = runtime.getProperty(key);
        }
        
        return property == null ? "" : property;
    }
    
    /**
     * Retrieves the property value associated with the specified key. The
     * application properties are checked first. If the property for the 
     * application does not exist, the runtime properties are then checked. If
     * the property does not exist in either the application or the runtime
     * properties, the default value is returned.
     * 
     * @param key the key for which the property value should be returned
     * @param value a default value if the key does not exist
     * @return the property value for the supplied key, or the supplied default
     *          value
     * 
     * @see #getProperty(java.lang.String) 
     */
    public String getProperty(String key, String value) {
        String property = application.getProperty(key);
        
        if (property == null || property.isEmpty()) {
            property = runtime.getProperty(key);
        }
        
        return property == null ? value : property;
    }
    
    /**
     * Sets the supplied value to the specified property key for the application
     * properties. Application properties are saved to file whenever the method 
     * {@code store} is called. Runtime properties are not.
     * 
     * @param key the key to which the property value should be set
     * @param value the value to set for the specified key
     * 
     * @see #store(java.io.OutputStream, java.lang.String) 
     * @see #store(java.io.Writer, java.lang.String) 
     * @see #setRuntimeProperty(java.lang.String, java.lang.String) 
     */
    public void setApplicationProperty(String key, String value) {
        application.setProperty(key, value);
    }
    
    /**
     * Sets the supplied value to the specified property key for the runtime
     * properties. Runtime properties are not saved to file whenever the {@code 
     * store} method is called, as these properties are only set and used when
     * the application is executed. Application properties are saved.
     * 
     * @param key the key to which the property value should be set
     * @param value the value to set for the specified key
     * 
     * @see #setApplicationProperty(java.lang.String, java.lang.String) 
     * @see #store(java.io.OutputStream, java.lang.String) 
     * @see #store(java.io.Writer, java.lang.String) 
     */
    public void setRuntimeProperty(String key, String value) {
        runtime.setProperty(key, value);
    }
    
    /**
     * Saves the application properties to the specified {@code Writer} file.
     * Only the application properties are stored to file, as these are typically
     * needed early-on during application start-up. Runtime properties are only
     * used while the application is running and are typically only set after
     * the application has been fully initialized. An example of a runtime
     * property would be the currently logged on user.
     * 
     * @param writer the {@code java.io.Writer} object for the properties file
     * @param comments any comments that should be placed at the top of the
     *          properties file
     * @throws IOException in the event an error occurs while writing the file
     * 
     * @see #store(java.io.OutputStream, java.lang.String) 
     * @see #load(java.io.InputStream) 
     */
    public void store(Writer writer, String comments) throws IOException {
        application.store(writer, comments);
    }
    
    /**
     * Saves the application properties to the specified {@code OutputStream} file.
     * Only the application properties are stored to file, as these are typically
     * needed early-on during application start-up. Runtime properties are only
     * used while the application is running and are typically only set after
     * the application has been fully initialized. An example of a runtime
     * property would be the currently logged on user.
     * 
     * @param stream the {@code java.io.OutputStream} object for the properties 
     *      file
     * @param comments any comments that should be placed at the top of the
     *          properties file
     * @throws IOException in the event an error occurs while writing the file
     * 
     * @see #store(java.io.Writer, java.lang.String) 
     * @see #load(java.io.Reader) 
     */
    public void store(OutputStream stream, String comments) throws IOException {
        application.store(stream, comments);
    }
    
    /**
     * Loads the application properties from the specified {@code Reader} file.
     * Only the application properties are stored, so only application properties
     * are loaded. Runtime properties will be set during the application run.
     * 
     * @param reader the {@code java.io.Reader} object for the properties file
     * @throws IOException in the event the file does not exist or a read error
     *          occurs
     */
    public void load(Reader reader) throws IOException {
        application.load(reader);
    }
    
    /**
     * Loads the application properties from the specified {@code InputStream} file.
     * Only the application properties are stored, so only application properties
     * are loaded. Runtime properties will be set during the application run.
     * 
     * @param stream the {@code java.io.InputStream} object for the properties 
     *          file
     * @throws IOException in the event the file does not exist or a read error
     *          occurs
     */
    public void load(InputStream stream) throws IOException {
        application.load(stream);
    }

}
