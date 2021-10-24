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
 *  Class      :   Application.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 9:06:34 PM
 *  Modified   :   Oct 22, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  ??? ??, 2006  Hans Muller          Initial creation.
 *  Oct 22, 2021  Sean Carrick         UPdated to JDK11.
 * *****************************************************************************
 */
package com.gs.platform.api;

import com.gs.platform.utils.LogRecord;
import com.gs.platform.utils.Logger;
import com.gs.platform.utils.Properties;
import com.gs.platform.utils.TerminalErrorPrinter;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.PaintEvent;
import java.beans.Beans;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The base class for Swing applications.
 *
 * <p>
 * This class defines a simple lifecyle for Swing applications: `initialize`,
 * `startup`, `ready`, and `shutdown`. The `Application`'s `startup` method is
 * responsible for creating the initial GUI and making it visible, and
 * `@Action`'s shutdown} method for hiding the GUI and performing any other
 * cleanup actions before the application exits. The `initialize` method can be
 * used configure system properties that must be setSystemProperty before the
 * GUI is constructed and the `ready` method is for applications that want to do
 * a little bit of extra work once the GUI is "ready" to use. Concrete
 * subclasses must override the `startup` method.
 * <p>
 * Applications are started with the static `launch` method. Applications use
 * the `ApplicationContext` {@link
 * Application#getContext singleton} to find resources, actions, local storage,
 * and so on.
 * <p>
 * All `Application` subclasses must override `startup` and they should call
 * {@link #exit} (which calls `shutdown`) to exit. Here's an example of a
 * complete "Hello World" Application:
 * <pre>
 * public class MyApplication extends Application {
 *     JFrame mainFrame = null;
 *     &#064;Override protected void startup() {
 *         mainFrame = new JFrame("Hello World");
 *         mainFrame.add(new JLabel("Hello World"));
 *         mainFrame.addWindowListener(new MainFrameListener());
 *         mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 *         mainFrame.pack();
 *         mainFrame.setVisible(true);
 *     }
 *     &#064;Override protected void shutdown() {
 *         mainFrame.setVisible(false);
 *     }
 *     private class MainFrameListener extends WindowAdapter {
 *         public void windowClosing(WindowEvent e) {
 *            exit();
 *         }
 *     }
 *     public static void main(String[] args) {
 *         Application.launch(MyApplication.class, args);
 *     }
 * }
 * </pre>
 * <p>
 * The `mainFrame`'s `defaultCloseOperation} is setSystemProperty to
 * `DO_NOTHING_ON_CLOSE} because we're handling attempts to close the window by
 * calling `ApplicationContext` {@link #exit}.
 * <p>
 * Platform applications like the example can be defined more easily with the {@link com.gs.platform.PlatformApplication
 * PlatformApplication} `Application` subclass.
 *
 * <p>
 * All of the Application's methods are called (must be called) on the EDT.
 *
 * <p>
 * All but the most trivial applications should define a ResourceBundle in the
 * resources subpackage with the same name as the application class (like `
 * resources/MyApplication.properties}). This ResourceBundle contains resources
 * shared by the entire application and should begin with the following the
 * standard Application resources:
 * <pre>
 * Application.name = A short name, typically just a few words
 * Application.id = Suitable for Application specific identifiers, like file names
 * Application.title = A title suitable for dialogs and frames
 * Application.version = A version string that can be incorporated into messages
 * Application.vendor = A proper name, like Sun Microsystems, Inc.
 * Application.vendorId = suitable for Application-vendor specific identifiers, like file names.
 * Application.homepage = A URL like http://www.javadesktop.org
 * Application.description =  One brief sentence
 * Application.lookAndFeel = either system, default, or a LookAndFeel class name
 * </pre>
 * <p>
 * The `Application.lookAndFeel` resource is used to initialize the `UIManager
 * lookAndFeel` as follows:
 * <ul>
 * <li>`system` - the system (native) look and feel</li>
 * <li>`default` - use the JVM default, typically the cross platform look and
 * feel</li>
 * <li>a LookAndFeel class name - use the specified class
 * </ul>
 *
 * @see com.gs.platform.PlatformApplication
 * @see ApplicationContext
 * @see UIManager#setLookAndFeel
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
@ProxyActions({"cut", "copy", "paste", "delete"})

