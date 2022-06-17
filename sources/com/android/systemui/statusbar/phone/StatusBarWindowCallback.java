package com.android.systemui.statusbar.phone;

public interface StatusBarWindowCallback {
    void onStateChanged(boolean z, boolean z2, boolean z3);

    void onStateChangedForCli(boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
    }

    void onViewStateChanged(boolean z) {
    }
}
