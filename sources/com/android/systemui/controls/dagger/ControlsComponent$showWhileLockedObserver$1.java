package com.android.systemui.controls.dagger;

import android.database.ContentObserver;
import android.os.Handler;

/* compiled from: ControlsComponent.kt */
public final class ControlsComponent$showWhileLockedObserver$1 extends ContentObserver {
    final /* synthetic */ ControlsComponent this$0;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    ControlsComponent$showWhileLockedObserver$1(ControlsComponent controlsComponent) {
        super((Handler) null);
        this.this$0 = controlsComponent;
    }

    public void onChange(boolean z) {
        this.this$0.updateShowWhileLocked();
    }
}
