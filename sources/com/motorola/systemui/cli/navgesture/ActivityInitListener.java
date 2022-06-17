package com.motorola.systemui.cli.navgesture;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import com.motorola.systemui.cli.navgesture.animation.remote.RemoteAnimationProvider;
import com.motorola.systemui.cli.navgesture.display.SecondaryDisplay;
import com.motorola.systemui.cli.navgesture.util.ActivityTracker;
import java.util.function.BiPredicate;

public class ActivityInitListener<T extends BaseGestureActivity> implements ActivityTracker.SchedulerCallback<T> {
    private final ActivityTracker<T> mActivityTracker;
    private boolean mIsRegistered = false;
    private BiPredicate<T, Boolean> mOnInitListener;

    public ActivityInitListener(BiPredicate<T, Boolean> biPredicate, ActivityTracker<T> activityTracker) {
        this.mOnInitListener = biPredicate;
        this.mActivityTracker = activityTracker;
    }

    public final boolean init(T t, boolean z) {
        if (!this.mIsRegistered) {
            return false;
        }
        return handleInit(t, z);
    }

    /* access modifiers changed from: protected */
    public boolean handleInit(T t, boolean z) {
        return this.mOnInitListener.test(t, Boolean.valueOf(z));
    }

    public void register(Intent intent) {
        this.mIsRegistered = true;
        this.mActivityTracker.runCallbackWhenActivityExists(this, intent);
    }

    public void unregister() {
        this.mIsRegistered = false;
        this.mOnInitListener = null;
    }

    public void registerAndStartActivity(Intent intent, RemoteAnimationProvider remoteAnimationProvider, Context context, Handler handler, long j) {
        this.mIsRegistered = true;
        ActivityOptions activityOptions = remoteAnimationProvider.toActivityOptions(handler, j);
        activityOptions.setLaunchDisplayId(SecondaryDisplay.INSTANCE.lambda$get$0(context).getDisplayId());
        Bundle bundle = activityOptions.toBundle();
        intent.addFlags(268435456);
        context.startActivity(addToIntent(new Intent(intent)), bundle);
    }
}
