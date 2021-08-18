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
 *  Class      :   GSLogger.java
 *  Author     :   Sean Carrick
 *  Created    :   Jul 17, 2021 @ 10:09:45 PM
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

import com.gsul.application.Application;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 0.1.0
 * @since 0.1.0
 */
public class GSLogger {
    
    /** OFF is a special level that can be used to turn off logging. */
    public static final int OFF = -1;
    /** DEBUG is the most verbose logging level. */
    public static final int DEBUG = 0;
    /** CONFIG is a message level for static configuration messages. */
    public static final int CONFIG = 1;
    /** INFO is a message level for informational messages. */
    public static final int INFO = 2;
    /** WARN is a message level indicating a potential problem. */
    public static final int WARN = 3;
    /** ERROR is a message level indicating a recoverable error. */
    public static final int ERROR = 4;
    /** CRITICAL is message level indicating a serious failure. */
    public static final int CRITICAL = 5;
    
    private String tempLogPath;
    private String logFilePath;
    private String errorPath;
    
    private Application app;
    private Class cls;
    private FileWriter log;
    private FileWriter err;
    private int level;
    private boolean fancy;
    
    private GSLogger (Application app, Class cls, int level) {
        this.cls = cls;
        this.app = app;
        this.level = level;
        this.fancy = false;
        
        initLogFile();
    }
    
    private GSLogger(Application app, Class cls, int level, boolean fancyOutput) {
        this(app, cls, level);
        
        this.level = level;
    }
    
    /**
     * Gets an instance of the {@code GSLogger} for the application and class
     * specified at the desired logging level.
     * 
     * @param app the {@code com.gsul.application.Application} in which this
     *          logger is being used
     * @param cls the class that is doing the logging
     * @param level the level at which messages should be logged
     * @return an instance of the {@code GSLogger} to use for logging messages
     */
    public static GSLogger getLogger(Application app, Class cls, int level) {
        return new GSLogger(app, cls, level);
    }
    
    /**
     * Gets an instance of the {@code GSLogger} for the application and class
     * specified at the desired logging level and with the desired output.
     * 
     * @param app the {@code com.gsul.application.Application} in which this
     *          logger is being used
     * @param cls the class that is doing the logging
     * @param level the level at which messages should be logged
     * @param fancyOutput {@code true} to have logged messages broken at the 80
     *          character mark; {@code false} to have messages logged as they
     *          are sent
     * @return an instance of the {@code GSLogger} to use for logging messages
     */
    public static GSLogger getLogger(Application app, Class cls, int level, boolean fancyOutput) {
        return new GSLogger(app, cls, level, fancyOutput);
    }
    
    private void initLogFile() {
        String appId = app.getApplicationId();
        File appHome = FileUtils.getApplicationDirectory(appId);
        
        try {
            if (!appHome.exists()) {
            tempLogPath = System.getProperty("user.home") + File.separator
                    + appId + "." + cls.getSimpleName() + "-tmp.log";

                log = new FileWriter(new File(tempLogPath));
            } else {
                // Backup any existing logs: keep 5 backups and the current.
//                LogRotate backup = new LogRotate(app, 5);
//                backup.rotateLogFiles(cls.getSimpleName());
                
                logFilePath = FileUtils.getApplicationLogDirectory(
                        appId).getAbsolutePath();
                log = new FileWriter(new File(logFilePath + cls.getSimpleName()
                        + ".log"), true);
            }
        } catch (IOException ex) {
            TerminalErrorPrinter.printException(ex);
            MessageBox.showWarning("Unable to initialize logger.\n\n"
                    + "GSLogger.initLogFiles()", "Logging Setup Failure");
        }
    }
    
