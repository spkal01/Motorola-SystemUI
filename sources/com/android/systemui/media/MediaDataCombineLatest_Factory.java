package com.android.systemui.media;

import dagger.internal.Factory;

public final class MediaDataCombineLatest_Factory implements Factory<MediaDataCombineLatest> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final MediaDataCombineLatest_Factory INSTANCE = new MediaDataCombineLatest_Factory();
    }

    public MediaDataCombineLatest get() {
        return newInstance();
    }

    public static MediaDataCombineLatest_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static MediaDataCombineLatest newInstance() {
        return new MediaDataCombineLatest();
    }
}
