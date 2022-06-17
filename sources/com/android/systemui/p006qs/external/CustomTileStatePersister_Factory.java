package com.android.systemui.p006qs.external;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.external.CustomTileStatePersister_Factory */
public final class CustomTileStatePersister_Factory implements Factory<CustomTileStatePersister> {
    private final Provider<Context> contextProvider;

    public CustomTileStatePersister_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public CustomTileStatePersister get() {
        return newInstance(this.contextProvider.get());
    }

    public static CustomTileStatePersister_Factory create(Provider<Context> provider) {
        return new CustomTileStatePersister_Factory(provider);
    }

    public static CustomTileStatePersister newInstance(Context context) {
        return new CustomTileStatePersister(context);
    }
}
