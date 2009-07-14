/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.wtk.skin.terra;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import org.apache.pivot.util.Vote;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseButtonListener;
import org.apache.pivot.wtk.Cursor;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.GraphicsUtilities;
import org.apache.pivot.wtk.Mouse;
import org.apache.pivot.wtk.Rollup;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.effects.Transition;
import org.apache.pivot.wtk.effects.TransitionListener;
import org.apache.pivot.wtk.effects.easing.Easing;
import org.apache.pivot.wtk.effects.easing.Quadratic;
import org.apache.pivot.wtk.skin.ComponentSkin;
import org.apache.pivot.wtk.skin.RollupSkin;

/**
 * Terra rollup skin.
 *
 * @author tvolkert
 * @author gbrown
 */
public class TerraRollupSkin extends RollupSkin {
    /**
     * Expand/collapse transition.
     *
     * @author gbrown
     */
    public class ExpandTransition extends Transition {
        private Easing easing = new Quadratic();

        public ExpandTransition(boolean reversed) {
            super(EXPAND_DURATION, EXPAND_RATE, false, reversed);
        }

        public float getScale() {
            int elapsedTime = getElapsedTime();
            int duration = getDuration();

            float scale;
            if (isReversed()) {
                scale = easing.easeIn(elapsedTime, 0, 1, duration);
            } else {
                scale = easing.easeOut(elapsedTime, 0, 1, duration);
            }

            return scale;
        }

        @Override
        public void start(TransitionListener transitionListener) {
            getComponent().setEnabled(false);
            super.start(transitionListener);
        }

        @Override
        public void stop() {
            getComponent().setEnabled(true);
            super.stop();
        }

        @Override
        protected void update() {
            invalidateComponent();
        }
    }

    /**
     * Component that allows the user to expand and collapse the Rollup.
     *
     * @author tvolkert
     */
    protected class RollupButton extends Component {
        public RollupButton() {
            setSkin(new RollupButtonSkin());
        }
    }

    /**
     * Skin for the rollup button.
     *
     * @author tvolkert
     */
    protected class RollupButtonSkin extends ComponentSkin {
        @Override
        public boolean isFocusable() {
            return false;
        }

        public int getPreferredWidth(int height) {
            return 7;
        }

        public int getPreferredHeight(int width) {
            return 7;
        }

        public Dimensions getPreferredSize() {
            return new Dimensions(7, 7);
        }

        public void layout() {
            // No-op
        }

        public void paint(Graphics2D graphics) {
            Rollup rollup = (Rollup)TerraRollupSkin.this.getComponent();

            graphics.setStroke(new BasicStroke(0));
            graphics.setPaint(buttonColor);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            if (rollup.getContent() == null && useBullet) {
                // Paint the bullet
                RoundRectangle2D.Double shape = new RoundRectangle2D.Double(1, 1, 4, 4, 2, 2);
                graphics.draw(shape);
                graphics.fill(shape);
            } else if (rollup.isExpanded()) {
                // Paint the collapse image
                int[] xPoints = {0, 3, 6};
                int[] yPoints = {0, 6, 0};
                graphics.fillPolygon(xPoints, yPoints, 3);
                graphics.drawPolygon(xPoints, yPoints, 3);
            } else {
                // Paint the expand image
                int[] xPoints = {0, 6, 0};
                int[] yPoints = {0, 3, 6};
                graphics.fillPolygon(xPoints, yPoints, 3);
                graphics.drawPolygon(xPoints, yPoints, 3);
            }
        }

