package com.motorola.systemui.cli.navgesture.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import com.motorola.systemui.cli.navgesture.BaseGestureActivity;
import java.io.Serializable;
import java.lang.ref.WeakReference;

public final class ActivityTracker<T extends BaseGestureActivity> {
    private WeakReference<T> mCurrentActivity = new WeakReference<>((Object) null);

    public <R extends T> R getCreatedActivity() {
        return (BaseGestureActivity) this.mCurrentActivity.get();
    }

    public void onActivityDestroyed(T t) {
        if (this.mCurrentActivity.get() == t) {
            this.mCurrentActivity.clear();
        }
    }

    public void runCallbackWhenActivityExists(SchedulerCallback<T> schedulerCallback, Intent intent) {
        BaseGestureActivity baseGestureActivity = (BaseGestureActivity) this.mCurrentActivity.get();
        if (baseGestureActivity != null) {
            schedulerCallback.init(baseGestureActivity, baseGestureActivity.isStarted());
        } else {
            schedulerCallback.addToIntent(intent);
        }
    }

    public boolean handleCreate(T t) {
        this.mCurrentActivity = new WeakReference<>(t);
        return handleIntent(t, t.getIntent(), false);
    }

    public boolean handleNewIntent(T t, Intent intent) {
        return handleIntent(t, intent, t.isStarted());
    }

    private boolean handleIntent(T t, Intent intent, boolean z) {
        if (intent == null || intent.getExtras() == null) {
            return false;
        }
        IBinder binder = intent.getExtras().getBinder("launcher.scheduler_callback");
        if (!(binder instanceof BinderWrapper)) {
            return false;
        }
        if (((SchedulerCallback) ((BinderWrapper) binder).get()).init(t, z)) {
            return true;
        }
        intent.getExtras().remove("launcher.scheduler_callback");
        return true;
    }

    public interface SchedulerCallback<T extends BaseGestureActivity> extends Serializable {
        boolean init(T t, boolean z);

        Intent addToIntent(Intent intent) {
            Bundle bundle = new Bundle();
            bundle.putBinder("launcher.scheduler_callback", BinderWrapper.with(this));
            intent.putExtras(bundle);
            return intent;
        }
    }
}
