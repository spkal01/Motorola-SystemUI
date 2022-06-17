package com.android.systemui;

import dagger.internal.Factory;

public final class ImageWallpaper_Factory implements Factory<ImageWallpaper> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final ImageWallpaper_Factory INSTANCE = new ImageWallpaper_Factory();
    }

    public ImageWallpaper get() {
        return newInstance();
    }

    public static ImageWallpaper_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static ImageWallpaper newInstance() {
        return new ImageWallpaper();
    }
}