public abstract class Application extends AbstractBean {

    private static Application application = null;
    private static final LogRecord record = new LogRecord(Application.class.getSimpleName());
    private static final Logger logger = Logger.getLogger(Application.getInstance(), Logger.TRACE);
    private final List<ExitListener> exitListeners;
    private final List<String> installedModules;
    private final ApplicationContext context;
    private final Properties props;
    private JFrame mainFrame;

    /**
     * Not to be called directly, see {@link #launch launch}.
     * <p>
     * Subclasses can provide a no-args constructor to initialize private final
     * state however GUI initialization, and anything else that might refer to
     * public API, should be done in the {@link #startup startup} method.
     */
    protected Application() {
        exitListeners = new CopyOnWriteArrayList<>();
        installedModules = new ArrayList<>();
        context = new ApplicationContext();
        props = new Properties(getContext());
    }

    /**
     * Creates an instance of the specified `Application` subclass, sets the
     * `ApplicationContext` ` `application` property, and then calls the new `
     * `Application`'s `startup` method.The `launch` method is typically called
     * from the Application's `main`
     * <pre>
     *     public static void main(String[] args) {
     *         Application.launch(MyApplication.class, args);
     *     }
     * </pre> The `applicationClass} constructor and `startup` methods run on
     * the event dispatching thread.
     *
     * @param <T>
     * @param applicationClass the `Application` class to launch
     * @param args `main` method arguments
     * @see #shutdown
     * @see ApplicationContext#getApplication
     */
    public static synchronized <T extends Application> void launch(
            final Class<T> applicationClass, final String[] args) {
        Runnable doCreateAndShowGUI = () -> {
            try {
                application = create(applicationClass);
                application.initialize(args);
                application.startup();
                application.waitForReady();
            } catch (Exception e) {
                String msg = String.format("Application %s failed to launch",
                        applicationClass);
                record.setInstant(Instant.now());
                record.setSourceMethodName("launch");
                record.setParameters(new Object[]{applicationClass, args});
                Long tID = Thread.currentThread().getId();
                record.setThreadID(tID.intValue());
                record.setThrown(e);
                record.setMessage(msg);
                record.setSequenceNumber(1l);

                logger.critical(record);
                TerminalErrorPrinter.print(e, msg);
                throw (new Error(msg, e));
            }
        };
        SwingUtilities.invokeLater(doCreateAndShowGUI);
    }

