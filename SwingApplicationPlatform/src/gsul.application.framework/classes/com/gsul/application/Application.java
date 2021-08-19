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
 *  Class      :   Application.java
 *  Author     :   Sean Carrick
 *  Created    :   Aug 18, 2021 @ 9:13:03 PM
 *  Modified   :   Aug 18, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  Aug 18, 2021  Sean Carrick         Initial creation.
 * *****************************************************************************
 */
package com.gsul.application;

/**
 * The base class for Swing applications.
 * <p>
 * This class defines a simple lifecycle for Swing applications: {@code initialize},
 * {@code startup}, {@code ready}, and {@code shutdown}. The {@code Application}'s
 * {@code startup} method is responsible for creating the inital GUI and making
 * it visible, and the {@code shutdown} method for hiding the GUI and performing
 * any other cleanup actions before the application exits. The {@code initialize}
 * method can be used to configure the system properties that must be set before
 * the GUI is constructed and the {@code ready} method is for applications that
 * want to do a little bit of extra work once the GUI is "ready" to use. Concrete
 * subclasses <strong><em>must</em></strong> override the {@code startup} method.
 * </p><p>
 * Applications are started with the static {@code launch} method. Applications
 * use the {@code ApplicationContext} {@link Application#getContext singleton} to
 * find resources, actions, local storage, and so on.</p>
 * <p>
 * All {@code Application} subclasses must override {@code startup} and they 
 * should call {@link #exit} (which calls {@code shutdown}) to exit. Here is an
 * example of a complete "Hello World" Application:</p>
 * <pre>
 * public class MyApplication extends Application {
 * 
 *     JFrame mainFrame = null;
 * 
 *     &#064;Override
 *     protected void startup() {
 *         mainFrame = new JFrame("Hello World");
 *         mainFrame.add(new JLabel("Hello World"));
 *         mainFrame.addWindowListener(new MainFrameListener());
 *         mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
 *         mainFrame.pack();
 *         mainFrame.setVisible(true);
 *     }
 * 
 *     &#064;Override
 *     protected void shutdown() {
 *         mainFrame.setVisible(false);
 *     }
 * 
 *     private class MainFrameListener extends WindowAdapter {
 *     
 *         public void windowClosing(WindowEvent e) {
 *             exit();
 *         }
 *     }
 * 
 *     public static void main(String[] args) {
 *         Application.launch(MyApplication.class, args);
 *     }
 * 
 * }
 * </pre><p>
 * The {@code mainFrame}'s {@code defaultCloseOperation} is set to {@code 
 * DO_NOTHING_ON_CLOSE} because we are handling attempts to close the window by
 * calling {@code ApplicationContext} {@link #exit}.</p>
 * <p>
 * Simple single frame applications like the example can be defined more easily
 * with the {@link SingleFrameApplication SingleFrameApplication} {@code 
 * Application} subclass.</p>
 * <p>
 * All of the Application's methods are called (must be called) on the Event
 * Dispatching Thread (EDT).</p>
 * <p>
 * All but the most trivial applications should define a ResourceBundle in the
 * same package as the class, or another well known location, with the same name
 * as the application class (like {@code MyApplication.properties} or {@code 
 * resources/MyApplication.class}). This ResourceBundle contains resources shared
 * by the entire application and should begin with the following standard 
 * Application resources:</p>
 * <pre>
 * Application.name=A short name, typically just a few words
 * Application.id=Suitable for Application specific identifiers, like file names
 * Application.title=A title sutiable for dialogs and frames
 * Application.version=A version string that can be incorporated into messages
 * Application.vendor=A proper name, like GS United Labs
 * Application.vendorId=Suitable for Application-vendor specific identifiers, like file names
 * Application.homepage=a URL like https://gs-unitedlabs.com
 * Application.description=One brief sentence
 * Application.lookAndFeel=Either system, default, or LookAndFeel class name
 * </pre><p>
 * The {@code Application.lookAndFeel} resource is used to initialize the {@code 
 * UIManager lookAndFeel} as follows:</p>
 * <ul>
 * <li>{@code system} - the system (native) look and feel</li>
 * <li>{@code default} - use the JVM default, typically the cross platform look
 * and feel</li>
 * <li>a LookAndFeel class name - use the specified look and feel</li></ul>
 *
 * <dl>
 * <dt><strong><em>Copyright Notice</em></strong></dt>
 * <dd>This class has been modified with the permission of the original author, 
 * Hans Muller. This class was originally written for the JSR-296 Swing 
 * Application Framework, using JDK6 in 2006, at Sun Microsystems.<br><br>This
 * work is an adaptation and update of that original work.</dd></dl>
 * 
 * @see SingleFrameApplication
 * @see ApplicationContext
 * @see UIManager#setLookAndFeel(java.lang.String)
 *
 * @author Hans Muller &ndash; Original JSR-296 Framework author
 * @author Sean Carrick  &ndash; Modular Java Swing Application Platform author &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class Application extends AbstractBean {

    /**
     * Not to be called directly, see {@link #launch launch}.
     * <p>
     * Subclasses can provide a no-args constructor to initialize private final
     * stat however GUI initialization, and anything else that might refer to
     * public API, should be done in the {@link #startup startup} method.</p>
     */
    protected Application () {

    }

}