    /**
     * This method is used by applications being run for the first time. On the
     * first run of an application, the application's home folder, most likely,
     * does not yet exist, so a temporary log file is created until such time
     * the application has a home folder. Once the application's home folder has
     * been created, then the temporary log <em>should be</em> copied to the
     * permanent log file location for the application. This provides a modicum
     * of continuity between the application's initial startup and all following
     * startups.
     */
    public void copyTempToPermanentLog() {
        String appId = app.getApplicationId();
        String permLog = FileUtils.getApplicationLogDirectory(
                appId).getAbsolutePath();
        if (!permLog.endsWith(File.separator)) {
            permLog += File.separator;
        }
        permLog += cls.getSimpleName() + ".log";
        
        File newLogFile = new File(permLog);
        
        try {
            // First, backup any old logs that may exist: keep 5 backups and the
            //+ current (being created) log.
//            LogRotate backup = new LogRotate(app, 5);
//            backup.rotateLogFiles(cls.getSimpleName());
            
            if (!newLogFile.exists()) {
                newLogFile.createNewFile();
            }
            
            FileWriter newLog = new FileWriter(newLogFile);
            
            if (newLog != null) {
                FileReader inFile = new FileReader(System.getProperty(
                        "user.home") + File.separator + appId 
                        + this.cls.getSimpleName() + "-tmp.log");
                BufferedReader in = new BufferedReader(inFile);
                String inLine;
                while ((inLine = in.readLine()) != null) {
                    newLog.write(inLine + "\n");
                }
                
                in.close();
                inFile.close();
                logFilePath = permLog;
                
                // Making the new log the log for this class.
                this.log = newLog;
                
                String oldFile = System.getProperty("user.home");
                if (!oldFile.endsWith(File.separator)) {
                    oldFile += File.separator;
                }
                oldFile += app + "." + this.cls.getSimpleName() + "-tmp.log";
                File oldLog = new File(oldFile);
                
                boolean oldLogDeleted = oldLog.delete();
                
                if (!oldLogDeleted) {
                    MessageBox.showInfo("The temporary log file was copied\n"
                            + "successfully, but not deleted. You\n"
                            + "manually delete it at your convenience.",
                            "Delete Failure");
                }
            }
        } catch (IOException ex) {
            TerminalErrorPrinter.printException(ex);
            String ttl = "Logging Setup Failure";
            MessageBox.showError(ex, ttl);
        }
    }
    
