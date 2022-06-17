package com.android.systemui;

import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
import com.android.systemui.animation.ActivityLaunchAnimator;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.util.Optional;

public class ActivityStarterDelegate implements ActivityStarter {
    private Optional<Lazy<StatusBar>> mActualStarter;

    public ActivityStarterDelegate(Optional<Lazy<StatusBar>> optional) {
        this.mActualStarter = optional;
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda0(pendingIntent));
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda3(pendingIntent, runnable));
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable, View view) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda4(pendingIntent, runnable, view));
    }

    public void startPendingIntentDismissingKeyguard(PendingIntent pendingIntent, Runnable runnable, ActivityLaunchAnimator.Controller controller) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda5(pendingIntent, runnable, controller));
    }

    public void startActivity(Intent intent, boolean z, boolean z2, int i) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda12(intent, z, z2, i));
    }

    public void startActivity(Intent intent, boolean z) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda8(intent, z));
    }

    public void startActivity(Intent intent, boolean z, ActivityLaunchAnimator.Controller controller) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda9(intent, z, controller));
    }

    public void startActivity(Intent intent, boolean z, boolean z2) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda11(intent, z, z2));
    }

    public void startActivity(Intent intent, boolean z, ActivityStarter.Callback callback) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda10(intent, z, callback));
    }

    public void postStartActivityDismissingKeyguard(Intent intent, int i) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda6(intent, i));
    }

    public void postStartActivityDismissingKeyguard(Intent intent, int i, ActivityLaunchAnimator.Controller controller) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda7(intent, i, controller));
    }

    public void postStartActivityDismissingKeyguard(PendingIntent pendingIntent) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda1(pendingIntent));
    }

    public void postStartActivityDismissingKeyguard(PendingIntent pendingIntent, ActivityLaunchAnimator.Controller controller) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda2(pendingIntent, controller));
    }

    public void postQSRunnableDismissingKeyguard(Runnable runnable) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda14(runnable));
    }

    public void dismissKeyguardThenExecute(ActivityStarter.OnDismissAction onDismissAction, Runnable runnable, boolean z) {
        this.mActualStarter.ifPresent(new ActivityStarterDelegate$$ExternalSyntheticLambda13(onDismissAction, runnable, z));
    }
}
