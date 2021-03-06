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
 *  Class      :   TaskListener.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 8:23:46 PM
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

import java.util.List;

/**
 * Listener used for observing `Task} execution.A `TaskListener} is particularly
 * useful for monitoring the the intermediate results {@link
 * Task#publish published} by a Task in situations where it's not practical to
 * override the Task's {@link Task#process process} method.Note that if what you
 * really want to do is monitor a Task's state and progress, a
 * PropertyChangeListener is probably more appropriate.
 * <p>
 * The Task class runs all TaskListener methods on the event dispatching thread
 * and the source of all TaskEvents is the Task object.</p>
 *
 * @param <T> the result type returned by this `TaskListener}'s `
 * doInBackground} and `finished} methods.
 * @param <V> the type used for carrying out intermediate results by this `
 * TaskListener}'s `process} method
 *
 * @see Task#addTaskListener
 * @see Task#removeTaskListener
 * @see Task#addPropertyChangeListener
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public interface TaskListener<T, V> {

    /**
     * Called just before the Task's {@link Task#doInBackground
     * doInBackground} method is called, i.e. just before the task begins
     * running. The `event's} source is the Task and its value is null.
     *
     * @param event a TaskEvent whose source is the `Task} object, value is null
     * @see Task#doInBackground
     * @see TaskEvent#getSource
     */
    void doInBackground(TaskEvent<Void> event);

    /**
     * Called each time the Task's {@link Task#process process} method is
     * called. The value of the event is the list of values passed to the
     * process method.
     *
     * @param event a TaskEvent whose source is the `Task} object and whose
     * value is a list of the values passed to the `Task.process()} method
     * @see Task#doInBackground
     * @see Task#process
     * @see TaskEvent#getSource
     * @see TaskEvent#getValue
     */
    void process(TaskEvent<List<V>> event);

    /**
     * Called after the Task's {@link Task#succeeded succeeded} completion
     * method is called. The event's value is the value returned by the Task's
     * `get} method, i.e. the value that is computed by
     * {@link Task#doInBackground}.
     *
     * @param event a TaskEvent whose source is the `Task} object, and whose
     * value is the value returned by `Task.get()}.
     * @see Task#succeeded
     * @see TaskEvent#getSource
     * @see TaskEvent#getValue
     */
    void succeeded(TaskEvent<T> event);

    /**
     * Called after the Task's {@link Task#failed failed} completion method is
     * called. The event's value is the Throwable passed to `Task.failed()}.
     *
     * @param event a TaskEvent whose source is the `Task} object, and whose
     * value is the Throwable passed to `Task.failed()}.
     * @see Task#failed
     * @see TaskEvent#getSource
     * @see TaskEvent#getValue
     */
    void failed(TaskEvent<Throwable> event);

    /**
     * Called after the Task's {@link Task#cancelled cancelled} method is
     * called. The `event's} source is the Task and its value is null.
     *
     * @param event a TaskEvent whose source is the `Task} object, value is null
     * @see Task#cancelled
     * @see Task#get
     * @see TaskEvent#getSource
     */
    void cancelled(TaskEvent<Void> event);

    /**
     * Called after the Task's {@link Task#interrupted interrupted} method is
     * called. The `event's} source is the Task and its value is the
     * InterruptedException passed to `Task.interrupted()}.
     *
     * @param event a TaskEvent whose source is the `Task} object, and whose
     * value is the InterruptedException passed to `Task.interrupted()}.
     * @see Task#interrupted
     * @see TaskEvent#getSource
     * @see TaskEvent#getValue
     */
    void interrupted(TaskEvent<InterruptedException> event);

    /**
     * Called after the Task's {@link Task#finished finished} method is called.
     * The `event's} source is the Task and its value is null.
     *
     * @param event a TaskEvent whose source is the `Task} object, value is
     * null.
     * @see Task#interrupted
     * @see TaskEvent#getSource
     */
    void finished(TaskEvent<Void> event);

    /**
     * Convenience class that stubs all of the TaskListener interface methods.
     * Using TaskListener.Adapter can simplify building TaskListeners.
     * 
     * @param <T>
     * @param <V>
     */
    class Adapter<T, V> implements TaskListener<T, V> {

        @Override
        public void doInBackground(TaskEvent<Void> event) {
        }

        @Override
        public void process(TaskEvent<List<V>> event) {
        }

        @Override
        public void succeeded(TaskEvent<T> event) {
        }

        @Override
        public void failed(TaskEvent<Throwable> event) {
        }

        @Override
        public void cancelled(TaskEvent<Void> event) {
        }

        @Override
        public void interrupted(TaskEvent<InterruptedException> event) {
        }

        @Override
        public void finished(TaskEvent<Void> event) {
        }
    }
}
