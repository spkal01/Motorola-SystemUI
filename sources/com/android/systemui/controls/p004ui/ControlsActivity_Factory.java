package com.android.systemui.controls.p004ui;

import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.controls.ui.ControlsActivity_Factory */
public final class ControlsActivity_Factory implements Factory<ControlsActivity> {
    private final Provider<ControlsUiController> uiControllerProvider;

    public ControlsActivity_Factory(Provider<ControlsUiController> provider) {
        this.uiControllerProvider = provider;
    }

    public ControlsActivity get() {
        return newInstance(this.uiControllerProvider.get());
    }

    public static ControlsActivity_Factory create(Provider<ControlsUiController> provider) {
        return new ControlsActivity_Factory(provider);
    }

    public static ControlsActivity newInstance(ControlsUiController controlsUiController) {
        return new ControlsActivity(controlsUiController);
    }
}
