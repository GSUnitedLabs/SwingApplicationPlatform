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
 *  Class      :   FileUtils.java
 *  Author     :   Sean Carrick
 *  Created    :   Jul 17, 2021 @ 1:03:31 PM
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The {@code FileUtils} class provide numerous static methods for manipulating
 * physical files.
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public class FileUtils {
    
    //<editor-fold defaultstate="collapsed" desc="Constructor(s)">
    private FileUtils () {
        // Privatized to prevent this class from being instantiated.
    }
    //</editor-fold>

    /**
     * Copies the file {@code toCopy} to the destination file {@code destFile}.
     * 
     * @param toCopy the file to copy
     * @param destFile the new location of the copied file
     * @return {@code true} upon success; {@code false} upon failure
     */
    public static boolean copyFile(final File toCopy, final File destFile) {
        try {
            return FileUtils.copyStream(new FileInputStream(toCopy), 
                                                new FileOutputStream(destFile));
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Copies all files in the {@code sourceDir} to the destination {@code 
     * destDir}. 
     * 
     * @param sourceDir the folder that contains the files to copy
     * @param destDir the destination folder for the copied files
     * @return {@code true} upon success; {@code false} upon failure
     */
    private static boolean copyFilesRecursively(final File sourceDir, 
                                                final File destDir) {
        assert destDir.isDirectory();
        
        if ( !sourceDir.isDirectory() ) {
            return FileUtils.copyFile(sourceDir, new File(destDir, sourceDir.getName()));
        } else {
            final File newDestDir = new File(destDir, sourceDir.getName());
            
            if ( !newDestDir.exists() && !newDestDir.mkdir() ) {
                return false;
            }
            
            for ( final File child : sourceDir.listFiles() ) {
                if ( !FileUtils.copyFilesRecursively(child, newDestDir)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public static boolean copyJarResourcesRecursively(final File destDir,
            final JarURLConnection jarConnection) throws IOException {
        final JarFile jarFile = jarConnection.getJarFile();
        
        for (final Enumeration<JarEntry> e = jarFile.entries(); 
                                                        e.hasMoreElements();) {
            final JarEntry entry = e.nextElement();
            
            if (entry.getName().startsWith(jarConnection.getEntryName())) {
                final String filename = StringUtils.removeStart(entry.getName(),
                        jarConnection.getEntryName());
                
                final File f = new File(destDir, filename);
                
                if (!entry.isDirectory()) {
                    final InputStream entryInputStream = jarFile.getInputStream(entry);
                    
                    if (!FileUtils.copyStream(entryInputStream, f)) {
                        return false;
                    }
                    
                    entryInputStream.close();
                } else {
                    if ( !FileUtils.ensureDirectoryExists(f)) {
                        throw new IOException("Could not create directory: " +
                                f.getAbsolutePath());
                    }
                }
            }
        }
        
        return true;
    }
    
    public static boolean copyResourcesRecursively(final URL originUrl,
                                                   final File destination) {
        try {
            final URLConnection urlConnection = originUrl.openConnection();
            
            if ( urlConnection instanceof JarURLConnection) {
                return FileUtils.copyJarResourcesRecursively(destination, 
                        (JarURLConnection) urlConnection);
            } else {
                return FileUtils.copyFilesRecursively(new File(
                                                      originUrl.getPath()), 
                                                      destination);
            }
        } catch ( final IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private static boolean copyStream(final InputStream is, final File f) {
        try {
            return FileUtils.copyStream(is, new FileOutputStream(f));
        } catch ( final FileNotFoundException e ) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private static boolean copyStream(final InputStream is,
                                      final OutputStream os) {
        try {
            final byte[] buf = new byte[1024];
            
            int len = 0;
            while ( (len = is.read(buf)) > 0 ) {
                os.write(buf);
            }
            
            is.close();
            os.close();
            return true;
        } catch ( final IOException e ) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Provides a means of determining if the given directory exists, and is not
     * a regular file. If {@code f} is a regular file, {@code false} is returned.
     * If {@code f} does not exist, the directory is created. Whether or not the
     * directory is created, {@code true} is returned if the directory exists.
     * 
     * @param f the file to verify, or create
     * @return {@code false} if the file is a regular file; {@code true} if the 
     * directory did not exist, but was created, or already exists; {@code false}
     * otherwise
     */
    public static boolean ensureDirectoryExists(final File f) {
        if (!f.isDirectory()) {
            return false;
        } else if (f.isDirectory()) {
            return true;
        } else if (!f.exists()) {
            return f.mkdir();
        }
        
        return false ;
    }
    
    /**
     * Gets the OS-specific location for the home folder of a given application.
     * This method makes sure to follow the "rules" of the operating system as
     * to where the application stores its files.
     * <p>
     * Applications can make whatever folder structure needed on the user's PC
     * hard drive. However, operating systems tend to desire that applications
     * do not make folders willy-nilly on the hard drive. Therefore, each 
     * operating system vendor has determined locations for third-party 
     * application developers to store their application's files. The OS-specific
     * locations are detailed in the table below.</p>
     * <table>
     * <caption>OS-Specific Application Top-Level Folder Locations</caption>
     * <tr>
     * <th>OS</th><th>Application Folder Location</th></tr>
     * <tr><td>Microsoft Windows<sup>&trade;</sup></td>
     * <td>{@code ${user.home}\\AppData\\${application.name}\\}</td></tr>
     * <tr><td>Apple Mac OS-X<sup>&trade;</sup></td>
     * <td>{@code ${user.home}/Library/Application Data/${application.name}/}</td></tr>
     * <tr><td>Linux and Solaris</td>
     * <td>{@code ${user.home}/.${application.name}/}</td></tr>
     * </table>
     * <p>
     * In order to follow these guidelines, this method has been created. It 
     * checks the system upon which the Java Virtual Machine (JVM) is running to
     * get its name, then builds the application folder path for that OS in a
     * {@code java.lang.String} and returns it to the calling class.</p>
     * 
     * @param appName the name of the application wanting to know where its
     *          OS-specific folder location should be located
     * @return the full path to the application's OS-specific folder location as
     *          a {@code java.io.File} object
     */
    public static File getApplicationDirectory(String appName) {
        String osName = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        String location = "";

        if (osName.contains("win")) {
            location = "AppData\\";
        } else if (osName.contains("mac")) {
            location = "Library/Preferences/";
        } else {
            location = ".";
        }

        return new File(userHome + File.separator + location + appName 
                + File.separator);
    }
    
    /**
     * A convenience method for getting a standard data directory in which to
     * store files from an application.
     * <p>
     * For applications that wish to store their data in a separate directory
     * from any other files the application may create, this method will get a
     * directory named "data", with the application's home directory prepended
     * to it. The path returned is described in the table below, by OS.</p>
     * <table>
     * <caption>OS-Specific Application Data Folder Locations</caption>
     * <tr>
     * <th>OS</th><th>Application Data Folder Location</th></tr>
     * <tr><td>Microsoft Windows<sup>&trade;</sup></td>
     * <td>{@code ${user.home}\\AppData\\${application.name}\\data\\}</td></tr>
     * <tr><td>Apple Mac OS-X<sup>&trade;</sup></td>
     * <td>{@code ${user.home}/Library/Application Data/${application.name}/data/}
     * </td></tr>
     * <tr><td>Linux and Solaris</td>
     * <td>{@code ${user.home}/.${application.name}/data/}</td></tr>
     * </table>
     * 
     * @param appName the name of the application wanting to know where its
     *          OS-specific data folder location should be located
     * @return the full path to the application's OS-specific folder location as
     *          a {@code java.io.File} object
     */
    public static File getApplicationDataDirectory(String appName) {
        return new File(getApplicationDirectory(appName) + File.separator
                + "data" + File.separator);
    }
    
    /**
     * A convenience method for getting a standard configuration file directory
     * in which applications settings and configurations may be stored.
     * <p>
     * For applications that wish to store their config files in a separate directory
     * from any other files the application may create, this method will get a
     * directory named "etc", with the application's home directory prepended
     * to it. The path returned is described in the table below, by OS.</p>
     * <table>
     * <caption>OS-Specific Application Configuration Folder Locations</caption>
     * <tr>
     * <th>OS</th><th>Application Configuration Folder Location</th></tr>
     * <tr><td>Microsoft Windows<sup>&trade;</sup></td>
     * <td>{@code ${user.home}\\AppData\\${application.name}\\etc\\}</td></tr>
     * <tr><td>Apple Mac OS-X<sup>&trade;</sup></td>
     * <td>{@code ${user.home}/Library/Preferences/${application.name}/}</td></tr>
     * <tr><td>Linux and Solaris</td>
     * <td>{@code ${user.home}/.${application.name}/etc/}</td></tr>
     * </table>
     * 
     * @param appName the name of the application wanting to know where its
     *          OS-specific configuration folder location should be located
     * @return the full path to the application's OS-specific folder location as
     *          a {@code java.io.File} object
     */
    public static File getApplicationConfigDirectory(String appName) {
        return new File(getApplicationDirectory(appName) + File.separator
                + "etc" + File.separator);
    }
    
    /**
     * A convenience method for getting a standard log file directory in which
     * applications may store log files.
     * <p>
     * For applications that wish to store their log files in a separate directory
     * from any other files the application may create, this method will get a
     * directory named "var/log" for Linux applications, with the application's 
     * home directory prepended to it. However, other operating systems have very
     * specific locations where they want log files created. The path returned 
     * for each OS is described in the table below, by OS.</p>
     * <table>
     * <caption>OS-Specific Application Log Folder Locations</caption>
     * <tr>
     * <th>OS</th><th>Application Log Folder Location</th></tr>
     * <tr><td>Microsoft Windows<sup>&trade;</sup></td>
     * <td>{@code %SystemRoot%\\System32\\Config\\${application.name}\\}</td></tr>
     * <tr><td>Apple Mac OS-X<sup>&trade;</sup></td>
     * <td>{@code ${user.home}/Library/Logs/${application.name}/}</td></tr>
     * <tr><td>Linux and Solaris</td>
     * <td>{@code /var/log/${application.name}} or 
     * {@code ${user.home}/.${application.name}}, if the system logs directory 
     * is not writable. Typically, an application that is installed system wide
     * will have write access to the {@code /var/log/${application.name}} folder.
     * If a user installs the application just for his/herself, then the logs
     * will be stored in the {@code ${user.home}/.${application.name}/var/log/}
     * folder.</td></tr>
     * </table>
     * 
     * @param appName the name of the application wanting to know where its
     *          OS-specific log file folder location should be located
     * @return the full path to the application's OS-specific folder location as
     *          a {@code java.io.File} object
     */
    public static File getApplicationLogDirectory(String appName) {
        String logDir = System.getProperty("system.root") + File.separator;
        
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            logDir = System.getenv("%SystemRoot%") + File.separator + "System32"
                    + File.separator + "Config" + appName + File.separator;
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            logDir = System.getProperty("user.home") + File.separator + "Library"
                    + File.separator + "Logs" + File.separator + appName
                    + File.separator;
        } else {
            // Attempt the /var/log folder on Linux and Solaris first.
            logDir += "var" + File.separator + "log" + File.separator;
            
            // Check to see if we can write to it.
            File logDirFile = new File(logDir);
            if (!logDirFile.canWrite()) {
                logDir = getApplicationDirectory(appName) + File.separator
                        + "var" + File.separator + "log" + File.separator;
            }
        }
        
        return new File(logDir);
    }
    
    /**
     * A convenience method for getting a standard error log file directory in 
     * which applications may store error log files.
     * <p>
     * For applications that wish to store their error log files in a separate 
     * directory from any other files the application may create, this method 
     * will get a directory named "var/err" for Linux applications, with the 
     * application's home directory prepended to it. However, other operating 
     * systems have very specific locations where they want log files created.
     * The path returned for each OS is described in the table below, by OS.</p>
     * <table>
     * <caption>OS-Specific Application Error Log Folder Locations</caption>
     * <tr>
     * <th>OS</th><th>Application Error Log Folder Location</th></tr>
     * <tr><td>Microsoft Windows<sup>&trade;</sup></td>
     * <td>{@code %SystemRoot%\\System32\\Config\\${application.name}\\err\\}</td></tr>
     * <tr><td>Apple Mac OS-X<sup>&trade;</sup></td>
     * <td>{@code ${user.home}/Library/Logs/${application.name}/err/}</td></tr>
     * <tr><td>Linux and Solaris</td>
     * <td>{@code /var/log/${application.name}/err/} or 
     * {@code ${user.home}/.${application.name}/var/err/}, if the system logs directory 
     * is not writable. Typically, an application that is installed system wide
     * will have write access to the {@code /var/log/${application.name}} folder.
     * If a user installs the application just for his/herself, then the logs
     * will be stored in the {@code ${user.home}/.${application.name}/var/log/}
     * folder.</td></tr>
     * </table>
     * 
     * @param appName the name of the application wanting to know where its
     *          OS-specific log file folder location should be located
     * @return the full path to the application's OS-specific folder location as
     *          a {@code java.io.File} object
     */
    public static File getApplicationErrorDirectory(String appName) {
        return new File(getApplicationLogDirectory(appName) + File.separator
                + "err" + File.separator);
    }
    
    /**
     * A convenience method for getting a standard system file directory in 
     * which applications may store system files.
     * <p>
     * For applications that wish to store their system files in a separate 
     * directory from any other files the application may create, this method 
     * will get a directory named "sys", with the application's home directory 
     * prepended to it.</p>
     * <table>
     * <caption>OS-Specific Application System Folder Locations</caption>
     * <tr>
     * <th>OS</th><th>Application System Folder Location</th></tr>
     * <tr><td>Microsoft Windows<sup>&trade;</sup></td>
     * <td>{@code ${user.home}\\AppData\\${application.name}\\sys\\}</td></tr>
     * <tr><td>Apple Mac OS-X<sup>&trade;</sup></td>
     * <td>{@code ${user.home}/Library/Application Data/${application.name}/sys/}</td></tr>
     * <tr><td>Linux and Solaris</td>
     * <td>{@code ${user.home}/.${application.name}/sys/}</td></tr>
     * </table>
     * 
     * @param appName the name of the application wanting to know where its
     *          OS-specific log file folder location should be located
     * @return the full path to the application's OS-specific folder location as
     *          a {@code java.io.File} object
     */
    public static File getApplicationSystemDirectory(String appName) {
        return new File(getApplicationDirectory(appName) + File.separator 
                + "sys" + File.separator);
    }

}