    /* Initializes the ApplicationContext applicationClass and application
     * properties.  
     * 
     * Note that, as of Java SE 5, referring to a class literal
     * doesn't force the class to be loaded.  More info:
     * http://java.sun.com/javase/technologies/compatibility.jsp#literal
     * It's important to perform these initializations early, so that
     * Application static blocks/initializers happen afterwards.
     */
    static <T extends Application> T create(Class<T> applicationClass)
            throws Exception {

        if (!Beans.isDesignTime()) {
            /* A common mistake for privileged applications that make
             * network requests (and aren't applets or web started) is to
             * not configure the http.proxyHost/Port system properties.
             * We paper over that issue here.
             */
            try {
                System.setProperty("java.net.useSystemProxies", "true");
            } catch (SecurityException ignoreException) {
                // Unsigned apps can't setSystemProperty this property. 
            }
        }

        /* Construct the Application object.  The following
         * complications, relative to just calling
         * applicationClass.newInstance(), allow a privileged app to
         * have a private static inner Application subclass.
         */
        Constructor<T> ctor = applicationClass.getDeclaredConstructor();
        if (!ctor.canAccess(ctor)) {
            try {
                ctor.setAccessible(true);
            } catch (SecurityException ignore) {
                // ctor.newInstance() will throw an IllegalAccessException
            }
        }
        T application = ctor.newInstance();

        /* Initialize the ApplicationContext application properties
         */
        ApplicationContext ctx = application.getContext();
        ctx.setApplicationClass(applicationClass);
        ctx.setApplication(application);

        /* Load the application resource map, notably the 
	 * Application.* properties.
         */
        ResourceMap appResourceMap = ctx.getResourceMap();

        appResourceMap.putResource("platform", platform());

        if (!Beans.isDesignTime()) {
            /* Initialize the UIManager lookAndFeel property with the
             * Application.lookAndFeel resource.  If the the resource
             * isn't defined we default to "system".
             */
            String key = "Application.lookAndFeel";
            String lnfResource = appResourceMap.getString(key);
            String lnf = (lnfResource == null) ? "system" : lnfResource;
            try {
                if (lnf.equalsIgnoreCase("system")) {
                    String name = UIManager.getSystemLookAndFeelClassName();
                    UIManager.setLookAndFeel(name);
                } else if (!lnf.equalsIgnoreCase("default")) {
                    UIManager.setLookAndFeel(lnf);
                }
            } catch (ClassNotFoundException | IllegalAccessException
                    | InstantiationException | UnsupportedLookAndFeelException e) {
                String s = "Couldn't set LookandFeel " + key + " = \""
                        + lnfResource + "\"";
                record.setInstant(Instant.now());
                record.setSourceMethodName("launch");
                record.setParameters(new Object[]{applicationClass});
                Long tID = Thread.currentThread().getId();
                record.setThreadID(tID.intValue());
                record.setThrown(e);
                record.setMessage(s);
                record.setSequenceNumber(1l);

                logger.warn(record);
                TerminalErrorPrinter.print(e, s);
            }
        }

        return application;
    }

    /* Defines the default value for the platform resource, 
     * either "osx" or "default".
     */
    private static String platform() {
        String platform = "default";
        try {
            String osName = System.getProperty("os.name");
            if ((osName != null) && osName.toLowerCase().startsWith("mac os x")) {
                platform = "osx";
            }
        } catch (SecurityException ignore) {
        }
        return platform;
    }

    /* Call the ready method when the eventQ is quiet.
     */
    void waitForReady() {
        new DoWaitForEmptyEventQ().execute();
    }

    /**
     * Responsible for initializations that must occur before the GUI is
     * constructed by `startup`.
     * <p>
     * This method is called by the static `launch` method, before `startup` is
     * called. Subclasses that want to do any initialization work before
     * `startup` must override it. The `initialize` method runs on the event
     * dispatching thread.
     * <p>
     * By default initialize() does nothing.
     *
     * @param args the main method's arguments.
     * @see #launch
     * @see #startup
     * @see #shutdown
     */
    protected void initialize(String[] args) {
    }

    /**
     * Responsible for starting the application; for creating and showing the
     * initial GUI.
     * <p>
     * This method is called by the static `launch` method, subclasses must
     * override it. It runs on the event dispatching thread.
     *
     * @see #launch
     * @see #initialize
     * @see #shutdown
     */
    protected abstract void startup();

    /**
     * Called after the startup() method has returned and there are no more
     * events on the {@link Toolkit#getSystemEventQueue system event queue}.
     * When this method is called, the application's GUI is ready to use.
     * <p>
     * It's usually important for an application to start up as quickly as
     * possible. Applications can override this method to do some additional
     * start up work, after the GUI is up and ready to use.
     *
     * @see #launch
     * @see #startup
     * @see #shutdown
     */
    protected void ready() {
    }