    /**
     * Closes the log file prior to the owner closing. For example:
     * <pre>
     * public class MyWindow extends javax.swing.JFrame implements WindowListener {
     *     
     *     private static final GSLogger logger;
     * 
     *     public MyWindow() {
     *         logger = new GSLogger(getClass().getSimpleName(), "ApplicationName");
     * 
     *         // ... rest of initialization ...
     *     }
     * 
     *     private void windowClosed(WindowEvent evt) {
     *         // ... handle window closing event for the form ...
     * 
     *         logger.close();
     *     }
     * }
     * </pre><p>
     * By making sure to close the logger on the closing of the class, you can 
     * be sure that all messages have been flushed from the buffer into the 
     * file.</p>
     * <p>
     * The only guarantee made by this method is that the flushing of the buffer
     * will only occur as long as the logging level is not set to {@code 
     * GSLogger.OFF}. As long as the logging level has the logger turned on, any
     * log messages remaining in the buffer will be written to the file.</p>
     */
    public void close() {
        // We need to try to close the log, however, we will only do so if 
        //+ logging is not turned off.
        if (this.level != OFF) {
            try {
                log.flush();
                log.close();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    
    /**
     * Logs a configuration message to the log file. Configuration messages can
     * consist of explaining that variables are being created and for what 
     * purpose, the initial values, etc. These messages are only logged when the
     * logger's level is set to {@code GSLogger.CONFIG} or higher, or if the
     * logger's level is set to {@code GSLogger.DEBUG}.
     * 
     * @param msg the configuration message to log
     */
    public void config(String msg) {
        if (this.level >= CONFIG || this.level == DEBUG) {
            try {
                if (fancy) {
                    configF(msg);
                } else {
                    log.write(cls.getSimpleName() + " - CONFIG: " + msg + "\n");
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void configF(String msg) throws IOException {
        log.write(StringUtils.wrapAt(cls.getSimpleName() + " - CONFIG: " 
                + msg, 80) + "\n");
    }

    /**
     * Logs a system critical error to the logging location, based upon the OS
     * on which the application is running, just before the application exits.
     * All non-fatal errors should be logged using the {@code error} method.
     * <p>
     * The {@code error} method is similar to this method, except that the {@code 
     * error} method does not take the {@code method} parameter and the name of
     * the generated error log file is different. The reason for the two 
     * different, but very similar, methods is that this one should be used for
     * <strong><em>fatal</em></strong> errors prior to forcibly exiting the 
     * application, whereas the {@code error} method should be used for <em>
     * non-fatal <strong>recoverable</strong></em> errors that do not cause the
     * application to exit.</p>
     * <p>
     * The {@code extraData} parameter should be used to supply information
     * pertinent to the context of the critical error. This parameter should
     * include, if it is used, application state, variable values, information
     * on what was currently being attempted, etc.</p>
     * <p>
     * The {@code critical} method automatically includes application information,
     * such as name, version, and build. It also includes OS-specific information,
     * such as name, architecture, and version. Also included in the error message
     * is the Java Runtime information for the JDK, classpath, JVM, etc. User
     * information is also included.</p>
     * <p>
     * Including any extra information via the {@code extraData} parameter will
     * aid your tech support in tracking down the issue to allow for faster 
     * corrections to be released to your customers.</p>
     * <p>
     * The typical use of the {@code critical} method would be similar to the 
     * following:</p>
     * <pre>
     * try {
     *     // ... something that throws an exception ...
     * } catch (Exception ex) {
     *     System.println(ex.getMessage());
     *     ex.printStacktrace(System.err);
     *     logger.critical(ex, "methodName", "All pertinent extra data, formatted");
     *     MessageBox.showError(ex, "Crash!");
     *     ApplicationName.exit(new java.util.ExitEvent(this));
     * }
     * </pre>
     * <dl><dt><strong><em>{@code critical} Note:</em></strong></dt>
     * <dd>Critical messages will always be logged, except if the {@code GSLogger}'s
     * level is set to {@code GSLogger.OFF}.</dd></dl>
     * 
     * @param ex the exception that was thrown. This will be used to build the
     *          error information portion of the report. The entire stacktrace
     *          will not be printed in the report, only the Project-relevant
     *          portion. For the entire stacktrace, {@code 
     *          ex.printStacktrace(System.err)} should be used.
     * @param method the name of the method where the error occurred.
     * @param extraData any extra data that cannot be determined by the {@code 
     *          System.getProperties()} or from the application's {@code 
     *          ResourceMap}s. This parameter could include such things as
     *          what was being attempted, application state, variable values,
     *          etc.
     * 
     * @see #error(Exception, String)
     */
    public void critical(Exception ex, String method, String extraData) {
        if (level != OFF) {
            String appId = app.getApplicationId();
            File errDir = FileUtils.getApplicationErrorDirectory(appId);
            String errPath = errDir.getAbsolutePath();
            if (!errPath.endsWith(File.separator)) {
                errPath += File.separator;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
            errPath += cls.getSimpleName() + sdf.format(new Date())
                    + "-CRITICAL.log";

            try {
                err = new FileWriter(new File(errPath));
                err.write(buildErrorReport(ex, method, extraData));

                err.flush();
                err.close();

                StringBuilder src = new StringBuilder();
                src.append(cls.getSimpleName()).append(" - ");
                src.append("CRITICAL: See error log: ").append(errPath).append("\n");

                log.write(src.toString());

                log.flush();
            } catch (IOException e) {
                TerminalErrorPrinter.printException(e);
            }
        }
    }
    
    /**
     * Logs a system recoverable error to the logging location, based upon the OS
     * on which the application is running, just before or during error recovery.
     * All fatal errors should be logged using the {@code critical} method.
     * <p>
     * The {@code critical} method is similar to this method, except that the {@code 
     * critical} method takes the {@code method} parameter and the name of
     * the generated error log file is different. The reason for the two 
     * different, but very similar, methods is that this one should be used for
     * <strong><em>non-fatal</em></strong> errors prior to or during recovery from 
     * the error, whereas the {@code critical} method should be used for <em>
     * fatal <strong>non-recoverable</strong></em> errors just prior to forcing
     * the application to exit.</p>
     * <p>
     * The {@code extraData} parameter should be used to supply information
     * pertinent to the context of the critical error. This parameter should
     * include, if it is used, application state, variable values, information
     * on what was currently being attempted, etc.</p>
     * <p>
     * The {@code error} method automatically includes application information,
     * such as name, version, and build. It also includes OS-specific information,
     * such as name, architecture, and version. Also included in the error message
     * is the Java Runtime information for the JDK, classpath, JVM, etc. User
     * information is also included.</p>
     * <p>
     * Including any extra information via the {@code extraData} parameter will
     * aid your tech support in tracking down the issue to allow for faster 
     * corrections to be released to your customers.</p>
     * <p>
     * The typical use of the {@code error} method would be similar to the 
     * following:</p>
     * <pre>
     * try {
     *     // ... something that throws an exception ...
     * } catch (Exception ex) {
     *     System.println(ex.getMessage());
     *     ex.printStacktrace(System.err);
     *     logger.error(ex, "All pertinent extra data, formatted");
     *     MessageBox.showError(ex, "Error");
     *     
     *     // ... perform whatever recovery is possible ...
     * }
     * </pre>
     * <dl><dt><strong><em>{@code error} Note:</em></strong></dt>
     * <dd>Error messages will always be logged, except if the {@code GSLogger}'s
     * level is set to {@code GSLogger.OFF}.</dd></dl>
     * 
     * @param errorClazz the class in which the error occurred
     * @param ex the exception that was thrown. This will be used to build the
     *          error information portion of the report. The entire stacktrace
     *          will not be printed in the report, only the Project-relevant
     *          portion. For the entire stacktrace, {@code 
     *          ex.printStacktrace(System.err)} should be used.
     * @param extraData any extra data that cannot be determined by the {@code 
     *          System.getProperties()} or from the application's {@code 
     *          ResourceMap}s. This parameter could include such things as
     *          what was being attempted, application state, variable values,
     *          etc.
     * 
     * @see #critical(Exception, String, String)
     */
    public void error(Class errorClazz, Exception ex, String extraData) {
        if (level != OFF) {
            String appId = app.getApplicationId();
            File errDir = FileUtils.getApplicationErrorDirectory(appId);
            String errPath = errDir.getAbsolutePath();
            if (!errPath.endsWith(File.separator)) {
                errPath += File.separator;
            }
            errPath += errorClazz.getName() + "-ERROR.log";

            try {
                err = new FileWriter(new File(errPath));
                err.write(buildErrorReport(ex, null, extraData));

                err.flush();
                err.close();

                StringBuilder src = new StringBuilder();
                src.append(errorClazz.getName()).append(" - ");
                src.append("CRITICAL: See error log: ").append(errPath).append("\n");

                log.write(src.toString());

                log.flush();
            } catch (IOException e) {
                TerminalErrorPrinter.printException(e);
            }
        }
    }
    
    /**
     * If the {@code GSLogger} is currently enabled for debugging messages, {@code 
     * GSLogger.DEBUG}, then the given message is written to the log file.
     * 
     * @param msg the message to write
     * 
     * @see #debug(java.lang.String, java.lang.String[], java.lang.Object[]) 
     */
    public void debug(String msg) {
        if (level != OFF && level == DEBUG) {
            try {
                if (fancy) {
                    debugF(msg);
                } else {
                    log.write(msg);
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void debugF(String msg) throws IOException {
        log.write(StringUtils.wrapAt("DEBUG: " + msg, 80));
    }
    
    /**
     * If the {@code GSLogger} is currently enabled for debugging messages, {@code 
     * GSLogger.DEBUG}, then the given message is written to the log file. This
     * method also provides a means of logging variable names and their associated
     * values. The values printed out are not necessarily the actual value of 
     * the variable, as this method uses the {@code Object.toString()} method to
     * print the value.
     * <p>
     * If you desire for the actual value of the variable to be printed, it is
     * advised to use the {@link #debug(java.lang.String) debug(String)} method
     * and build up the variable names and values yourself.</p>
     * 
     * @param msg the message to write
     * @param variables an array of the names of the variables to record
     * @param values a matching array of the values of the variable to record
     * @throws IllegalArgumentException in the event that the {@code variables}
     *          and {@code values} arrays do not have the same length.
     * 
     * @see #debug(java.lang.String, java.lang.String[], java.lang.Object[]) 
     */
    public void debug(String msg, String[] variables, Object[] values) {
        if (level != OFF && level == DEBUG) {
            if (variables.length != values.length) {
                throw new IllegalArgumentException("variables.length != values."
                        + "length");
            }
            try {
                if (fancy) {
                    debugF(msg, variables, values);
                } else {
                    StringBuilder src = new StringBuilder();
                    src.append("DEBUG: ").append(msg).append(" - ");

                    for (int idx = 0; idx < variables.length; idx++){
                        src.append(variables[idx]).append("==");
                        src.append(values[idx]).append(": ");
                    }

                    log.write(src.toString());
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void debugF(String msg, String[] variables, Object[] values) 
            throws IOException {
                    StringBuilder src = new StringBuilder();
                    src.append("DEBUG: ").append(msg).append(" - ");

                    for (int idx = 0; idx < variables.length; idx++){
                        src.append("\n\t").append(variables[idx]).append("==");
                        src.append(values[idx]).append(": ");
                    }

                    log.write(StringUtils.wrapAt(src.toString(), 80));
    }
    
    /**
     * Logs a message when entering a method. This method is used for entering
     * methods with no parameters. The {@code enter} method will write to the
     * log file regardless of the level setting for this {@code GSLogger}, other
     * than when the level is set to {@code GSLogger.OFF}.
     * 
     * @param sourceMethod the name of the method entered
     */
    public void enter(String sourceMethod) {
        if (level != OFF) {
            try {
                if (fancy) {
                    enterF(sourceMethod);
                } else {
                    log.write("ENTER: " + cls.getSimpleName() + "." 
                            + sourceMethod);
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void enterF(String sourceMethod) throws IOException {
        log.write(StringUtils.wrapAt("ENTER: " + cls.getSimpleName() + "." 
                + sourceMethod, 80));
    }
    
    /**
     * Logs a message when entering a method. This method is used for entering
     * methods with a single parameters. The {@code enter} method will write to 
     * the log file regardless of the level setting for this {@code GSLogger}, 
     * other than when the level is set to {@code GSLogger.OFF}.
     * 
     * @param sourceMethod the name of the method entered
     * @param param the method's parameter
     */
    public void enter(String sourceMethod, Object param) {
        if (level != OFF) {
            try {
                if (fancy) {
                    enterF(sourceMethod, param);
                } else {
                    log.write("ENTER: " + cls.getSimpleName() + "." 
                            + sourceMethod + " (" 
                            + param.getClass().getSimpleName() + "=" 
                            + param.toString() + ")");
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void enterF(String sourceMethod, Object param) throws IOException {
        log.write(StringUtils.wrapAt("ENTER: " + cls.getSimpleName() + "." 
                + sourceMethod + " (" + param.getClass().getSimpleName()
                + " " + param.toString() + ")", 80));
    }
    
    /**
     * Logs a message when entering a method. This method is used for entering
     * methods with a single parameters. The {@code enter} method will write to 
     * the log file regardless of the level setting for this {@code GSLogger}, 
     * other than when the level is set to {@code GSLogger.OFF}.
     * 
     * @param sourceMethod the name of the method entered
     * @param params the method's parameter
     */
    public void enter(String sourceMethod, Object[] params) {
        if (level != OFF) {
            try {
                if (fancy) {
                    enterF(sourceMethod, params);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ENTER: ").append(cls.getSimpleName()).append(".");
                    sb.append(sourceMethod).append(" (");

                    int count = 0;
                    for (Object param : params) {
                        sb.append(param.getClass().toString()).append("=");
                        sb.append(param.toString());
                        if (count < params.length) {
                            sb.append(", ");
                        }
                    }
                    sb.append(")");

                    log.write(sb.toString());
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void enterF(String sourceMethod, Object[] params) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("ENTER: ").append(cls.getSimpleName()).append(".");
        sb.append(sourceMethod).append(" (");

        int count = 0;
        for (Object param : params) {
            sb.append(param.getClass().toString()).append("=");
            sb.append(param.toString());
            if (count < params.length) {
                sb.append(", ");
            }
        }
        sb.append(")");

        log.write(StringUtils.wrapAt(sb.toString(), 80));
    }
    
    
    /**
     * Logs a message when exiting a method. This method is used for exiting
     * methods with no return value. The {@code exit} method will write to the
     * log file regardless of the level setting for this {@code GSLogger}, other
     * than when the level is set to {@code GSLogger.OFF}.
     * 
     * @param sourceMethod the name of the method exited
     */
    public void exit(String sourceMethod) {
        if (level != OFF) {
            try {
                if (fancy) {
                    exitF(sourceMethod);
                } else {
                    log.write("EXIT: " + cls.getSimpleName() + "." 
                            + sourceMethod);
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void exitF(String sourceMethod) throws IOException {
        log.write(StringUtils.wrapAt("EXIT: " + cls.getSimpleName() + "." 
                + sourceMethod, 80));
    }
    
    /**
    /**
     * Logs a message when exiting a method. This method is used for exiting
     * methods with a return value. The {@code exit} method will write to the
     * log file regardless of the level setting for this {@code GSLogger}, other
     * than when the level is set to {@code GSLogger.OFF}.
     * 
     * @param sourceMethod the name of the method exited
     * @param returnValue the method's return value
     */
    public void exit(String sourceMethod, Object returnValue) {
        if (level != OFF) {
            try {
                if (fancy) {
                    exitF(sourceMethod, returnValue);
                } else {
                    log.write("EXIT: " + cls.getSimpleName() + "." 
                            + sourceMethod + " returning: " 
                            + returnValue.getClass().getSimpleName() + "=" 
                            + returnValue.toString());
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void exitF(String sourceMethod, Object returnValue) throws IOException {
        log.write(StringUtils.wrapAt("EXIT: " + cls.getSimpleName() + "." 
                + sourceMethod + " returning: " + returnValue.getClass().getSimpleName()
                + " " + returnValue.toString(), 80));
    }
    
    /**
     * Logs a message when entering a method. This method is used for entering
     * methods with an array of parameters. The {@code enter} method will write  
     * to the log file regardless of the level setting for this {@code GSLogger}, 
     * other than when the level is set to {@code GSLogger.OFF}.
     * 
     * @param sourceMethod the name of the method entered
     * @param returnValues the method's return values
     */
    public void exit(String sourceMethod, Object[] returnValues) {
        if (level != OFF) {
            try {
                if (fancy) {
                    enterF(sourceMethod, returnValues);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("EXIT: ").append(cls.getSimpleName()).append(".");
                    sb.append(sourceMethod).append(" returning: ");

                    int count = 0;
                    for (Object param : returnValues) {
                        sb.append(param.getClass().toString()).append("=");
                        sb.append(param.toString());
                        if (count < returnValues.length) {
                            sb.append(", ");
                        }
                    }

                    log.write(sb.toString());
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void exitF(String sourceMethod, Object[] params) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("EXIT: ").append(cls.getSimpleName()).append(".");
        sb.append(sourceMethod).append(" returning: ");

        int count = 0;
        for (Object param : params) {
            sb.append(param.getClass().toString()).append("=");
            sb.append(param.toString());
            if (count < params.length) {
                sb.append("\n");
            }
        }

        log.write(StringUtils.wrapAt(sb.toString(), 80));
    }
    
    /**
     * Retrieves the {@code GSLogger}'s current logging level. Though not needed
     * in most cases, this method is available for applications to use to check
     * the logging level before making calls to logging message methods, if it
     * desires.
     * 
     * @return the current level to which this {@code GSLogger} is set
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Determines if the log file output is &quot;fancy&quot;, meaning that the
     * written lines are broken at 80 characters.
     * 
     * @return {@code true} means the log lines are broken at 80 characters;
     *          {@code false} means the log lines are not broken
     */
    public boolean isFancy() {
        return fancy;
    }
    
    /**
     * Logs an information message to the log file. For this message to actually
     * be logged, the level for messages must be set to: at least {@code
     * GSLogger.INFO}, or less ({@code CONFIG} or {@code DEBUG}. This message will 
     * not be logged at any higher logging level.
     * 
     * @param msg the message to log
     */
    public void info(String msg) {
        if (level >= INFO || level == DEBUG) {
            try {
                if (fancy) {
                    infoF(msg);
                } else {
                    log.write(msg);
                }
                log.flush();
            } catch (IOException ex) {
                TerminalErrorPrinter.printException(ex);
            }
        }
    }
    private void infoF(String msg) throws IOException {
        log.write(StringUtils.wrapAt(msg, 80));
    }
    
    /**
     * Changes the logging level for this {@code GSLogger} to the level provided.
     * 
     * @param level the new logging level for more or less verbosity
     * @throws InvalidLoggingLevelException in the event an invalid level value
     *          is provided
     */
    public void setLevel(int level) throws InvalidLoggingLevelException {
        switch(level) {
            case OFF:
            case DEBUG:
            case CONFIG:
            case INFO:
            case WARN:
            case ERROR:
            case CRITICAL:
                this.level = level;
                break;
            default:
                throw new InvalidLoggingLevelException(level + " is not a valid"
                        + " logging level.");
        }
    }
    
    /**
     * Sets whether the log file output should be &quot;fancy&quot;, meaning the
     * log file will have all of its lines broken at 80 characters.
     * 
     * @param fancy {@code true} will break all written lines at 80 characters;
     *          {@code false} will not break the lines.
     */
    public void setFancy(boolean fancy) {
        this.fancy = fancy;
    }
    
    /**
     * Logs a warning message. Warning messages are typically used for logging
     * that an issue <em>could</em> arise within the program, not that one did
     * at this time.
     * 
     * @param msg the warning message to log
     * @param e the exception that was thrown, if any
     */
    public void warning(String msg, Exception e) {
        if (level >= WARN || level == DEBUG) {
            try {
                if (fancy) {
                    warnF(msg, e);
                } else {
                    log.write("WARNING: " + msg + "" + "\n    Exception" 
                            + e.getClass().getSimpleName() + " - Message: "
                            + e.getMessage());
                }
                log.flush();
            } catch (IOException ex) {
                
            }
        }
    }
    private void warnF(String msg, Exception e) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.wrapAt("WARNING: " + msg, 80));
        sb.append("\nException: ").append(e.getMessage()).append("\n");
        sb.append("Project Stacktrace:\n");
        for (StackTraceElement s : e.getStackTrace()) {
            if (e.toString().contains(cls.getCanonicalName())) {
                sb.append(s.toString()).append("\n");
            }
        }
        
        log.write(sb.toString());
    }
    
    public static class InvalidLoggingLevelException extends Exception {
        
        public InvalidLoggingLevelException() {
            super();
        }
        
        public InvalidLoggingLevelException(String msg) {
            super(msg);
        }
        
        public InvalidLoggingLevelException(Throwable cause) {
            super(cause);
        }
        
        public InvalidLoggingLevelException(String msg, Throwable cause) {
            super(msg, cause);
        }
        
    }
    
    private String buildErrorReport(Exception ex, String method
            , String extraData) {
        StringBuilder src = new StringBuilder();
        String rule = StringUtils.repeat("-", 80);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - HH:mm:ss");
        String timeStamp = sdf.format(new Date());
        
        src.append("Error Location: ");
        String pkg = cls.getCanonicalName();
        if (pkg == null) {
             pkg = "Anonymous";
        } else {
            pkg = pkg.substring(0, pkg.lastIndexOf("."));
        }
        src.append("   Package: ").append(pkg).append("\n");
        src.append("     Class: ").append(cls.getSimpleName()).append("\n");
        
        if (method != null) {
            src.append("    Method: ").append(method).append("\n");
        }
        src.append(rule).append("\n");
        src.append(" Exception: ").append(ex.getClass().getName()).append("\n");
        src.append("   Message: ").append(ex.getMessage()).append("\n");
        src.append("Stacktrace:\n");
        for (StackTraceElement e : ex.getStackTrace()) {
            src.append("    ").append(e.toString()).append("\n");
        }
        
        src.append(rule).append("\n");
        String appName = app.getApplicationName();
        int appNameLen = appName.length() + " Information".length();
        src.append(StringUtils.repeat(" ", 80 - (appNameLen / 2)));
        src.append(appName).append(" Information").append("\n");
        src.append("   Version: ").append(app.getVersionString());
        src.append("\n\n");
        String sysInfo = "System Information";
        int sysInfoLen = sysInfo.length();
        src.append(StringUtils.repeat(" ", 80 - (sysInfoLen / 2)));
        src.append(sysInfo).append("\n");
        src.append("        OS: ").append(System.getProperty("os.name")).append("\n");
        src.append("      Arch:").append(System.getProperty("os.arch")).append("\n");
        src.append("   Version: ").append(System.getProperty("os.version"));
        src.append("\n");
        
        String javaInfo = "Java Information";
        int javaInfoLen = javaInfo.length();
        src.append(StringUtils.repeat(javaInfo, 80 - (javaInfoLen / 2))).append("\n");
        Enumeration keys = System.getProperties().keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (key.contains("java") || key.contains("sun") || key.contains("awt")) {
                src.append(key).append(" = ").append(System.getProperty(key));
                src.append("\n");
            }
        }
        src.append("\n");
        
        String usrInfo = "User Information";
        int usrInfoLen = usrInfo.length();
        src.append(StringUtils.repeat(usrInfo, 80 - (usrInfoLen / 2))).append("\n");
        keys = System.getProperties().keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (key.contains("user")) {
                src.append(key).append(" = ").append(System.getProperty(key));
                src.append("\n");
            }
        }
        
        return src.toString();
    }
}
