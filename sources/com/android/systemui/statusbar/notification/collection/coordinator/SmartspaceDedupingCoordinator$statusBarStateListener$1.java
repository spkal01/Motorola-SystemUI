package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.plugins.statusbar.StatusBarStateController;

/* compiled from: SmartspaceDedupingCoordinator.kt */
public final class SmartspaceDedupingCoordinator$statusBarStateListener$1 implements StatusBarStateController.StateListener {
    final /* synthetic */ SmartspaceDedupingCoordinator this$0;

    SmartspaceDedupingCoordinator$statusBarStateListener$1(SmartspaceDedupingCoordinator smartspaceDedupingCoordinator) {
        this.this$0 = smartspaceDedupingCoordinator;
    }

    public void onStateChanged(int i) {
        this.this$0.recordStatusBarState(i);
    }
}
