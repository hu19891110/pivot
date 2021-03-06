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
package org.apache.pivot.wtk;

import java.net.URISyntaxException;
import java.net.URL;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.HashMap;
import org.apache.pivot.json.JSON;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.util.ThreadUtilities;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.media.Image;

/**
 * Component that displays an image.
 */
public class ImageView extends Component {
    private static class ImageViewListenerList extends ListenerList<ImageViewListener>
        implements ImageViewListener {
        @Override
        public void imageChanged(ImageView imageView, Image previousImage) {
            for (ImageViewListener listener : this) {
                listener.imageChanged(imageView, previousImage);
            }
        }

        @Override
        public void asynchronousChanged(ImageView imageView) {
            for (ImageViewListener listener : this) {
                listener.asynchronousChanged(imageView);
            }
        }

        @Override
        public void imageKeyChanged(ImageView imageView, String previousImageKey) {
            for (ImageViewListener listener : this) {
                listener.imageKeyChanged(imageView, previousImageKey);
            }
        }
    }

    private Image image = null;
    private boolean asynchronous = false;
    private String imageKey = null;

    private ImageViewListenerList imageViewListeners = new ImageViewListenerList();

    // Maintains a mapping of image URL to image views that should be notified when
    // an asynchronously loaded image is available
    private static HashMap<java.net.URI, ArrayList<ImageView>> loadMap =
        new HashMap<java.net.URI, ArrayList<ImageView>>();

    /**
     * Creates an empty image view.
     */
    public ImageView() {
        this(null);
    }

    /**
     * Creates an image view with the given image.
     *
     * @param image
     * The initial image to set, or <tt>null</tt> for no image.
     */
    public ImageView(Image image) {
        setImage(image);

        installThemeSkin(ImageView.class);
    }

    /**
     * Returns the image view's current image.
     *
     * @return
     * The current image, or <tt>null</tt> if no image is set.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the image view's current image.
     *
     * @param image
     * The image to set, or <tt>null</tt> for no image.
     */
    public void setImage(Image image) {
        Image previousImage = this.image;

        if (previousImage != image) {
            this.image = image;
            imageViewListeners.imageChanged(this, previousImage);
        }
    }

    /**
     * Sets the image view's current image by URL.
     * <p>
     * <b>Note</b>: Using this signature will cause an entry to be added in the
     * application context's {@linkplain ApplicationContext#getResourceCache()
     * resource cache} if one does not already exist.
     *
     * @param imageURL
     * The location of the image to set.
     */
    public final void setImage(final URL imageURL) {
        if (imageURL == null) {
            throw new IllegalArgumentException("imageURL is null.");
        }

        Image image = (Image)ApplicationContext.getResourceCache().get(imageURL);

        if (image == null) {
            // Convert to URI because using a URL as a key causes performance problems
            final java.net.URI imageURI;
            try {
                imageURI = imageURL.toURI();
            } catch (URISyntaxException exception) {
                throw new RuntimeException(exception);
            }

            if (asynchronous) {
                if (loadMap.containsKey(imageURI)) {
                    // Add this to the list of image views that are interested in
                    // the image at this URL
                    loadMap.get(imageURI).add(this);
                } else {
                    Image.load(imageURL, new TaskAdapter<Image>(new TaskListener<Image>() {
                        @Override
                        public void taskExecuted(Task<Image> task) {
                            Image image = task.getResult();

                            // Update the contents of all image views that requested this
                            // image
                            for (ImageView imageView : loadMap.get(imageURI)) {
                                imageView.setImage(image);
                            }

                            loadMap.remove(imageURI);

                            // Add the image to the cache
                            ApplicationContext.getResourceCache().put(imageURL, image);
                        }

                        @Override
                        public void executeFailed(Task<Image> task) {
                            // No-op
                        }
                    }));

                    loadMap.put(imageURI, new ArrayList<ImageView>(this));
                }
            } else {
                try {
                    image = Image.load(imageURL);
                } catch (TaskExecutionException exception) {
                    throw new IllegalArgumentException(exception);
                }

                ApplicationContext.getResourceCache().put(imageURL, image);
            }
        }

        setImage(image);
    }

    /**
     * Sets the image view's image by {@linkplain ClassLoader#getResource(String)
     * resource name}.
     * <p>
     * <b>Note</b>: Using this signature will cause an entry to be added in the
     * application context's {@linkplain ApplicationContext#getResourceCache()
     * resource cache} if one does not already exist.
     *
     * @param image
     * The resource name of the image to set.
     */
    public final void setImage(String image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null.");
        }

        ClassLoader classLoader = ThreadUtilities.getClassLoader();
        setImage(classLoader.getResource(image));
    }

    /**
     * Returns the image view's asynchronous flag.
     *
     * @return
     * <tt>true</tt> if images specified via URL will be loaded in the background;
     * <tt>false</tt> if they will be loaded synchronously.
     */
    public boolean isAsynchronous() {
        return asynchronous;
    }

    /**
     * Sets the image view's asynchronous flag.
     *
     * @param asynchronous
     * <tt>true</tt> if images specified via URL will be loaded in the background;
     * <tt>false</tt> if they will be loaded synchronously.
     */
    public void setAsynchronous(boolean asynchronous) {
        if (this.asynchronous != asynchronous) {
            this.asynchronous = asynchronous;
            imageViewListeners.asynchronousChanged(this);
        }
    }

    /**
     * Returns the image view's image key.
     *
     * @return
     * The image key, or <tt>null</tt> if no key is set.
     */
    public String getImageKey() {
        return imageKey;
    }

    /**
     * Sets the image view's image key.
     *
     * @param imageKey
     * The image key, or <tt>null</tt> to clear the binding.
     */
    public void setImageKey(String imageKey) {
        String previousImageKey = this.imageKey;

        if (previousImageKey != imageKey) {
            this.imageKey = imageKey;
            imageViewListeners.imageKeyChanged(this, previousImageKey);
        }
    }

    @Override
    public void load(Object context) {
        if (imageKey != null
            && JSON.containsKey(context, imageKey)) {
            Object value = JSON.get(context, imageKey);
            if (value instanceof Image) {
                setImage((Image)value);
            } else if (value instanceof URL) {
                setImage((URL)value);
            } else if (value instanceof String) {
                setImage((String)value);
            } else {
                throw new IllegalArgumentException(getClass().getName() + " can't bind to "
                    + value + ".");
            }
        }
    }

    @Override
    public void store(Object context) {
        if (isEnabled()
            && imageKey != null) {
            JSON.put(context, imageKey, getImage());
        }
    }

    /**
     * Returns the image view listener list.
     *
     * @return
     * The image view listener list.
     */
    public ListenerList<ImageViewListener> getImageViewListeners() {
        return imageViewListeners;
    }
}
