package com.motorola.systemui.cli.navgesture.animation.remote;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.RecentsAnimationListener;
import java.util.function.Consumer;

public final /* synthetic */ class TaskAnimationManager$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Intent f$0;

    public /* synthetic */ TaskAnimationManager$$ExternalSyntheticLambda0(Intent intent) {
        this.f$0 = intent;
    }

    public final void run() {
        ActivityManagerWrapper.getInstance().startRecentsActivity(this.f$0, SystemClock.currentThreadTimeMillis(), (RecentsAnimationListener) null, (Consumer<Boolean>) null, (Handler) null);
    }
}
