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
 *  Class      :   LogRecord.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 23, 2021 @ 7:45:31 PM
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

import java.time.Instant;
import java.util.ResourceBundle;

/**
 * The `LogRecord` class provides the means by which to send detailed messages
 * to the `Logger` messaging methods. By using the `LogRecord` to record all of
 * the data to be logged, the log messages will be detailed and provide better
 * understanding of the inner workings of a running application.
 * <p>
 * The typical class will define a single `LogRecord` to be used throughout that
 * class. The common way of accomplishing this is:</p>
 * ```java public class MyClass {
 *
 * private Logger logger; private final LogRecord record = new
 * LogRecord(MyClass.class.getSimpleName()); private final Application app; //
 * ...other required fields...
 *
 * public MyClass(Application application) { // other parameters can be listed
 * after Application. logger = Logger.getLogger(application);
 * record.setSourceMethodName("MyClass (Constructor)"); record.setParameters(new
 * Object[] {application}); logger.enter(record); app = application;
 *
 *         // the rest of the class' initialization.
 *
 * logger.exit(record); }
 *
 * public void doSomething(String name, int id, float pay) { // Let's assume
 * that name == Joe, id == 5, and pay == 5.5 record.setParameters(new Object[]
 * {name, id, pay}); record.setSourceMethodName("doSomething");
 * logger.enter(record);
 *
 *         // Based upon the assumed values above, calc would then equal 27.5 float
 * calc = pay * id; record.setMessage("When multiplying pay (%2f) by id (%d),
 * the " + "product is %2f"); record.setParameters(new Object[] {pay, id,
 * calc}); logger.debug(record);
 *
 *         // the rest of the method...
 *
 * logger.exit(record); }
 *
 * }
 * ```
 * <p>
 * As can be seen in the example above, a format string may be set to the
 * `message` property of the `LogRecord`, then the parameters need to be set to
 * hold the parameters to the format string. A call to the above `doSomething`
 * message will have the following three messages entered into the log file:</p>
 * <pre>
 * Sat Oct 23 20:41:49 CDT 2021: ENTERING MyClass.doSomething(Joe, 5, 5.5)
 * Sat Oct 23 20:41:51 CDT 2021: DEBUG: When multiplying pay (5.5) by id (5), the product is 27.5
 * Sat Oct 23 20:41:53 CDT 2021: EXITING MyClass.doSomething()
 * </pre>
 * <p>
 * The `enter` and `exit` methods place the words `ENTERING` and `EXITING`,
 * respectively, after the message timestamp, but do not have colons (:) after
 * them. For all other messaging methods, there will be a colon after the method
 * description: `DEBUG:`, `CONFIG:`, `INFO:`, `WARNING:`, `ERROR:`, and
 * `CRITICAL:`. Also, unless a newline character is placed into the raw message
 * text, the log message will be on a single line. However, if the
 * `--formatted-logs` command-line switch is passed to the application, then the
 * log messages will be wrapped at eighty (80) characters, taking into account
 * any newlines that may be in the message text. Also, when the
 * `--formatted-logs` switch is present, each message will have a header and a
 * footer, and the message body will be wrapped in such a way that no word will
 * be split over multiple lines, unless that word has a hyphen in it and the
 * part of the word to the left of the hyphen will fit on one line, while
 * leaving that part of the word to the right for the next line.</p>
 *
 * @author Sean Carrick &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class LogRecord {

    private Instant instant;
    private int level;
    private String loggerName;
    private String message;
    private Object[] parameters;
    private ResourceBundle resourceBundle;
    private String resourceBundleName;
    private long sequenceNumber;
    private final String sourceClassName;
    private String sourceMethodName;
    private int threadID;
    private Throwable thrown;

    /**
     * Constructs a new `LogRecord` object for the given class name.
     *
     * @param sourceClassName the name of the class in which this `LogRecord` is
     * being used
     */
    public LogRecord(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }

    /**
     * Constructs a new `LogRecord` object for the given class name and method
     * name.
     *
     * @param sourceClassName the name of the class in which this `LogRecord` is
     * being used
     * @param sourceMethodName the name of the method in which this `LogRecord`
     * is being used
     */
    public LogRecord(String sourceClassName, String sourceMethodName) {
        this(sourceClassName);

        this.sourceMethodName = sourceMethodName;
    }

    /**
     * Gets the instant that the event occurred.
     *
     * @return the event instant
     */
    public Instant getInstant() {
        return instant;
    }

    /**
     * Sets the instant that the event occurred.
     *
     * @param instant the event instant
     */
    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    /**
     * Get logging message level, for example `Logger.CRITICAL`.
     *
     * @return the currently set logging message level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the logging message level, for example `Logger.CRITICAL`.
     *
     * @param level the new logging message level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Gets the source `Logger`'s name.
     *
     * @return the source `Logger`'s name
     */
    public String getLoggerName() {
        return loggerName;
    }

    /**
     * Sets the source `Logger`'s name.
     *
     * @param loggerName the source `Logger`'s new name
     */
    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    /**
     * Gets the "raw" log message, before localization or formatting.
     *
     * @return the raw log message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the "raw" log message, before localization or formatting.
     *
     * @param message the raw log message &mdash; may include `String.format`
     * tokens, provided that those tokens match up to the parameters array
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the array of parameters as an `Object[]`.
     *
     * @return an `Object` array of parameters
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Sets the specified array of `Object`s as the parameters.
     *
     * @param parameters an array of `Object`s
     */
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the `ResourceBundle` associated with this `LogRecord`.
     *
     * @return the associated `ResourceBundle`
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Sets the `ResourceBundle` to associate with this `LogRecord`
     *
     * @param resourceBundle the `ResourceBundle` to associate
     */
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /**
     * Gets the name of the `ResourceBundle` associated with this `LogRecord`.
     *
     * @return the associated `ResourceBundle`'s name
     */
    public String getResourceBundleName() {
        return resourceBundleName;
    }

    /**
     * Sets the name of the `ResourceBundle` associated with this `LogRecord`.
     *
     * @param resourceBundleName the associated `ResourceBundle`'s name
     */
    public void setResourceBundleName(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
    }

    /**
     * Gets the sequence number for this `LogRecord`.
     *
     * @return the sequence number
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the sequence number for this `LogRecord`.
     *
     * @param sequenceNumber the sequence number
     */
    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Gets the name of the (alleged) source class. "Alleged" because this will
     * only have the name of the source class if it is explicitly set at
     * runtime.
     *
     * @return the source class name
     */
    public String getSourceClassName() {
        return sourceClassName;
    }

    /**
     * Gets the name of the source method for this `LogRecord`.
     *
     * @return the source method's name
     */
    public String getSourceMethodName() {
        return sourceMethodName;
    }

    /**
     * Sets the name of the source method for this `LogRecord`.
     *
     * @param sourceMethodName the source method's name
     */
    public void setSourceMethodName(String sourceMethodName) {
        this.sourceMethodName = sourceMethodName;
    }

    /**
     * Gets the ID of the thread in which this `LogRecord` is used.
     *
     * @return the thread's ID
     */
    public int getThreadID() {
        return threadID;
    }

    /**
     * Sets the ID of the thread in which this `LogRecord` is used.
     *
     * @param threadID the thread's ID
     */
    public void setThreadID(int threadID) {
        this.threadID = threadID;
    }

    /**
     * Gets the `Throwable` associated with this `LogRecord`.
     *
     * @return the associated `Throwable`
     */
    public Throwable getThrown() {
        return thrown;
    }

    /**
     * Sets the `Throwable` to be associated with this `LogRecord`.
     *
     * @param thrown the associated `Throwable`
     */
    public void setThrown(Throwable thrown) {
        this.thrown = thrown;
    }

}