    /**
     * Called when the application {@link #exit exits}. Subclasses may override
     * this method to do any cleanup tasks that are neccessary before exiting.
     * Obviously, you'll want to try and do as little as possible at this point.
     * This method runs on the event dispatching thread.
     *
     * @see #startup
     * @see #ready
     * @see #exit
     * @see #addExitListener
     */
    protected void shutdown() {
        // TBD should call TaskService#shutdownNow() on each TaskService
    }

    /* An event that sets a flag when it's dispatched and another
     * flag, see isEventQEmpty(), that indicates if the event queue
     * was empty at dispatch time.
     */
    private static class NotifyingEvent extends PaintEvent implements ActiveEvent {

        private boolean dispatched = false;
        private boolean qEmpty = false;

        NotifyingEvent(Component c) {
            super(c, PaintEvent.UPDATE, null);
        }

        synchronized boolean isDispatched() {
            return dispatched;
        }

        synchronized boolean isEventQEmpty() {
            return qEmpty;
        }

        @Override
        public void dispatch() {
            EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
            synchronized (this) {
                qEmpty = (q.peekEvent() == null);
                dispatched = true;
                notifyAll();
            }
        }
    }

    /* Keep queuing up NotifyingEvents until the event queue is
     * empty when the NotifyingEvent is dispatched().
     */
    private void waitForEmptyEventQ() {
        boolean qEmpty = false;
        JPanel placeHolder = new JPanel();
        EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
        while (!qEmpty) {
            NotifyingEvent e = new NotifyingEvent(placeHolder);
            q.postEvent(e);
            synchronized (e) {
                while (!e.isDispatched()) {
                    try {
                        e.wait();
                    } catch (InterruptedException ie) {
                    }
                }
                qEmpty = e.isEventQEmpty();
            }
        }
    }

    /* When the event queue is empty, give the app a chance to do
     * something, now that the GUI is "ready".
     */
    private class DoWaitForEmptyEventQ extends Task<Void, Void> {

        DoWaitForEmptyEventQ() {
            super(Application.this);
        }

        @Override
        protected Void doInBackground() {
            waitForEmptyEventQ();
            return null;
        }

        @Override
        protected void finished() {
            ready();
        }
    }

    /**
     * Gracefully shutdown the application, calls `exit(null)` This version of
     * `exit()` is convenient if the decision to exit the application wasn't
     * triggered by an event.
     *
     * @see #exit(EventObject)
     */
    public final void exit() {
        exit(null);
    }

    /**
     * Gracefully shutdown the application.
     * <p>
     * If none of the `ExitListener.canExit()` methods return false, calls the
     * `ExitListener.willExit()` methods, then `shutdown()`, and then exits the
     * Application with {@link #end end}. Exceptions thrown while running
     * willExit() or shutdown() are logged but otherwise ignored.
     * <p>
     * If the caller is responding to an GUI event, it's helpful to pass the
     * event along so that ExitListeners' canExit methods that want to popup a
     * dialog know on which screen to show the dialog. For example:
     * <pre>
     * class ConfirmExit implements Application.ExitListener {
     *     public boolean canExit(EventObject e) {
     *         Object source = (e != null) ? e.getSource() : null;
     *         Component owner = (source instanceof Component) ? (Component)source : null;
     *         int option = JOptionPane.showConfirmDialog(owner, "Really Exit?");
     *         return option == JOptionPane.YES_OPTION;
     *     }
     *     public void willExit(EventObejct e) {}
     * }
     * myApplication.addExitListener(new ConfirmExit());
     * </pre> The `eventObject} argument may be null, e.g. if the exit call was
     * triggered by non-GUI code, and `canExit`, ` willExit` methods must guard
     * against the possibility that the `eventObject} argument's `source} is not
     * a ` Component}.
     *
     * @param event the EventObject that triggered this call or null
     * @see #addExitListener
     * @see #removeExitListener
     * @see #shutdown
     * @see #end
     */
    public void exit(EventObject event) {
        for (ExitListener listener : exitListeners) {
            if (!listener.canExit(event)) {
                return;
            }
        }
        try {
            exitListeners.forEach(listener -> {
                try {
                    listener.willExit(event);
                } catch (Exception e) {
                    record.setInstant(Instant.now());
                    record.setSourceMethodName("launch");
                    record.setParameters(new Object[]{event});
                    Long tID = Thread.currentThread().getId();
                    record.setThreadID(tID.intValue());
                    record.setThrown(e);
                    record.setMessage("Notifying windows application willExit");
                    record.setSequenceNumber(1l);

                    logger.critical(record);
                    TerminalErrorPrinter.print(e, "Notifying windows application"
                            + " willExit");
                }
            });
            shutdown();
        } catch (Exception e) {
            record.setInstant(Instant.now());
            record.setSourceMethodName("launch");
            record.setParameters(new Object[]{event});
            Long tID = Thread.currentThread().getId();
            record.setThreadID(tID.intValue());
            record.setThrown(e);
            record.setMessage("Attempting to exit");
            record.setSequenceNumber(1l);

            logger.critical(record);
            TerminalErrorPrinter.print(e, "Attempting to exit");
        } finally {
            end();
        }
    }

