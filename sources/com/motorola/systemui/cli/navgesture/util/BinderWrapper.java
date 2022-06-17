package com.motorola.systemui.cli.navgesture.util;

import android.os.Binder;
import android.os.IBinder;

public class BinderWrapper<T> extends Binder {
    private T mObject;

    public BinderWrapper(T t) {
        this.mObject = t;
    }

    public T get() {
        return this.mObject;
    }

    public static IBinder with(Object obj) {
        return new BinderWrapper(obj);
    }
}
