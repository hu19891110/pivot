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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Bounds;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseListener;
import org.apache.pivot.wtk.Dimensions;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.FormAttributeListener;
import org.apache.pivot.wtk.FormListener;
import org.apache.pivot.wtk.HorizontalAlignment;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.Insets;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.Separator;
import org.apache.pivot.wtk.Theme;
import org.apache.pivot.wtk.VerticalAlignment;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.effects.Decorator;
import org.apache.pivot.wtk.media.Image;
import org.apache.pivot.wtk.skin.ContainerSkin;

/**
 * Terra form skin.
 */
public class TerraFormSkin extends ContainerSkin
    implements FormListener, FormAttributeListener {
    private class FieldIdentifierDecorator implements Decorator {
        private Color color;
        private Graphics2D graphics = null;

        public FieldIdentifierDecorator(Color color) {
            this.color = color;
        }

        @Override
        public Graphics2D prepare(Component component, Graphics2D graphics) {
            this.graphics = graphics;
            return graphics;
        }

        @Override
        public void update() {
            GeneralPath arrow = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            arrow.moveTo(FIELD_INDICATOR_OFFSET, 0);
            arrow.lineTo(FIELD_INDICATOR_OFFSET + FIELD_INDICATOR_WIDTH / 2, -FIELD_INDICATOR_HEIGHT);
            arrow.lineTo(FIELD_INDICATOR_OFFSET + FIELD_INDICATOR_WIDTH, 0);
            arrow.closePath();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setStroke(new BasicStroke(0));
            graphics.setPaint(color);

            graphics.draw(arrow);
            graphics.fill(arrow);

            graphics = null;
        }

        @Override
        public Bounds getBounds(Component component) {
            return new Bounds(FIELD_INDICATOR_OFFSET, -FIELD_INDICATOR_HEIGHT,
                FIELD_INDICATOR_WIDTH, FIELD_INDICATOR_HEIGHT);
        }

        @Override
        public AffineTransform getTransform(Component component) {
            return new AffineTransform();
        }
    }


    private ArrayList<Separator> separators = new ArrayList<Separator>();
    private ArrayList<ArrayList<BoxPane>> rowHeaders = new ArrayList<ArrayList<BoxPane>>();
    // TODO private ArrayList<ArrayList<Label>> flagMessages = new ArrayList<ArrayList<Label>>();

    private int horizontalSpacing = 6;
    private int verticalSpacing = 6;
    private int flagImageOffset = 4;
    private boolean fill = false;
    private boolean showFirstSectionHeading = false;
    private boolean showFlagIcons = true;
    private boolean showFlagHighlight = true;
    private boolean showFlagMessagesInline = false;
    private boolean leftAlignLabels = false;
    private String delimiter = DEFAULT_DELIMITER;

    private Window flagMessageWindow = null;

    private ComponentMouseListener fieldMouseListener = new ComponentMouseListener.Adapter() {
        @Override
        public void mouseOver(Component component) {
            Form.Flag flag = Form.getFlag(component);

            if (flag != null) {
                String message = flag.getMessage();
                if (message != null) {
                    MessageType messageType = flag.getMessageType();
                    Label label = new Label(message);
                    label.getStyles().put("padding", new Insets(3, 4, 3, 4));

                    TerraTheme theme = (TerraTheme)Theme.getTheme();

                    Color color = null;
                    Color backgroundColor = null;

                    switch (messageType) {
                        case ERROR: {
                            color = theme.getColor(4);
                            backgroundColor = theme.getColor(22);
                            break;
                        }

                        case WARNING: {
                            color = theme.getColor(1);
                            backgroundColor = theme.getColor(25);
                            break;
                        }

                        case QUESTION: {
                            color = theme.getColor(4);
                            backgroundColor = theme.getColor(16);
                            break;
                        }

                        case INFO: {
                            color = theme.getColor(1);
                            backgroundColor = theme.getColor(10);
                            break;
                        }
                    }

                    label.getStyles().put("color", color);
                    label.getStyles().put("backgroundColor", backgroundColor);

                    flagMessageWindow = new Window(label);
                    flagMessageWindow.getDecorators().add(new FieldIdentifierDecorator(backgroundColor));

                    Point location = component.mapPointToAncestor(component.getDisplay(), 0,
                        component.getHeight());

                    int y = location.y + FIELD_INDICATOR_HEIGHT - 4;
                    if (showFlagHighlight) {
                        y += FLAG_HIGHLIGHT_PADDING;
                    }

                    flagMessageWindow.setLocation(location.x, y);
                    flagMessageWindow.open(component.getWindow());

                    // Set a timer to hide the message
                    scheduledHideFlagMessageCallback =
                        ApplicationContext.scheduleCallback(hideFlagMessageCallback, HIDE_FLAG_MESSAGE_DELAY);
                }
            }
        }

        @Override
        public void mouseOut(Component component) {
            if (flagMessageWindow != null) {
                flagMessageWindow.close();
            }

            if (scheduledHideFlagMessageCallback != null) {
                scheduledHideFlagMessageCallback.cancel();
            }

            flagMessageWindow = null;
            scheduledHideFlagMessageCallback = null;
        }
    };

    private Runnable hideFlagMessageCallback = new Runnable() {
        public void run() {
            if (flagMessageWindow != null) {
                flagMessageWindow.close();
            }

            // TODO Fade the message

            flagMessageWindow = null;
            scheduledHideFlagMessageCallback = null;
        }
    };

    private ApplicationContext.ScheduledCallback scheduledHideFlagMessageCallback = null;

    private static final int FLAG_IMAGE_SIZE = 16;
    private static final int FLAG_HIGHLIGHT_PADDING = 2;
    private static final int FIELD_INDICATOR_WIDTH = 13;
    private static final int FIELD_INDICATOR_HEIGHT = 6;
    private static final int FIELD_INDICATOR_OFFSET = 10;
    private static final int HIDE_FLAG_MESSAGE_DELAY = 5000;

    private static final String DEFAULT_DELIMITER = ":";

    @Override
    public void install(Component component) {
        super.install(component);

        Form form = (Form) component;
        form.getFormListeners().add(this);
        form.getFormAttributeListeners().add(this);

        Form.SectionSequence sections = form.getSections();
        for (int i = 0, n = sections.getLength(); i < n; i++) {
            insertSection(sections.get(i), i);
        }
    }

    @Override
    public int getPreferredWidth(int height) {
        // TODO Respect showFlagMessagesInline

        int preferredWidth = 0;

        int maximumRowHeaderWidth = 0;
        int maximumFieldWidth = 0;
        int maximumSeparatorWidth = 0;

        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        for (int sectionIndex = 0, sectionCount = sections.getLength();
            sectionIndex < sectionCount; sectionIndex++) {
            Form.Section section = sections.get(sectionIndex);

            if (showFirstSectionHeading
                || sectionIndex > 0) {
                Separator separator = separators.get(sectionIndex);
                maximumSeparatorWidth = Math.max(maximumSeparatorWidth,
                    separator.getPreferredWidth());
            }

            for (int fieldIndex = 0, fieldCount = section.getLength();
                fieldIndex < fieldCount; fieldIndex++) {
                Component field = section.get(fieldIndex);

                if (field.isVisible()) {
                    BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
                    maximumRowHeaderWidth = Math.max(maximumRowHeaderWidth,
                        rowHeader.getPreferredWidth(-1));
                    maximumFieldWidth = Math.max(maximumFieldWidth,
                        field.getPreferredWidth(-1));
                }
            }
        }

        preferredWidth = Math.max(maximumRowHeaderWidth + horizontalSpacing + maximumFieldWidth,
            maximumSeparatorWidth);

        if (showFlagHighlight) {
            preferredWidth += FLAG_HIGHLIGHT_PADDING * 2;
        }

        return preferredWidth;
    }

    @Override
    public int getPreferredHeight(int width) {
        // TODO Respect showFlagMessagesInline

        int preferredHeight = 0;

        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        // If justified and constrained, determine field width constraint
        int fieldWidth = -1;

        if (fill && width != -1) {
            int maximumRowHeaderWidth = 0;

            for (int sectionIndex = 0, sectionCount = sections.getLength();
                sectionIndex < sectionCount; sectionIndex++) {
                Form.Section section = sections.get(sectionIndex);

                for (int fieldIndex = 0, fieldCount = section.getLength();
                    fieldIndex < fieldCount; fieldIndex++) {
                    Component field = section.get(fieldIndex);

                    if (field.isVisible()) {
                        BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
                        maximumRowHeaderWidth = Math.max(maximumRowHeaderWidth,
                            rowHeader.getPreferredWidth(-1));
                    }
                }
            }

            fieldWidth = Math.max(0, width - (maximumRowHeaderWidth + horizontalSpacing));

            if (showFlagHighlight) {
                fieldWidth = Math.max(0, fieldWidth - FLAG_HIGHLIGHT_PADDING * 2);
            }
        }

        for (int sectionIndex = 0, sectionCount = sections.getLength();
            sectionIndex < sectionCount; sectionIndex++) {
            Form.Section section = sections.get(sectionIndex);

            if (showFirstSectionHeading
                || sectionIndex > 0) {
                Separator separator = separators.get(sectionIndex);
                preferredHeight += separator.getPreferredHeight(width);
                preferredHeight += verticalSpacing;
            }

            for (int fieldIndex = 0, fieldCount = section.getLength();
                fieldIndex < fieldCount; fieldIndex++) {
                Component field = section.get(fieldIndex);

                if (field.isVisible()) {
                    BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);

                    // Determine the row header size and baseline
                    Dimensions rowHeaderSize = rowHeader.getPreferredSize();
                    int rowHeaderAscent = rowHeader.getBaseline(rowHeaderSize.width,
                        rowHeaderSize.height);
                    if (rowHeaderAscent == -1) {
                        rowHeaderAscent = rowHeaderSize.height;
                    }

                    int rowHeaderDescent = rowHeaderSize.height - rowHeaderAscent;

                    // Determine the field size and baseline
                    Dimensions fieldSize;
                    if (fill
                        && fieldWidth != -1) {
                        fieldSize = new Dimensions(fieldWidth, field.getPreferredHeight(fieldWidth));
                    } else {
                        fieldSize = field.getPreferredSize();
                    }

                    int fieldAscent = field.getBaseline(fieldSize.width, fieldSize.height);
                    if (fieldAscent == -1) {
                        fieldAscent = fieldSize.height;
                    }

                    int fieldDescent = fieldSize.height - fieldAscent;

                    // Determine the baseline and row height
                    int baseline = Math.max(rowHeaderAscent, fieldAscent);
                    int rowHeight = baseline + Math.max(rowHeaderDescent, fieldDescent);

                    preferredHeight += rowHeight;

                    if (fieldIndex > 0) {
                        preferredHeight += verticalSpacing;
                    }
                }
            }
        }

        if (showFlagHighlight) {
            preferredHeight += FLAG_HIGHLIGHT_PADDING * 2;
        }

        return preferredHeight;
    }

    @Override
    public Dimensions getPreferredSize() {
        // TODO Optimize
        return new Dimensions(getPreferredWidth(-1), getPreferredHeight(-1));
    }

    @Override
    public int getBaseline(int width, int height) {
        // TODO Respect showFlagMessagesInline

        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        // Determine the field width
        int fieldWidth = -1;

        if (fill) {
            int maximumRowHeaderWidth = 0;

            for (int sectionIndex = 0, sectionCount = sections.getLength();
                sectionIndex < sectionCount; sectionIndex++) {
                Form.Section section = sections.get(sectionIndex);

                for (int fieldIndex = 0, fieldCount = section.getLength();
                    fieldIndex < fieldCount; fieldIndex++) {
                    Component field = section.get(fieldIndex);

                    if (field.isVisible()) {
                        BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
                        maximumRowHeaderWidth = Math.max(maximumRowHeaderWidth,
                            rowHeader.getPreferredWidth(-1));
                    }
                }
            }

            fieldWidth = Math.max(0, width - (maximumRowHeaderWidth + horizontalSpacing));

            if (showFlagHighlight) {
                fieldWidth = Math.max(0, FLAG_HIGHLIGHT_PADDING * 2);
            }
        }

        int baseline = -1;

        int sectionCount = sections.getLength();
        int sectionIndex = 0;

        int rowY = 0;
        while (sectionIndex < sectionCount
            && baseline == -1) {
            Form.Section section = sections.get(sectionIndex);

            if (showFirstSectionHeading
                || sectionIndex > 0) {
                Separator separator = separators.get(sectionIndex);
                rowY += separator.getPreferredHeight(width);
                rowY += verticalSpacing;
            }

            int fieldCount = section.getLength();
            int fieldIndex = 0;

            while (fieldIndex < fieldCount
                && baseline == -1) {
                Component field = section.get(fieldIndex);

                if (field.isVisible()) {
                    BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);

                    // Determine the row header size and baseline
                    Dimensions rowHeaderSize = rowHeader.getPreferredSize();
                    int rowHeaderAscent = rowHeader.getBaseline(rowHeaderSize.width,
                        rowHeaderSize.height);
                    if (rowHeaderAscent == -1) {
                        rowHeaderAscent = rowHeaderSize.height;
                    }

                    // Determine the field size and baseline
                    Dimensions fieldSize;
                    if (fill && fieldWidth != -1) {
                        fieldSize = new Dimensions(fieldWidth, field.getPreferredHeight(fieldWidth));
                    } else {
                        fieldSize = field.getPreferredSize();
                    }

                    int fieldAscent = field.getBaseline(fieldSize.width, fieldSize.height);
                    if (fieldAscent == -1) {
                        fieldAscent = fieldSize.height;
                    }

                    // Determine the baseline
                    baseline = rowY + Math.max(rowHeaderAscent, fieldAscent);
                }

                fieldIndex++;
            }

            sectionIndex++;
        }

        if (showFlagHighlight) {
            baseline += FLAG_HIGHLIGHT_PADDING;
        }

        return baseline;
    }

    @Override
    public void layout() {
        // TODO Respect showFlagMessagesInline

        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        // Determine the maximum row header width
        int maximumRowHeaderWidth = 0;

        for (int sectionIndex = 0, sectionCount = sections.getLength();
            sectionIndex < sectionCount; sectionIndex++) {
            Form.Section section = sections.get(sectionIndex);

            for (int fieldIndex = 0, fieldCount = section.getLength();
                fieldIndex < fieldCount; fieldIndex++) {
                Component field = section.get(fieldIndex);

                if (field.isVisible()) {
                    BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
                    maximumRowHeaderWidth = Math.max(maximumRowHeaderWidth,
                        rowHeader.getPreferredWidth(-1));
                }
            }
        }

        // Determine the field width
        int width = getWidth();
        int fieldWidth = Math.max(0, width - (maximumRowHeaderWidth + horizontalSpacing));

        if (showFlagHighlight) {
            fieldWidth = Math.max(0, fieldWidth - FLAG_HIGHLIGHT_PADDING * 2);
        }

        // Lay out the components
        int rowX = 0;
        int rowY = 0;

        if (showFlagHighlight) {
            rowX += FLAG_HIGHLIGHT_PADDING;
            rowY += FLAG_HIGHLIGHT_PADDING;
        }

        for (int sectionIndex = 0, sectionCount = sections.getLength();
            sectionIndex < sectionCount; sectionIndex++) {
            Form.Section section = sections.get(sectionIndex);

            Separator separator = separators.get(sectionIndex);
            if (sectionIndex == 0
                && !showFirstSectionHeading) {
                separator.setVisible(false);
            } else {
                separator.setVisible(true);
                separator.setSize(width, separator.getPreferredHeight(width));
                separator.setLocation(rowX, rowY);
                rowY += separator.getHeight();
            }

            for (int fieldIndex = 0, fieldCount = section.getLength();
                fieldIndex < fieldCount; fieldIndex++) {
                Component field = section.get(fieldIndex);

                BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);

                if (field.isVisible()) {
                    // Show the row header
                    rowHeader.setVisible(true);

                    // Determine the row header size and baseline
                    Dimensions rowHeaderSize = new Dimensions(maximumRowHeaderWidth,
                        rowHeader.getPreferredHeight());
                    rowHeader.setSize(rowHeaderSize);
                    int rowHeaderAscent = rowHeader.getBaseline(rowHeaderSize.width,
                        rowHeaderSize.height);
                    if (rowHeaderAscent == -1) {
                        rowHeaderAscent = rowHeaderSize.height;
                    }

                    int rowHeaderDescent = rowHeaderSize.height - rowHeaderAscent;

                    // Determine the field size and baseline
                    Dimensions fieldSize;
                    if (fill) {
                        fieldSize = new Dimensions(fieldWidth, field.getPreferredHeight(fieldWidth));
                    } else {
                        fieldSize = field.getPreferredSize();
                    }

                    field.setSize(fieldSize);

                    int fieldAscent = field.getBaseline(fieldSize.width, fieldSize.height);
                    if (fieldAscent == -1) {
                        fieldAscent = rowHeaderAscent;
                    }

                    int fieldDescent = fieldSize.height - fieldAscent;

                    // Determine the baseline and row height
                    int baseline = Math.max(rowHeaderAscent, fieldAscent);
                    int rowHeight = baseline + Math.max(rowHeaderDescent, fieldDescent);

                    // Position the row header
                    int rowHeaderX = 0;
                    if (showFlagHighlight) {
                        rowHeaderX += FLAG_HIGHLIGHT_PADDING;
                    }

                    int rowHeaderY = rowY + (baseline - rowHeaderAscent);
                    rowHeader.setLocation(rowHeaderX, rowHeaderY);

                    // Position the field
                    int fieldX = maximumRowHeaderWidth + horizontalSpacing;
                    if (showFlagHighlight) {
                        fieldX += FLAG_HIGHLIGHT_PADDING;
                    }

                    int fieldY = rowY + (baseline - fieldAscent);
                    field.setLocation(fieldX, fieldY);

                    // Update the row y-coordinate
                    rowY += rowHeight + verticalSpacing;
                } else {
                    // Hide the row header
                    rowHeader.setVisible(false);
                }
            }
        }
    }

    @Override
    public void paint(Graphics2D graphics) {
        super.paint(graphics);

        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        for (int sectionIndex = 0, sectionCount = sections.getLength();
            sectionIndex < sectionCount; sectionIndex++) {
            Form.Section section = sections.get(sectionIndex);

            for (int fieldIndex = 0, fieldCount = section.getLength();
                fieldIndex < fieldCount; fieldIndex++) {
                Component field = section.get(fieldIndex);

                Form.Flag flag = Form.getFlag(field);
                if (flag != null && showFlagHighlight) {
                    TerraTheme theme = (TerraTheme)Theme.getTheme();
                    MessageType messageType = flag.getMessageType();

                    Color highlightColor = null;

                    switch (messageType) {
                        case ERROR: {
                            highlightColor = theme.getColor(22);
                            break;
                        }

                        case WARNING: {
                            highlightColor = theme.getColor(25);
                            break;
                        }

                        case QUESTION: {
                            highlightColor = theme.getColor(16);
                            break;
                        }
                    }

                    if (highlightColor != null) {
                        Bounds fieldBounds = field.getBounds();

                        graphics.setColor(highlightColor);
                        graphics.setStroke(new BasicStroke(1));
                        graphics.drawRect(fieldBounds.x - FLAG_HIGHLIGHT_PADDING,
                            fieldBounds.y - FLAG_HIGHLIGHT_PADDING,
                            fieldBounds.width + FLAG_HIGHLIGHT_PADDING * 2 - 1,
                            fieldBounds.height + FLAG_HIGHLIGHT_PADDING * 2 - 1);
                    }
                }
            }
        }
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        if (horizontalSpacing < 0) {
            throw new IllegalArgumentException("horizontalSpacing is negative.");
        }

        this.horizontalSpacing = horizontalSpacing;
        invalidateComponent();
    }

    public final void setHorizontalSpacing(Number horizontalSpacing) {
        if (horizontalSpacing == null) {
            throw new IllegalArgumentException("horizontalSpacing is null.");
        }

        setHorizontalSpacing(horizontalSpacing.intValue());
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        if (verticalSpacing < 0) {
            throw new IllegalArgumentException("verticalSpacing is negative.");
        }

        this.verticalSpacing = verticalSpacing;
        invalidateComponent();
    }

    public final void setVerticalSpacing(Number verticalSpacing) {
        if (verticalSpacing == null) {
            throw new IllegalArgumentException("verticalSpacing is null.");
        }

        setVerticalSpacing(verticalSpacing.intValue());
    }

    public int getFlagImageOffset() {
        return flagImageOffset;
    }

    public void setFlagImageOffset(int flagImageOffset) {
        if (flagImageOffset < 0) {
            throw new IllegalArgumentException("flagImageOffset is negative.");
        }

        this.flagImageOffset = flagImageOffset;

        // Set spacing style of existing row headers to flagImageOffset
        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        for (int sectionIndex = 0, sectionCount = sections.getLength();
            sectionIndex < sectionCount; sectionIndex++) {
            Form.Section section = sections.get(sectionIndex);

            for (int fieldIndex = 0, fieldCount = section.getLength();
                fieldIndex < fieldCount; fieldIndex++) {
                BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
                rowHeader.getStyles().put("spacing", flagImageOffset);
            }
        }

        invalidateComponent();
    }

    public final void setFlagImageOffset(Number flagImageOffset) {
        if (flagImageOffset == null) {
            throw new IllegalArgumentException("flagImageOffset is null.");
        }

        setFlagImageOffset(flagImageOffset.intValue());
    }

    public boolean getFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
        invalidateComponent();
    }

    public boolean getShowFirstSectionHeading() {
        return showFirstSectionHeading;
    }

    public void setShowFirstSectionHeading(boolean showFirstSectionHeading) {
        this.showFirstSectionHeading = showFirstSectionHeading;
        invalidateComponent();
    }

    public boolean getShowFlagIcons() {
        return showFlagIcons;
    }

    public void setShowFlagIcons(boolean showFlagIcons) {
        this.showFlagIcons = showFlagIcons;

        // Set visibility of existing flag image views to false
        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        for (int sectionIndex = 0, sectionCount = sections.getLength();
            sectionIndex < sectionCount; sectionIndex++) {
            Form.Section section = sections.get(sectionIndex);

            for (int fieldIndex = 0, fieldCount = section.getLength();
                fieldIndex < fieldCount; fieldIndex++) {
                BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
                ImageView flagImageView = (ImageView)rowHeader.get(0);
                flagImageView.setVisible(showFlagIcons);
            }
        }

        invalidateComponent();
    }

    public boolean getShowFlagHighlight() {
        return showFlagHighlight;
    }

    public void setShowFlagHighlight(boolean showFlagHighlight) {
        this.showFlagHighlight = showFlagHighlight;
        invalidateComponent();
    }

    public boolean getShowFlagMessagesInline() {
        return showFlagMessagesInline;
    }

    public void setShowFlagMessagesInline(boolean showFlagMessagesInline) {
        this.showFlagMessagesInline = showFlagMessagesInline;

        // TODO?

        invalidateComponent();
    }

    public boolean getLeftAlignLabels() {
        return leftAlignLabels;
    }

    public void setLeftAlignLabels(boolean leftAlignLabels) {
        this.leftAlignLabels = leftAlignLabels;

        // Set horizontal alignment style of existing row headers to left or right
        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        for (int sectionIndex = 0, sectionCount = sections.getLength();
            sectionIndex < sectionCount; sectionIndex++) {
            Form.Section section = sections.get(sectionIndex);

            for (int fieldIndex = 0, fieldCount = section.getLength();
                fieldIndex < fieldCount; fieldIndex++) {
                BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
                rowHeader.getStyles().put("horizontalAlignment", leftAlignLabels ?
                    HorizontalAlignment.LEFT : HorizontalAlignment.RIGHT);
            }
        }

        invalidateComponent();
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        if (delimiter == null) {
            throw new IllegalArgumentException("delimiter is null.");
        }

        this.delimiter = delimiter;

        Form form = (Form)getComponent();
        Form.SectionSequence sections = form.getSections();

        for (int i = 0, n = sections.getLength(); i < n; i++) {
            Form.Section section = sections.get(i);

            for (int j = 0, m = section.getLength(); j < m; j++) {
                updateFieldLabel(section, j);
            }
        }

        invalidateComponent();
    }

    // Form events
    @Override
    public void sectionInserted(Form form, int index) {
        insertSection(form.getSections().get(index), index);
    }

    @Override
    public void sectionsRemoved(Form form, int index, Sequence<Form.Section> removed) {
        removeSections(index, removed);
    }

    @Override
    public void sectionHeadingChanged(Form.Section section) {
        updateSectionHeading(section);
    }

    @Override
    public void fieldInserted(Form.Section section, int index) {
        insertField(section, section.get(index), index);
    }

    @Override
    public void fieldsRemoved(Form.Section section, int index, Sequence<Component> fields) {
        Form form = (Form)getComponent();
        removeFields(form.getSections().indexOf(section), index, fields);
    }

    // Form attribute events
    @Override
    public void labelChanged(Form form, Component field, String previousLabel) {
        Form.Section section = Form.getSection(field);
        updateFieldLabel(section, section.indexOf(field));
    }

    @Override
    public void requiredChanged(Form form, Component field) {
        // No-op
    }

    @Override
    public void flagChanged(Form form, Component field, Form.Flag previousFlag) {
        Form.Section section = Form.getSection(field);
        updateFieldFlag(section, section.indexOf(field));
    }

    private void insertSection(Form.Section section, int index) {
        Form form = (Form)getComponent();

        // Insert separator
        Separator separator = new Separator(section.getHeading());
        separators.insert(separator, index);
        form.add(separator);

        // Insert row header list
        ArrayList<BoxPane> sectionRowHeaders = new ArrayList<BoxPane>();
        rowHeaders.insert(sectionRowHeaders, index);

        // TODO Insert flag message list, if inline

        // Insert fields
        for (int i = 0, n = section.getLength(); i < n; i++) {
            insertField(section, section.get(i), i);
        }

        invalidateComponent();
    }

    private void removeSections(int index, Sequence<Form.Section> removed) {
        Form form = (Form)getComponent();
        int count = removed.getLength();

        // Remove fields
        for (int i = 0; i < count; i++) {
            removeFields(index + i, 0, removed.get(i));
        }

        // Remove row header list
        rowHeaders.remove(index, count);

        // TODO Remove flag message list, if inline

        // Remove separators
        Sequence<Separator> removedSeparators = separators.remove(index, count);
        for (int i = 0; i < count; i++) {
            form.remove(removedSeparators.get(i));
        }

        invalidateComponent();
    }

    private void insertField(Form.Section section, Component field, int index) {
        Form form = (Form)getComponent();
        int sectionIndex = form.getSections().indexOf(section);

        // Create the row header
        BoxPane rowHeader = new BoxPane();
        rowHeader.getStyles().put("horizontalAlignment", leftAlignLabels ?
            HorizontalAlignment.LEFT : HorizontalAlignment.RIGHT);
        rowHeader.getStyles().put("verticalAlignment", VerticalAlignment.CENTER);

        ImageView flagImageView = new ImageView();
        flagImageView.setPreferredSize(FLAG_IMAGE_SIZE, FLAG_IMAGE_SIZE);
        flagImageView.setVisible(showFlagIcons);

        rowHeader.add(flagImageView);

        Label label = new Label();
        rowHeader.add(label);

        rowHeaders.get(sectionIndex).insert(rowHeader, index);
        form.add(rowHeader);

        // Add mouse listener
        if (!showFlagMessagesInline) {
            field.getComponentMouseListeners().add(fieldMouseListener);
        }

        // Update the field label and flag
        updateFieldLabel(section, index);
        updateFieldFlag(section, index);

        invalidateComponent();
    }

    private void removeFields(int sectionIndex, int index, Sequence<Component> removed) {
        Form form = (Form)getComponent();
        int count = removed.getLength();

        // Remove the row headers
        Sequence<BoxPane> removedRowHeaders = rowHeaders.get(sectionIndex).remove(index, count);

        for (int i = 0; i < count; i++) {
            form.remove(removedRowHeaders.get(i));

            // Remove mouse listener
            if (!showFlagMessagesInline) {
                Component field = removed.get(i);
                field.getComponentMouseListeners().remove(fieldMouseListener);
            }
        }

        invalidateComponent();
    }

    private void updateSectionHeading(Form.Section section) {
        Form form = (Form)getComponent();
        int sectionIndex = form.getSections().indexOf(section);

        Separator separator = separators.get(sectionIndex);
        separator.setHeading(section.getHeading());
    }

    private void updateFieldLabel(Form.Section section, int fieldIndex) {
        Form form = (Form)getComponent();
        Component field = section.get(fieldIndex);

        int sectionIndex = form.getSections().indexOf(section);
        BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
        Label label = (Label)rowHeader.get(1);
        String labelText = Form.getLabel(field);
        label.setText((labelText == null) ? "" : labelText + delimiter);
    }

    private void updateFieldFlag(Form.Section section, int fieldIndex) {
        Form form = (Form)getComponent();
        Component field = section.get(fieldIndex);

        TerraTheme theme = (TerraTheme)Theme.getTheme();

        int sectionIndex = form.getSections().indexOf(section);
        BoxPane rowHeader = rowHeaders.get(sectionIndex).get(fieldIndex);
        ImageView flagImageView = (ImageView)rowHeader.get(0);

        Form.Flag flag = Form.getFlag(field);
        if (flag == null) {
            flagImageView.setImage((Image)null);
        } else {
            MessageType messageType = flag.getMessageType();
            flagImageView.setImage(theme.getSmallMessageIcon(messageType));
        }

        if (showFlagHighlight) {
            Bounds fieldBounds = field.getBounds();
            repaintComponent(fieldBounds.x - FLAG_HIGHLIGHT_PADDING,
                fieldBounds.y - FLAG_HIGHLIGHT_PADDING,
                fieldBounds.width + FLAG_HIGHLIGHT_PADDING * 2 - 1,
                fieldBounds.height + FLAG_HIGHLIGHT_PADDING * 2 - 1);
        }
    }
}
