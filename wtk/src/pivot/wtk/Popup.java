/*
 * Copyright (c) 2008 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pivot.wtk;

import pivot.util.ListenerList;

/**
 * Window class representing a "popup" window. A popup is an auxiliary window
 * that is optionally associated with an "affiliate" component. A popup closes
 * automatically when:
 *
 * <ul>
 * <li>A component mouse down event or a container mouse wheel event occurs
 * outside the bounds of its affiliate, itself, or any of its owned
 * windows.</li>
 * <li>The absolute location of its affiliate component changes.</li>
 * <li>The absolute visibility of its affiliate component (the affiliate's
 * "showing" state) changes.</li>
 * <li>The affiliate's ancestry changes.</li>
 * </ul>
 *
 * @author gbrown
 */
public class Popup extends Window {
    private static class PopupListenerList extends ListenerList<PopupListener>
        implements PopupListener {
        public void affiliateChanged(Popup popup, Component previousAffiliate) {
            for (PopupListener listener : this) {
                listener.affiliateChanged(popup, previousAffiliate);
            }
        }
    }

    private Component affiliate = null;

    private PopupListenerList popupListeners = new PopupListenerList();

    /**
     * Creates a new popup.
     */
    public Popup() {
        this(null);
    }

    /**
     * Creates a new popup with an initial content component.
     *
     * @param content
     * The popup's content component.
     */
    public Popup(Component content) {
        super(content);

        installSkin(Popup.class);
    }

    /**
     * Returns the popup's affiliate component.
     *
     * @return
     * The component with which this popup is affiliated, or <tt>null</tt> if
     * the popup has no affiliate.
     */
    public Component getAffiliate() {
        return affiliate;
    }

    /**
     * Sets the popup's affiliate component.
     *
     * @param affiliate
     */
    public void setAffiliate(Component affiliate) {
        Component previousAffiliate = this.affiliate;

        if (previousAffiliate != affiliate) {
            this.affiliate = affiliate;
            popupListeners.affiliateChanged(this, previousAffiliate);
        }
    }

    /**
     * @return
     * <tt>true</tt>; by default, popups are auxilliary windows.
     */
    @Override
    public boolean isAuxilliary() {
        return true;
    }

    /**
     * Opens the popup.
     *
     * @param affiliate
     * The component with which the popup is affiliated.
     */
    public void open(Component affiliate) {
        if (affiliate == null) {
            throw new IllegalArgumentException("affiliate is null.");
        }

        if (isOpen()
            && getAffiliate() != affiliate) {
            throw new IllegalStateException("Popup is already open with a different affiliate.");
        }

        setAffiliate(affiliate);
        open(affiliate.getWindow());
    }

    public ListenerList<PopupListener> getPopupListeners() {
        return popupListeners;
    }

    public void setPopupListener(PopupListener listener) {
        popupListeners.add(listener);
    }
}
