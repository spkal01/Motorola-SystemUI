package com.motorola.systemui.statusbar.policy;

import com.android.systemui.statusbar.policy.CallbackController;

public interface NfcController extends CallbackController<NfcChangeCallback> {

    public interface NfcChangeCallback {
        void onNfcIconVisibleChanged(boolean z) {
        }
    }

    void registerNfcIconObserver(int i);

    void unregisterNfcIconObserver(int i);
}
