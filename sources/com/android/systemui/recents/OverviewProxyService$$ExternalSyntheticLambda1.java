package com.android.systemui.recents;

import com.android.systemui.model.SysUiState;

public final /* synthetic */ class OverviewProxyService$$ExternalSyntheticLambda1 implements SysUiState.SysUiStateCallback {
    public final /* synthetic */ OverviewProxyService f$0;

    public /* synthetic */ OverviewProxyService$$ExternalSyntheticLambda1(OverviewProxyService overviewProxyService) {
        this.f$0 = overviewProxyService;
    }

    public final void onSystemUiStateChanged(int i) {
        this.f$0.notifySystemUiStateFlags(i);
    }
}