    /**
     * Called by {@link #exit exit} to terminate the application. Calls
     * `Runtime.getRuntime().exit(0)}, which halts the JVM.
     *
     * @see #exit
     */
    protected void end() {
        Runtime.getRuntime().exit(0);
    }

    /**
     * Give the Application a chance to veto an attempt to exit/quit. An
     * `ExitListener`'s `canExit` method should return false if there are
     * pending decisions that the user must makebefore the app exits. A typical
     * `ExitListener} would prompt the user with a modal dialog.
     * <p>
     * The `eventObject} argument will be the the value passed to
     * {@link #exit(EventObject) exit()}. It may be null.
     * <p>
     * The `willExit` method is called after the exit has been confirmed. An
     * ExitListener that's going to perform some cleanup work should do so in
     * `willExit`.
     * <p>
     * `ExitListener}s run on the event dispatching thread.
     *
     * @see #exit(EventObject)
     * @see #addExitListener
     * @see #removeExitListener
     */
    public interface ExitListener extends EventListener {

        boolean canExit(EventObject event);

        void willExit(EventObject event);
    }

    /**
     * Add an `ExitListener} to the list.
     *
     * @param listener the `ExitListener}
     * @see #removeExitListener
     * @see #getExitListeners
     */
    public void addExitListener(ExitListener listener) {
        exitListeners.add(listener);
    }

    /**
     * Remove an `ExitListener} from the list.
     *
     * @param listener the `ExitListener}
     * @see #addExitListener
     * @see #getExitListeners
     */
    public void removeExitListener(ExitListener listener) {
        exitListeners.remove(listener);
    }

    /**
     * All of the `ExitListener}s added so far.
     *
     * @return all of the `ExitListener}s added so far.
     */
    public ExitListener[] getExitListeners() {
        int size = exitListeners.size();
        return exitListeners.toArray(new ExitListener[size]);
    }

    /**
     * The default `Action` for quitting an application, `quit} just exits the
     * application by calling `exit(e)}.
     *
     * @param e the triggering event
     * @see #exit(EventObject)
     */
    @Action
    public void quit(ActionEvent e) {
        exit(e);
    }

    /**
     * The ApplicationContext singleton for this Application.
     *
     * @return the Application's ApplicationContext singleton
     */
    public final ApplicationContext getContext() {
        return context;
    }

