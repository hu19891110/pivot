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
package org.apache.pivot.demos.dnd;

import java.io.IOException;

import org.apache.pivot.collections.Map;
import org.apache.pivot.io.FileList;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Clipboard;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.DragSource;
import org.apache.pivot.wtk.DropAction;
import org.apache.pivot.wtk.DropTarget;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.ListView;
import org.apache.pivot.wtk.LocalManifest;
import org.apache.pivot.wtk.Manifest;
import org.apache.pivot.wtk.Point;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Visual;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.media.Image;
import org.apache.pivot.wtkx.WTKX;
import org.apache.pivot.wtkx.WTKXSerializer;

public class DragAndDropDemo implements Application {
    private Window window = null;

    @WTKX private Label label;
    @WTKX private PushButton copyTextButton;
    @WTKX private PushButton pasteTextButton;
    @WTKX private ImageView imageView;
    @WTKX private PushButton copyImageButton;
    @WTKX private PushButton pasteImageButton;
    @WTKX private ListView listView;
    @WTKX private PushButton copyFilesButton;
    @WTKX private PushButton pasteFilesButton;

    @Override
    public void startup(Display display, Map<String, String> properties)
        throws Exception {
        WTKXSerializer wtkxSerializer = new WTKXSerializer();
        window = (Window)wtkxSerializer.readObject(this, "drag_and_drop.wtkx");
        wtkxSerializer.bind(this, DragAndDropDemo.class);

        // Text
        label.setDragSource(new DragSource() {
            private LocalManifest content = null;

            @Override
            public boolean beginDrag(Component component, int x, int y) {
                String text = label.getText();
                if (text != null) {
                    content = new LocalManifest();
                    content.putText(label.getText());
                }

                return (content != null);
            }

            @Override
            public void endDrag(Component component, DropAction dropAction) {
                content = null;
            }

            @Override
            public boolean isNative() {
                return true;
            }

            @Override
            public LocalManifest getContent() {
                return content;
            }

            @Override
            public Visual getRepresentation() {
                return null;
            }

            @Override
            public Point getOffset() {
                return null;
            }

            @Override
            public int getSupportedDropActions() {
                return DropAction.COPY.getMask();
            }
        });

        label.setDropTarget(new DropTarget() {
            @Override
            public DropAction dragEnter(Component component, Manifest dragContent,
                int supportedDropActions, DropAction userDropAction) {
                DropAction dropAction = null;

                if (dragContent.containsText()
                    && DropAction.COPY.isSelected(supportedDropActions)) {
                    dropAction = DropAction.COPY;
                }

                return dropAction;
            }

            @Override
            public void dragExit(Component component) {
            }

            @Override
            public DropAction dragMove(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                return (dragContent.containsText() ? DropAction.COPY : null);
            }

            @Override
            public DropAction userDropActionChange(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                return (dragContent.containsText() ? DropAction.COPY : null);
            }

            @Override
            public DropAction drop(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                DropAction dropAction = null;

                if (dragContent.containsText()) {
                    try {
                        label.setText(dragContent.getText());
                        dropAction = DropAction.COPY;
                    } catch(IOException exception) {
                        System.err.println(exception);
                    }
                }

                dragExit(component);

                return dropAction;
            }
        });

        copyTextButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                String text = label.getText();
                LocalManifest clipboardContent = new LocalManifest();
                clipboardContent.putText(text);
                Clipboard.setContent(clipboardContent);
            }
        });

        pasteTextButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                Manifest clipboardContent = Clipboard.getContent();

                if (clipboardContent != null
                    && clipboardContent.containsText()) {
                    try {
                        label.setText(clipboardContent.getText());
                    } catch(IOException exception) {
                        System.err.println(exception);
                    }
                }
            }
        });

        // Images
        imageView.setDragSource(new DragSource() {
            private LocalManifest content = null;

            @Override
            public boolean beginDrag(Component component, int x, int y) {
                Image image = imageView.getImage();

                if (image != null) {
                    content = new LocalManifest();
                    content.putImage(image);
                }

                return (content != null);
            }

            @Override
            public void endDrag(Component component, DropAction dropAction) {
                content = null;
            }

            @Override
            public boolean isNative() {
                return true;
            }

            @Override
            public LocalManifest getContent() {
                return content;
            }

            @Override
            public Visual getRepresentation() {
                return null;
            }

            @Override
            public Point getOffset() {
                return null;
            }

            @Override
            public int getSupportedDropActions() {
                return DropAction.COPY.getMask();
            }
        });

        imageView.setDropTarget(new DropTarget() {
            @Override
            public DropAction dragEnter(Component component, Manifest dragContent,
                int supportedDropActions, DropAction userDropAction) {
                DropAction dropAction = null;

                if (dragContent.containsImage()
                    && DropAction.COPY.isSelected(supportedDropActions)) {
                    dropAction = DropAction.COPY;
                }

                return dropAction;
            }

            @Override
            public void dragExit(Component component) {
            }

            @Override
            public DropAction dragMove(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                return (dragContent.containsImage() ? DropAction.COPY : null);
            }

            @Override
            public DropAction userDropActionChange(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                return (dragContent.containsImage() ? DropAction.COPY : null);
            }

            @Override
            public DropAction drop(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                DropAction dropAction = null;

                if (dragContent.containsImage()) {
                    try {
                        imageView.setImage(dragContent.getImage());
                        dropAction = DropAction.COPY;
                    } catch(IOException exception) {
                        System.err.println(exception);
                    }
                }

                dragExit(component);

                return dropAction;
            }
        });

        copyImageButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                Image image = imageView.getImage();
                if (image != null) {
                    LocalManifest clipboardContent = new LocalManifest();
                    clipboardContent.putImage(image);
                    Clipboard.setContent(clipboardContent);
                }
            }
        });

        pasteImageButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                Manifest clipboardContent = Clipboard.getContent();

                if (clipboardContent != null
                    && clipboardContent.containsImage()) {
                    try {
                        imageView.setImage(clipboardContent.getImage());
                    } catch(IOException exception) {
                        System.err.println(exception);
                    }
                }
            }
        });

        // Files
        listView.setListData(new FileList());

        listView.setDragSource(new DragSource() {
            private LocalManifest content = null;

            @Override
            public boolean beginDrag(Component component, int x, int y) {
                ListView listView = (ListView)component;
                FileList fileList = (FileList)listView.getListData();

                if (fileList.getLength() > 0) {
                    content = new LocalManifest();
                    content.putFileList(fileList);
                }

                return (content != null);
            }

            @Override
            public void endDrag(Component component, DropAction dropAction) {
                content = null;
            }

            @Override
            public boolean isNative() {
                return true;
            }

            @Override
            public LocalManifest getContent() {
                return content;
            }

            @Override
            public Visual getRepresentation() {
                return null;
            }

            @Override
            public Point getOffset() {
                return null;
            }

            @Override
            public int getSupportedDropActions() {
                return DropAction.COPY.getMask();
            }
        });

        listView.setDropTarget(new DropTarget() {
            @Override
            public DropAction dragEnter(Component component, Manifest dragContent,
                int supportedDropActions, DropAction userDropAction) {
                DropAction dropAction = null;

                if (dragContent.containsFileList()
                    && DropAction.COPY.isSelected(supportedDropActions)) {
                    dropAction = DropAction.COPY;
                }

                return dropAction;
            }

            @Override
            public void dragExit(Component component) {
            }

            @Override
            public DropAction dragMove(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                return (dragContent.containsFileList() ? DropAction.COPY : null);
            }

            @Override
            public DropAction userDropActionChange(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                return (dragContent.containsFileList() ? DropAction.COPY : null);
            }

            @Override
            public DropAction drop(Component component, Manifest dragContent,
                int supportedDropActions, int x, int y, DropAction userDropAction) {
                DropAction dropAction = null;

                if (dragContent.containsFileList()) {
                    try {
                        listView.setListData(dragContent.getFileList());
                        dropAction = DropAction.COPY;
                    } catch(IOException exception) {
                        System.err.println(exception);
                    }
                }

                dragExit(component);

                return dropAction;
            }
        });

        copyFilesButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                // TODO
            }
        });

        pasteFilesButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                // TODO
            }
        });

        window.open(display);
    }

    @Override
    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return false;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    public static void main(String[] args) {
        DesktopApplicationContext.main(DragAndDropDemo.class, args);
    }
}
