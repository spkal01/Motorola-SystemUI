package com.android.systemui.p006qs;

import android.content.Context;
import com.android.systemui.p006qs.AutoAddTracker;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.AutoAddTracker_Builder_Factory */
public final class AutoAddTracker_Builder_Factory implements Factory<AutoAddTracker.Builder> {
    private final Provider<Context> contextProvider;

    public AutoAddTracker_Builder_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public AutoAddTracker.Builder get() {
        return newInstance(this.contextProvider.get());
    }

    public static AutoAddTracker_Builder_Factory create(Provider<Context> provider) {
        return new AutoAddTracker_Builder_Factory(provider);
    }

    public static AutoAddTracker.Builder newInstance(Context context) {
        return new AutoAddTracker.Builder(context);
    }
}