    /**
     * The `Application` singleton.
     * <p>
     * Typically this method is only called after an Application has been
     * launched however in some situations, like tests, it's useful to be able
     * to getProperty an `Application` object without actually launching. In
     * that case, an instance of the specified class is constructed and
     * configured as it would be by the {@link #launch launch} method. However
     * it's `initialize` and `startup` methods are not run.
     *
     * @param <T>
     * @param applicationClass this `Application`'s subclass
     * @return the launched Application singleton.
     * @see Application#launch
     */
    public static synchronized <T extends Application> T getInstance(
            Class<T> applicationClass) {
        if (application == null) {
            /* Special case: the application hasn't been launched.  We're
             * constructing the applicationClass here to getProperty the same effect
             * as the NoApplication class serves for getInstance().  We're
             * not launching the app, no initialize/startup/wait steps.
             */
            try {
                application = create(applicationClass);
            } catch (Exception e) {
                String msg = String.format("Couldn't construct %s",
                        applicationClass);
                throw (new Error(msg, e));
            }
        }
        return applicationClass.cast(application);
    }

    /**
     * The `Application` singleton, or a placeholder if ` launch} hasn't been
     * called yet.
     * <p>
     * Typically this method is only called after an Application has been
     * launched however in some situations, like tests, it's useful to be able
     * to getProperty an `Application` object without actually launching. The
     * <em>placeholder</em> Application object provides access to an
     * `ApplicationContext` singleton and has the same semantics as launching an
     * Application defined like this:
     * <pre>
     * public class PlaceholderApplication extends Application {
     *     public void startup() { }
     * }
     * Application.launch(PlaceholderApplication.class);
     * </pre>
     *
     * @return the `Application` singleton or a placeholder
     * @see Application#launch
     * @see Application#getInstance(Class)
     */
    public static synchronized Application getInstance() {
        if (application == null) {
            application = new NoApplication();
        }
        return application;
    }

    private static class NoApplication extends Application {

        protected NoApplication() {
            ApplicationContext ctx = getContext();
            ctx.setApplicationClass(getClass());
            ctx.setApplication(this);
            ResourceMap appResourceMap = ctx.getResourceMap();
            appResourceMap.putResource("platform", platform());
        }

        @Override
        protected void startup() {
        }
    }

    /**
     * Sets the `mainFrame` for the `Application`.
     *
     * @param mainFrame the window to use as the `Application`'s `mainFrame`
     */
    public void setMainFrame(JFrame mainFrame) {
        if (mainFrame == null || !(mainFrame instanceof JFrame)) {
            throw new IllegalArgumentException("mainFrame is either null or not "
                    + "an instance of javax.swing.JFrame");
        }

        this.mainFrame = mainFrame;
    }

    /**
     * Retrieves the `mainFrame` for the `Application`.
     *
     * @return the window used as the `Application`'s `mainFrame`
     */
    public JFrame getMainFrame() {
        return (mainFrame == null) ? null : mainFrame;
    }

    public void show(JFrame window) {
        getContext().getResourceMap().injectComponents(window);

        try {
            getContext().getSessionStorage().restore(window,
                    window.getClass().getSimpleName());
        } catch (IOException ex) {
            String msg = String.format("Unable to restore session state for %s "
                    + "from the %s file", window,
                    window.getClass().getSimpleName());
            TerminalErrorPrinter.print(ex, msg);
            record.setInstant(Instant.now());
            record.setSourceMethodName("launch");
            record.setParameters(new Object[]{window});
            Long tID = Thread.currentThread().getId();
            record.setThreadID(tID.intValue());
            record.setThrown(ex);
            record.setMessage(msg);
            record.setSequenceNumber(1l);

            logger.warn(record);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public String getProperty(String propertyName) {
        return props.getProperty(propertyName);
    }

    public String getProperty(String propertyName, String defaultValue) {
        return props.getProperty(propertyName, defaultValue);
    }

    public void setProperty(String propertyName, String value) {
        props.setSystemProperty(propertyName, value);
    }

    public void registerModule(String moduleName) {
        installedModules.add(moduleName);
    }

    public String[] getInstalledModules() {
        return installedModules.toArray(new String[installedModules.size()]);
    }

}
