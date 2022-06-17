package com.android.systemui.statusbar.phone;

public final /* synthetic */ class StatusBar$$ExternalSyntheticLambda9 implements PanelExpansionListener {
    public final /* synthetic */ StatusBar f$0;

    public /* synthetic */ StatusBar$$ExternalSyntheticLambda9(StatusBar statusBar) {
        this.f$0 = statusBar;
    }

    public final void onPanelExpansionChanged(float f, boolean z) {
        this.f$0.dispatchPanelExpansionForKeyguardDismiss(f, z);
    }
}
