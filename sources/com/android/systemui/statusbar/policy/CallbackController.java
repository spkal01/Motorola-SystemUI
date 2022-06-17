package com.android.systemui.statusbar.policy;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public interface CallbackController<T> {
    void addCallback(T t);

    void removeCallback(T t);

    T observe(LifecycleOwner lifecycleOwner, T t) {
        return observe(lifecycleOwner.getLifecycle(), t);
    }

    T observe(Lifecycle lifecycle, T t) {
        lifecycle.addObserver(new CallbackController$$ExternalSyntheticLambda0(this, t));
        return t;
    }

    /* access modifiers changed from: private */
    /* synthetic */ void lambda$observe$0(Object obj, LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            addCallback(obj);
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            removeCallback(obj);
        }
    }
}