        @Override
        public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
            Rollup rollup = (Rollup)TerraRollupSkin.this.getComponent();
            rollup.setExpanded(!rollup.isExpanded());
            return true;
        }
    }

    private RollupButton rollupButton = null;

    // Styles
    private Color buttonColor;
    private int spacing;
    private int buffer;
    private boolean fill;
    private boolean headingToggles;
    private boolean useBullet;

    private ExpandTransition expandTransition = null;

    private ComponentMouseButtonListener headingMouseButtonListener = new ComponentMouseButtonListener.Adapter() {
        public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
            boolean consumed = false;

            if (headingToggles) {
                Rollup rollup = (Rollup)getComponent();
                rollup.setExpanded(!rollup.isExpanded());
                consumed = true;
            }

            return consumed;
        }
    };

    private static final int EXPAND_DURATION = 250;
    private static final int EXPAND_RATE = 30;

    public TerraRollupSkin() {
        TerraTheme theme = (TerraTheme)Theme.getTheme();

        buttonColor = theme.getColor(9);
        spacing = 4;
        buffer = 4;
        fill = false;
        headingToggles = true;
        useBullet = false;
    }

    @Override
    public void install(Component component) {
        super.install(component);

        Rollup rollup = (Rollup)component;

        // Add the rollup button
        rollupButton = new RollupButton();
        rollup.add(rollupButton);

        // Initialize state
        headingChanged(rollup, null);
        contentChanged(rollup, null);
    }

    @Override
    public void uninstall() {
        Rollup rollup = (Rollup)getComponent();

        // Uninitialize state
        Component heading = rollup.getHeading();
        if (heading != null) {
            heading.getComponentMouseButtonListeners().remove(headingMouseButtonListener);
        }

        // Remove the rollup button
        rollup.remove(rollupButton);
        rollupButton = null;

        super.uninstall();
    }

    @Override
    public int getPreferredWidth(int height) {
        Rollup rollup = (Rollup)getComponent();

        Component heading = rollup.getHeading();
        Component content = rollup.getContent();

        int preferredWidth = 0;

        if (heading != null) {
            preferredWidth = heading.getPreferredWidth(-1);
        }

        if (content != null
            && (rollup.isExpanded()
                || (expandTransition != null
                    && !expandTransition.isReversed()))) {
            preferredWidth = Math.max(preferredWidth, content.getPreferredWidth(-1));
        }

        preferredWidth += rollupButton.getPreferredWidth(-1) + buffer;

        return preferredWidth;
    }

    @Override
    public int getPreferredHeight(int width) {
        Rollup rollup = (Rollup)getComponent();

        Component heading = rollup.getHeading();
        Component content = rollup.getContent();

        int preferredHeight = 0;

        // Calculate our internal width constraint
        if (fill && width >= 0) {
            width = Math.max(width - rollupButton.getPreferredWidth(-1) - buffer, 0);
        } else {
            width = -1;
        }

        if (heading != null) {
            preferredHeight += heading.getPreferredHeight(width);
        }

        if (content != null) {
            if (expandTransition == null) {
                if (rollup.isExpanded()) {
                    preferredHeight += spacing + content.getPreferredHeight(width);
                }
            } else {
                float scale = expandTransition.getScale();
                preferredHeight += (int)(scale * (spacing + content.getPreferredHeight(width)));
            }
        }

        preferredHeight = Math.max(preferredHeight, rollupButton.getPreferredHeight(-1));

        return preferredHeight;
    }

    public void layout() {
        Rollup rollup = (Rollup)getComponent();

        Component heading = rollup.getHeading();
        Component content = rollup.getContent();

        Dimensions rollupButtonSize = rollupButton.getPreferredSize();
        rollupButton.setSize(rollupButtonSize.width, rollupButtonSize.height);

        int x = rollupButtonSize.width + buffer;
        int y = 0;
        int justifiedWidth = Math.max(getWidth() - rollupButtonSize.width - buffer, 0);

        if (heading != null) {
            int headingWidth, headingHeight;
            if (fill) {
                headingWidth = justifiedWidth;
                headingHeight = heading.getPreferredHeight(headingWidth);
            } else {
                Dimensions headingPreferredSize = heading.getPreferredSize();
                headingWidth = headingPreferredSize.width;
                headingHeight = headingPreferredSize.height;
            }

            heading.setVisible(true);
            heading.setLocation(x, y);
            heading.setSize(headingWidth, headingHeight);

            y += headingHeight + spacing;
        }

        if (content != null) {
            if (rollup.isExpanded()
                || (expandTransition != null
                    && !expandTransition.isReversed())) {
                int contentWidth, contentHeight;
                if (fill) {
                    contentWidth = justifiedWidth;
                    contentHeight = content.getPreferredHeight(contentWidth);
                } else {
                    Dimensions contentPreferredSize = content.getPreferredSize();
                    contentWidth = contentPreferredSize.width;
                    contentHeight = contentPreferredSize.height;
                }

                content.setVisible(true);
                content.setLocation(x, y);
                content.setSize(contentWidth, contentHeight);
            } else {
                content.setVisible(false);
            }
        }

        y = (heading == null ? 0 : (heading.getHeight() - rollupButtonSize.height) / 2 + 1);

        rollupButton.setLocation(0, y);
    }

    public Color getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(Color buttonColor) {
        if (buttonColor == null) {
            throw new IllegalArgumentException("buttonColor is null.");
        }

        this.buttonColor = buttonColor;
        rollupButton.repaint();
    }

    public final void setButtonColor(String buttonColor) {
        if (buttonColor == null) {
            throw new IllegalArgumentException("buttonColor is null.");
        }

        setButtonColor(GraphicsUtilities.decodeColor(buttonColor));
    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;

        Rollup rollup = (Rollup)getComponent();
        if (rollup.isExpanded()) {
            invalidateComponent();
        }
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
        invalidateComponent();
    }

    public boolean getFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
        invalidateComponent();
    }

    public boolean getHeadingToggles() {
        return headingToggles;
    }

    public void setHeadingToggles(boolean headingToggles) {
        this.headingToggles = headingToggles;
    }

    public boolean getUseBullet() {
        return useBullet;
    }

    public void setUseBullet(boolean useBullet) {
        this.useBullet = useBullet;

        Rollup rollup = (Rollup)getComponent();
        if (rollup.getContent() == null) {
            rollupButton.repaint();
        }
    }

    // RollupListener methods

    @Override
    public void headingChanged(Rollup rollup, Component previousHeading) {
        if (previousHeading != null) {
            previousHeading.getComponentMouseButtonListeners().remove(headingMouseButtonListener);
        }

        Component heading = rollup.getHeading();

        if (heading != null) {
            heading.getComponentMouseButtonListeners().add(headingMouseButtonListener);
        }

        invalidateComponent();
    }

    @Override
    public void contentChanged(Rollup rollup, Component previousContent) {
        if (rollup.getContent() == null && useBullet) {
            rollupButton.setCursor(Cursor.DEFAULT);
        } else {
            rollupButton.setCursor(Cursor.HAND);
        }

        rollupButton.repaint();

        if (rollup.isExpanded()) {
            invalidateComponent();
        }
    }

    // Rollup state events
    @Override
    public Vote previewExpandedChange(final Rollup rollup) {
        Vote vote;

        if (rollup.isShowing()
            && expandTransition == null
            && rollup.getContent() != null) {
            final boolean expanded = rollup.isExpanded();
            expandTransition = new ExpandTransition(expanded);

            expandTransition.start(new TransitionListener() {
                public void transitionCompleted(Transition transition) {
                    rollup.setExpanded(!expanded);
                    expandTransition = null;
                }
            });
        }

        if (expandTransition == null
            || !expandTransition.isRunning()) {
            vote = Vote.APPROVE;
        } else {
            vote = Vote.DEFER;
        }

        return vote;
    }

    @Override
    public void expandedChangeVetoed(Rollup rollup, Vote reason) {
        if (reason == Vote.DENY
            && expandTransition != null) {
            // NOTE We stop, rather than end, the transition so the completion
            // event isn't fired; if the event fires, the listener will set
            // the expanded state
            expandTransition.stop();
            expandTransition = null;

            invalidateComponent();
        }
    }

    @Override
    public void expandedChanged(final Rollup rollup) {
        invalidateComponent();
    }
}
