package com.android.systemui.statusbar.phone;

import com.android.systemui.statusbar.policy.CallbackController;

public interface AudioFxController extends CallbackController<Callback> {

    public interface Callback {
        void onAudioFxChanged();
    }

    boolean isAudioFxAvailable();

    boolean isAudioFxEnabled();
}
