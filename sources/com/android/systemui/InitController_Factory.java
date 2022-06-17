package com.android.systemui;

import dagger.internal.Factory;

public final class InitController_Factory implements Factory<InitController> {

    private static final class InstanceHolder {
        /* access modifiers changed from: private */
        public static final InitController_Factory INSTANCE = new InitController_Factory();
    }

    public InitController get() {
        return newInstance();
    }

    public static InitController_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static InitController newInstance() {
        return new InitController();
    }
}
