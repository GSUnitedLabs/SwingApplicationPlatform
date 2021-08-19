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
 *  Class      :   Task.java
 *  Author     :   Sean Carrick
 *  Created    :   Aug 19, 2021 @ 8:39:47 AM
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
package com.gsul.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;

/**
 * A type of {@link javax.swing.SwingWorker} that represents an application
 * background task. Tasks add descriptive properties that can be shown to the 
 * user, a new set of methods for customizing task completion, support for 
 * blocking input to the GUI while the Task is executing, and a {@code 
 * TaskListener} that enables one to monitor the three key SwingWorker methods:
 * {@code doInBackground}, {@code process}, and {@code done}.
 * <p>
 * When a Task completes, the {@code final done} method invokes one of {@code 
 * succeeded}, {@code cancelled}, {@code interrupted}, or {@code failed}. The
 * {@code final done} method invokes {@code finished} when the completion method
 * returns or throws an exception.</p>
 * <p>
 * Tasks should provide localized values for the {@code title}, {@code 
 * description}, and {@code message} properties in a ResourceBundle for the Task
 * subclass. A {@link ResourceMap} is loaded automatically using the Task 
 * subclass as the {@code startClass} and the {@code Task.class} as the {@code 
 * stopClass}. This ResourceMap is also used to look up format strings used in 
 * calls to {@link #message message}, which is used to set the {@code message}
 * property.</p>
 * <p>
 * For example: given a Task called {@code MyTask} defined like this:</p>
 * <pre>
 * class MyTask extends Task&lt;MyResultType, Void&gt; {
 *     protected MyResultType doInBackground() {
 *         message("startMessage", getPlannedSubtaskCount());
 *         // do the work ... if an error is encountered:
 *             message("errorMessage");
 *         message("finishedMessage", getActualSubtaskCount(), getFailureCount());
 *         // return the result
 *     }
 * }
 * </pre><p>
 * Typically, the resources for this class would be defined in the MyTask
 * ResourceBundle, {@code resources/MyTask.properties}:</p>
 * <pre>
 * title=My Task
 * description=A task of mine for my own purposes
 * startMessage=Starting: working on %s subtasks...
 * errorMessage=An unexpected error occurred, skipping subtask
 * finishedMessage=Finished: completed %1$s subtasks, %2$s failures
 * </pre><p>
 * Task subclasses can override resource values in their own ResourceBundles:</p>
 * <pre>
 * class MyTaskSubclass extends MyTask {
 * }
 * 
 * # resources/MyTaskSubclass.properties:
 * title=My Task Subclass
 * description=An appropriate description
 * # ... all other resources inherited from superclass
 * </pre><p>
 * Tasks can specify that input to the GUI is to be blocked while they are being
 * executed. The {@code inputBlocker} property specifies what part of the GUI is
 * to be blocked and how that is accomplished. The {@code inputBlocker} is set
 * automatically when an {@code &#064;Action} method that returns a Task
 * specifies a {@link BlockingScope} value for the {@code block} annotation 
 * parameter. To customize the way blocking is implemented you can define your
 * own {@code Task.inputBlocker}. For example, assume that {@code busyGlassPane}
 * is a component that consumes (and ignores) keyboard and mouse input:</p>
 * <pre>
 * class MyInputBlocker extends InputBlocker {
 *     MyInputBlocker(Task task) {
 *         super(task, Task.BlockingScope.WINDOW, busyGlassPane);
 *     }
 * 
 *     &#064;Override
 *     protected void block() {
 *         myFrame.setGlassPane(busyGlassPane);
 *         busyGlassPane.setVisible(true);
 *     }
 * 
 *     &#064;Override
 *     protected void unblock() {
 *         busyGlassPane.setVisible(false);
 *     }
 * }
 * // ...
 * myTask.setInputBlocker(new MyInputBlocker(myTask));
 * </pre><p>
 * All of the settable properties in this class are bound, i.e. a
 * PropertyChangeEvent is fired when the value of the property changes. As with
 * the {@code SwingWorker} superclass, all {@code PropertyChangeListener}s run
 * on the Event Dispatching Thread (EDT). This is also true of {@code 
 * TaskListener}s.</p>
 * <p>
 * Unless specified otherwise, this class is thread-safe. All of the Task
 * properties can be get/set on any thread.</p>
 * 
 * <dl>
 * <dt><strong><em>Copyright Notice</em></strong></dt>
 * <dd>This class has been modified with the permission of the original author, 
 * Hans Muller. This class was originally written for the JSR-296 Swing 
 * Application Framework, using JDK6 in 2006, at Sun Microsystems.<br><br>This
 * work is an adaptation and update of that original work.</dd></dl>
 * 
 * @see ApplicationContext
 * @see ResourceMap
 * @see TaskListener
 * @see TaskEvent
 *
 * @author Hans Muller &ndash; Original JSR-296 Framework author
 * @author Sean Carrick  &ndash; Modular Java Swing Application Platform author &lt;sean at gs-unitedlabs dot com&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class Task<T, V> extends SwingWorker<T, V> {
    
    /**
     * Specifies to what extend the GUI should be blocked when a task is 
     * executed by a TaskService. Input blocking is carried out by the Task's
     * {@link #getInputBlocker inputBlocker}.
     * 
     * @see Task.InputBlocker
     * @see Action#block() 
     */
    public enum BlockingScope {
        /**
         * Do not block the GUI while this Task is executing.
         */
        NONE,
        /**
         * Block an {@link ApplicationAction Action} while the task is executing,
         * typically by temporarily disabling it.
         */
        ACTION,
        /**
         * Block a component while the task is executing, typically by 
         * temporarily disabling it.
         */
        COMPONENT,
        /**
         * Block a top-level window while the task is executing, typically by
         * showing a window-modal dialog.
         */
        WINDOW,
        /**
         * Block all of the application's top-level windows, typically by 
         * showing an application-modal dialog.
         */
        APPLICATION
    }

    /**
     * Construct a {@code Task} with an empty ({@code ""}) resource name prefix,
     * whose ResourceMap is the value of {@code 
     * ApplicationContenxt.getInstance().getResourceMap(this.getClass(), Task.class)}.
     * For example:
     * <pre>
     * Task task = new Task(getApplication().getContext().getInstance().getResoruceMap(this.getClass(), Task.class);
     * // ...
     * </pre>
     * 
     * @param application the application to which this {@code Task} belongs
     */
    public Task (Application application) {

    }

    @Override
    protected T doInBackground() throws Exception {
        return null;
    }
    
    private class StatePCL implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }
        
    }
    
    /**
     * Specifies to what extend input to the Application's GUI should be blocked
     * while this Task is being executed and provides a pair of methods, {@code 
     * block} and {@code unblock} that do the work of blocking the GUI. For the
     * sake of input blocking, a Task begins executing when it is {@link 
     * TaskService#execute submitted} to a {@code TaskService}, and it finishes
     * executing after the Task's completion methods have been called
     * <p>
     * The InputBlocker's {@link Task.BlockingScope BlockingScope} and the 
     * blocking {@code target} object define what part of the GUI's input will 
     * be blocked:</p>
     * <dl><dt><strong>{@code Task.BlockingScope.NONE}</strong></dt>
     * <dd>Do not block input. The blocking target is ignored in this case.</dd>
     * <dt><strong>{@code Task.BlockingScope.ACTION}</strong></dt>
     * <dd>Disable the target {@link javax.swing.Action Action} while the Task
     * is executing.</dd>
     * <dt><strong>{@code Task.BlockingScope.COMPONENT}</strong></dt>
     * <dd>Disable the target {@link java.awt.Component Component} while the 
     * Task is executing.</dd>
     * <dt><strong>{@code Task.BlockingScope.WINDOW}</strong></dt>
     * <dd>Block the Window ancestor of the target Component while the Task is
     * executing.</dd>
     * <dt><strong>{@code Task.BlockingScope.APPCLICATION}</strong></dt>
     * <dd>Block the entire Application while the Task is executing. The blocking
     * target is ignored in this case.</dd></dl>
     * <p>
     * Input blocking begins when the {@code block} method is called and ends
     * when the {@code unblock} method is called. Each method is only called 
     * once, typically by the {@code TaskService}.</p>
     * 
     * @see Task#getInputBlocker()
     * @see Task#setInputBlocker(com.gsul.application.Task.InputBlocker)
     * @see TaskService
     * @see Action
     */
    public static abstract class InputBlocker extends AbstractBean {
        
    }

}
