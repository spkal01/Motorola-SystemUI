package com.android.systemui.screenrecord;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class ScreenRecordDialog_Factory implements Factory<ScreenRecordDialog> {
    private final Provider<RecordingController> controllerProvider;

    public ScreenRecordDialog_Factory(Provider<RecordingController> provider) {
        this.controllerProvider = provider;
    }

    public ScreenRecordDialog get() {
        return newInstance(this.controllerProvider.get());
    }

    public static ScreenRecordDialog_Factory create(Provider<RecordingController> provider) {
        return new ScreenRecordDialog_Factory(provider);
    }

    public static ScreenRecordDialog newInstance(RecordingController recordingController) {
        return new ScreenRecordDialog(recordingController);
    }
}
