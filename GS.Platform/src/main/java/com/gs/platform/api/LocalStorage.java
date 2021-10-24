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
 *  Class      :   LocalStorage.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 8:48:30 PM
 *  Modified   :   Oct 22, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  ??? ??, 2006  Hans Muller          Initial creation.
 *  Oct 22, 2021  Sean Carrick         Updated to JDK11.
 * *****************************************************************************
 */
package com.gs.platform.api;

import com.gs.platform.utils.LogRecord;
import com.gs.platform.utils.Logger;
import java.awt.Rectangle;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Instant;

/**
 * Access to per application, per user, local file storage.
 *
 * @see ApplicationContext#getLocalStorage
 * @see SessionStorage
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class LocalStorage extends AbstractBean {

    private static final LogRecord record = new LogRecord(LocalStorage.class.getSimpleName());
    private static Logger logger = Logger.getLogger(Application.getInstance(), Logger.TRACE);
    private final ApplicationContext context;
    private long storageLimit = -1L;
    private LocalIO localIO = null;
    private final File unspecifiedFile = new File("unspecified");
    private File directory = unspecifiedFile;

    protected LocalStorage(ApplicationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("null context");
        }
        this.context = context;
    }

    /**
     * Retrieves the `ApplicationContext` singleton for which this
     * `LocalStorage` instance is being used.
     *
     * @return the current `ApplicationContext`
     */
    protected final ApplicationContext getContext() {
        return context;
    }

    private void checkFileName(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("null fileName");
        }
    }

    public InputStream openInputFile(String fileName) throws IOException {
        checkFileName(fileName);
        return getLocalIO().openInputFile(fileName);
    }

    public OutputStream openOutputFile(String fileName) throws IOException {
        checkFileName(fileName);
        return getLocalIO().openOutputFile(fileName);
    }

    public boolean deleteFile(String fileName) throws IOException {
        checkFileName(fileName);
        return getLocalIO().deleteFile(fileName);
    }

    /* If an exception occurs in the XMLEncoder/Decoder, we want
     * to throw an IOException.  The exceptionThrow listener method
     * doesn't throw a checked exception so we just set a flag
     * here and check it when the encode/decode operation finishes
     */
    private static class AbortExceptionListener implements ExceptionListener {

        public Exception exception = null;

        @Override
        public void exceptionThrown(Exception e) {
            if (exception == null) {
                exception = e;
            }
        }
    }

    private static boolean persistenceDelegatesInitialized = false;

    public void save(Object bean, final String fileName) throws IOException {
        AbortExceptionListener el = new AbortExceptionListener();
        XMLEncoder e = null;
        /* Buffer the XMLEncoder's output so that decoding errors don't
	 * cause us to trash the current version of the specified file.
         */
        ByteArrayOutputStream bst = new ByteArrayOutputStream();
        try {
            e = new XMLEncoder(bst);
            if (!persistenceDelegatesInitialized) {
                e.setPersistenceDelegate(Rectangle.class, new RectanglePD());
                persistenceDelegatesInitialized = true;
            }
            e.setExceptionListener(el);
            e.writeObject(bean);
        } finally {
            if (e != null) {
                e.close();
            }
        }
        if (el.exception != null) {
            throw new LSException("save failed \"" + fileName + "\"",
                    el.exception);
        }
        try (OutputStream ost = openOutputFile(fileName)) {
            ost.write(bst.toByteArray());
        }
    }

    public Object load(String fileName) throws IOException {
        InputStream ist;
        try {
            ist = openInputFile(fileName);
        } catch (IOException e) {
            return null;
        }
        AbortExceptionListener el = new AbortExceptionListener();
        try (XMLDecoder d = new XMLDecoder(ist)) {
            d.setExceptionListener(el);
            Object bean = d.readObject();
            if (el.exception != null) {
                throw new LSException("load failed \"" + fileName + "\"",
                        el.exception);
            }
            return bean;
        }
    }

    private void closeStream(Closeable st, String fileName) throws IOException {
        if (st != null) {
            try {
                st.close();
            } catch (java.io.IOException e) {
                throw new LSException("close failed \"" + fileName + "\"", e);
            }
        }
    }

    public long getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(long storageLimit) {
        if (storageLimit < -1L) {
            throw new IllegalArgumentException("invalid storageLimit");
        }
        long oldValue = this.storageLimit;
        this.storageLimit = storageLimit;
        firePropertyChange("storageLimit", oldValue, this.storageLimit);
    }

    private String getId(String key, String def) {
        ResourceMap appResourceMap = getContext().getResourceMap();
        String id = appResourceMap.getString(key);
        if (id == null) {
            String msg = String.format("unspecified resource {0} using {1}",
                    new Object[]{key, def});
            record.setInstant(Instant.now());
            record.setMessage(msg);
            record.setParameters(new Object[] {key, def});
            record.setSourceMethodName("getId");
            Long tid = Thread.currentThread().getId();
            record.setThreadID(tid.intValue());
            logger.warn(record);
            id = def;
        } else if (id.trim().length() == 0) {
            String msg = String.format("empty resource {0} using {1}",
                    new Object[]{key, def});
            record.setInstant(Instant.now());
            record.setMessage(msg);
            record.setParameters(new Object[] {key, def});
            record.setSourceMethodName("getId");
            Long tid = Thread.currentThread().getId();
            record.setThreadID(tid.intValue());
            logger.warn(record);
            id = def;
        }
        return id;
    }

    private String getApplicationId() {
        return getId("Application.id", getContext().getApplicationClass().getSimpleName());
    }

    private String getVendorId() {
        return getId("Application.vendorId", "UnknownApplicationVendor");
    }

    /* The following enum and method only exist to distinguish 
     * Windows and OSX for the sake of getDirectory().
     */
    private enum OSId {
        WINDOWS, OSX, UNIX
    }

    private OSId getOSId() {
        PrivilegedAction<String> doGetOSName = () -> System.getProperty("os.name");
        OSId id = OSId.UNIX;
        String osName = AccessController.doPrivileged(doGetOSName);
        if (osName != null) {
            if (osName.toLowerCase().startsWith("mac os x")) {
                id = OSId.OSX;
            } else if (osName.contains("Windows")) {
                id = OSId.WINDOWS;
            }
        }
        return id;
    }

    public File getDirectory() {
        if (directory == unspecifiedFile) {
            directory = null;
            String userHome = null;
            try {
                userHome = System.getProperty("user.home");
            } catch (SecurityException ignore) {
            }
            if (userHome != null) {
                String applicationId = getApplicationId();
                OSId osId = getOSId();
                if (null == osId) {
                    // ${userHome}/.${applicationId}/
                    String path = "." + applicationId + "/";
                    directory = new File(userHome, path);
                } else {
                    switch (osId) {
                        case WINDOWS:
                            File appDataDir = null;
                            try {
                                String appDataEV = System.getenv("APPDATA");
                                if ((appDataEV != null) && (appDataEV.length() > 0)) {
                                    appDataDir = new File(appDataEV);
                                }
                            } catch (SecurityException ignore) {
                            }
                            String vendorId = getVendorId();
                            if ((appDataDir != null) && appDataDir.isDirectory()) {
                                // ${APPDATA}\{vendorId}\${applicationId}
                                String path = vendorId + "\\" + applicationId + "\\";
                                directory = new File(appDataDir, path);
                            } else {
                                // ${userHome}\Application Data\${vendorId}\${applicationId}
                                String path = "Application Data\\" + vendorId + "\\"
                                        + applicationId + "\\";
                                directory = new File(userHome, path);
                            }
                            break;
                        case OSX: {
                            // ${userHome}/Library/Application Support/${applicationId}
                            String path = "Library/Application Support/"
                                    + applicationId + "/";
                            directory = new File(userHome, path);
                            break;
                        }
                        default: {
                            // ${userHome}/.${applicationId}/
                            String path = "." + applicationId + "/";
                            directory = new File(userHome, path);
                            break;
                        }
                    }
                }
            }
        }
        return directory;
    }

    public void setDirectory(File directory) {
        File oldValue = this.directory;
        this.directory = directory;
        firePropertyChange("directory", oldValue, this.directory);
    }

    /* Papers over the fact that the String,Throwable IOException 
     * constructor was only introduced in Java 6.
     */
    private static class LSException extends IOException {

        public LSException(String s, Throwable e) {
            super(s);
            initCause(e);
        }

        public LSException(String s) {
            super(s);
        }
    }

    /* There are some (old) Java classes that aren't proper beans.  Rectangle
     * is one of these.  When running within the secure sandbox, writing a 
     * Rectangle with XMLEncoder causes a security exception because 
     * DefaultPersistenceDelegate calls Field.setAccessible(true) to gain
     * access to private fields.  This is a workaround for that problem.
     * A bug has been filed, see JDK bug ID 4741757  
     */
    private static class RectanglePD extends DefaultPersistenceDelegate {

        public RectanglePD() {
            super(new String[]{"x", "y", "width", "height"});
        }

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            Rectangle oldR = (Rectangle) oldInstance;
            Object[] constructorArgs = new Object[]{
                oldR.x, oldR.y, oldR.width, oldR.height
            };
            return new Expression(oldInstance, oldInstance.getClass(), "new",
                    constructorArgs);
        }
    }

    private synchronized LocalIO getLocalIO() {
        if (localIO == null) {
            localIO = new LocalFileIO();
        }
        return localIO;
    }

    private abstract class LocalIO {

        public abstract InputStream openInputFile(String fileName)
                throws IOException;

        public abstract OutputStream openOutputFile(String fileName)
                throws IOException;

        public abstract boolean deleteFile(String fileName) throws IOException;
    }

    private class LocalFileIO extends LocalIO {

        @Override
        public InputStream openInputFile(String fileName) throws IOException {
            File path = new File(getDirectory(), fileName);
            try {
                return new BufferedInputStream(new FileInputStream(path));
            } catch (IOException e) {
                throw new LSException("couldn't open input file \"" + fileName
                        + "\"", e);
            }
        }

        @Override
        public OutputStream openOutputFile(String fileName) throws IOException {
            File dir = getDirectory();
            if (!dir.isDirectory()) {
                if (!dir.mkdirs()) {
                    throw new LSException("couldn't create directory " + dir);
                }
            }
            File path = new File(dir, fileName);
            try {
                return new BufferedOutputStream(new FileOutputStream(path));
            } catch (IOException e) {
                throw new LSException("couldn't open output file \"" + fileName
                        + "\"", e);
            }
        }

        @Override
        public boolean deleteFile(String fileName) throws IOException {
            File path = new File(getDirectory(), fileName);
            return path.delete();
        }
    }

}
