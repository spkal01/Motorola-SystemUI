package com.android.systemui.statusbar.phone;

public final /* synthetic */ class StatusBarWindowController$$ExternalSyntheticLambda0 implements StatusBarContentInsetsChangedListener {
    public final /* synthetic */ StatusBarWindowController f$0;

    public /* synthetic */ StatusBarWindowController$$ExternalSyntheticLambda0(StatusBarWindowController statusBarWindowController) {
        this.f$0 = statusBarWindowController;
    }

    public final void onStatusBarContentInsetsChanged() {
        this.f$0.calculateStatusBarLocationsForAllRotations();
    }
}
