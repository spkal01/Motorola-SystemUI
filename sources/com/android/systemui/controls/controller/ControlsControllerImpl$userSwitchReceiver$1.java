package com.android.systemui.controls.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: ControlsControllerImpl.kt */
public final class ControlsControllerImpl$userSwitchReceiver$1 extends BroadcastReceiver {
    final /* synthetic */ ControlsControllerImpl this$0;

    ControlsControllerImpl$userSwitchReceiver$1(ControlsControllerImpl controlsControllerImpl) {
        this.this$0 = controlsControllerImpl;
    }

    public void onReceive(@NotNull Context context, @NotNull Intent intent) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(intent, "intent");
        if (Intrinsics.areEqual((Object) intent.getAction(), (Object) "android.intent.action.USER_SWITCHED")) {
            this.this$0.userChanging = true;
            UserHandle of = UserHandle.of(intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId()));
            if (Intrinsics.areEqual((Object) this.this$0.currentUser, (Object) of)) {
                this.this$0.userChanging = false;
                return;
            }
            ControlsControllerImpl controlsControllerImpl = this.this$0;
            Intrinsics.checkNotNullExpressionValue(of, "newUser");
            controlsControllerImpl.setValuesForUser(of);
        }
    }
}
