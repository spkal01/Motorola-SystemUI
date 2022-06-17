package com.android.systemui.media;

import com.android.systemui.media.MediaHost;
import dagger.internal.Factory;

public final class MediaHost_MediaHostStateHolder_Factory implements Factory<MediaHost.MediaHostStateHolder> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final MediaHost_MediaHostStateHolder_Factory INSTANCE = new MediaHost_MediaHostStateHolder_Factory();
    }

    public MediaHost.MediaHostStateHolder get() {
        return newInstance();
    }

    public static MediaHost_MediaHostStateHolder_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static MediaHost.MediaHostStateHolder newInstance() {
        return new MediaHost.MediaHostStateHolder();
    }
}
