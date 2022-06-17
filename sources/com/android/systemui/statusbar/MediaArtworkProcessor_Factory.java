package com.android.systemui.statusbar;

import dagger.internal.Factory;

public final class MediaArtworkProcessor_Factory implements Factory<MediaArtworkProcessor> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final MediaArtworkProcessor_Factory INSTANCE = new MediaArtworkProcessor_Factory();
    }

    public MediaArtworkProcessor get() {
        return newInstance();
    }

    public static MediaArtworkProcessor_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static MediaArtworkProcessor newInstance() {
        return new MediaArtworkProcessor();
    }
}
