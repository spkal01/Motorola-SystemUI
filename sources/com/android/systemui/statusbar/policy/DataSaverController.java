package com.android.systemui.statusbar.policy;

public interface DataSaverController extends CallbackController<Listener> {

    public interface Listener {
        void onDataSaverChanged(boolean z);
    }

    boolean dataSaverUnavailable() {
        return false;
    }

    boolean isDataSaverEnabled();

    void setDataSaverEnabled(boolean z);
}
