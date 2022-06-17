package com.android.systemui.controls.p004ui;

import android.util.Log;
import com.android.systemui.controls.p004ui.ControlActionCoordinatorImpl;
import com.android.systemui.plugins.ActivityStarter;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinatorImpl$bouncerOrRun$1 */
/* compiled from: ControlActionCoordinatorImpl.kt */
final class ControlActionCoordinatorImpl$bouncerOrRun$1 implements ActivityStarter.OnDismissAction {
    final /* synthetic */ ControlActionCoordinatorImpl.Action $action;

    ControlActionCoordinatorImpl$bouncerOrRun$1(ControlActionCoordinatorImpl.Action action) {
        this.$action = action;
    }

    public final boolean onDismiss() {
        Log.d("ControlsUiController", "Device unlocked, invoking controls action");
        this.$action.invoke();
        return true;
    }
}
