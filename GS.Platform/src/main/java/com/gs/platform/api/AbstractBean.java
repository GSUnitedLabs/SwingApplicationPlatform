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
 *  Class      :   AbstractBean.java
 *  Author     :   Sean Carrick
 *  Created    :   Oct 22, 2021 @ 10:47:16 AM
 *  Modified   :   Oct 22, 2021
 * 
 *  Purpose:     See class JavaDoc comment.
 * 
 *  Revision History:
 * 
 *  WHEN          BY                   REASON
 *  ------------  -------------------  -----------------------------------------
 *  ??? ??, 2006  Hans Muller          Initial creation.
 * *****************************************************************************
 */
package com.gs.platform.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

/**
 * An encapsulation of the PropertyChangeSupport methods based on
 * java.beans.PropertyChangeSupport. PropertyChangeListeners are fired on the
 * Event Dispatching Thread (EDT).
 *
 * @author Hans Muller &lt;HANS.MULLER@SUN.COM&gt;
 *
 * @version 1.0.0
 * @since 1.0.0
 */
class AbstractBean {

    private final PropertyChangeSupport pcs;

    /**
     * Constructs a new `AbstractBean` instance.
     */
    public AbstractBean() {
        pcs = new EDTPropertyChangeSupport(this);
    }

    /**
     * Add a PropertyChangeListener to the listener list. The listener is
     * registered for all properties and its `propertyChange` method will run on
     * the EDT.
     * <p>
     * If `listener` is `null`, no exception is thrown and no action is
     * taken.</p>
     *
     * @param listener the PropertyChangeListener to be added
     *
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     * @see
     * java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property. The listener will
     * be invoked only when a call on `firePropertyChange` names that specific
     * property. The same listener object may be added more than once. For each
     * property, the listener will be invoked the number of times that it was
     * added for that property. If `propertyName` or `listener` is `null`, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName the name of the property on which to listen
     * @param listener the PropertyChangeListener to be added
     *
     * @see
     * java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String,
     * java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a PropertyChangeListener from the listener list.
     * <p>
     * If `listener` is `null`, no exception is thrown and no action is taken.
     *
     * @param listener the PropertyChangeListener to be removed
     *
     * @see #addPropertyChangeListener(java.beans.PropertyChangeListener)
     * @see
     * java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property. If `listener`
     * was added more than once to the same event source for the specified
     * property, it will be notified one less time after being removed. If
     * `propertyName` is `null`, no exception is thrown and no action is taken.
     * If `listener` is `null`, or was never added for the specified property,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName the name of the property on which was listened
     * @param listener the PropertyChangeListener to be removed
     *
     * @see
     * java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String,
     * java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * An array of all of the `PropertyChangeListener`s added so far.
     *
     * @return all of the PropertyChangeListeners that have been added
     *
     * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners()
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    /**
     * Called whenever the value of a bound property is set.
     * <p>
     * If `oldValue` is not equal to `newValue`, invoke `@Action`'s
     * `propertyChange` method on all of the `PropertyChangeListener`s added so
     * far, on the EDT.</p>
     *
     * @param propertyName name of the property being changed
     * @param oldValue the old value (from what it is being changed)
     * @param newValue the new value (to what it is being changed)
     *
     * @see #addPropertyChangeListener(java.lang.String,
     * java.beans.PropertyChangeListener)
     * @see #removePropertyChangeListener(java.lang.String,
     * java.beans.PropertyChangeListener)
     * @see
     * java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String,
     * java.lang.Object, java.lang.Object)
     */
    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }

        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire an existing PropertyChangeEvent.
     *
     * @param e the `PropertyChangeEvent` that caused this to fire
     *
     * @see #addPropertyChangeListener(java.beans.PropertyChangeListener)
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     * @see
     * java.beans.PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent)
     */
    protected void firePropertyChange(PropertyChangeEvent e) {
        pcs.firePropertyChange(e);
    }

    private static class EDTPropertyChangeSupport extends PropertyChangeSupport {

        public EDTPropertyChangeSupport(Object source) {
            super(source);
        }

        /**
         * {@inheritDoc }
         *
         * @param e {@inheritDoc }
         */
        @Override
        public void firePropertyChange(final PropertyChangeEvent e) {
            if (SwingUtilities.isEventDispatchThread()) {
                super.firePropertyChange(e);
            } else {
                Runnable doFirePropertyChange = () -> {
                    firePropertyChange(e);
                };

                SwingUtilities.invokeLater(doFirePropertyChange);
            }
        }

    }

}
